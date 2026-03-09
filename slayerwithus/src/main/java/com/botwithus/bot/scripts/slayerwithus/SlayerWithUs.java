package com.botwithus.bot.scripts.slayerwithus;

import com.botwithus.bot.api.Client;
import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.ScriptContext;
import com.botwithus.bot.api.ScriptManifest;
import com.botwithus.bot.api.event.ChatMessageEvent;
import com.botwithus.bot.scripts.slayerwithus.combat.SlayerCombatHandler;
import com.botwithus.bot.scripts.slayerwithus.config.SlayerCombatProfile;
import com.botwithus.bot.scripts.slayerwithus.config.SlayerConfiguration;
import com.botwithus.bot.scripts.slayerwithus.config.SlayerSettings;
import com.botwithus.bot.scripts.slayerwithus.config.SlayerShareCodec;
import com.botwithus.bot.scripts.slayerwithus.controller.SlayerController;
import com.botwithus.bot.scripts.slayerwithus.loot.SlayerLootHandler;
import com.botwithus.bot.scripts.slayerwithus.master.SlayerMaster;
import com.botwithus.bot.scripts.slayerwithus.master.SlayerMasterHandler;
import com.botwithus.bot.scripts.slayerwithus.navigation.SlayerNavigationHandler;
import com.botwithus.bot.scripts.slayerwithus.preparation.SlayerPreparationHandler;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTask;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskLocation;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskLocationRegistry;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskMonsterRegistry;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskSnapshot;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskTracker;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.xapi.script.base.DelayableScript;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@ScriptManifest(
        name = "SlayerWithUs",
        version = "1.0",
        author = "BotWithUs",
        description = "Backend-only SlayerWithUs port for the new BotWithUs framework"
)
public class SlayerWithUs extends DelayableScript {
    private static final long LOOP_LOG_INTERVAL_MS = 5_000L;

    private SlayerConfiguration configuration;
    private SlayerSettings settings;
    private SlayerTaskTracker taskTracker;
    private SlayerNavigationHandler navigationHandler;
    private SlayerController controller;
    private final Map<String, SlayerTaskTracker> clientTrackers = new LinkedHashMap<>();
    private String status = "Initializing";
    private Consumer<ChatMessageEvent> chatListener;
    private boolean postTaskBankingPending;
    private boolean lootBankingPending;
    private long lastLoopLogAt;

    @Override
    protected void onInitialize() {
        ScriptContext context = context();
        GameAPI api = context.getGameAPI();
        this.configuration = new SlayerConfiguration();
        this.settings = configuration.load();
        this.taskTracker = new SlayerTaskTracker(api);
        this.navigationHandler = new SlayerNavigationHandler(api);
        SlayerPreparationHandler preparationHandler = new SlayerPreparationHandler(api);
        SlayerLootHandler lootHandler = new SlayerLootHandler(api, settings);
        SlayerMasterHandler masterHandler = new SlayerMasterHandler(this, api, taskTracker);
        SlayerCombatHandler combatHandler = new SlayerCombatHandler(this, api, lootHandler);
        this.controller = new SlayerController(
                this,
                api,
                taskTracker,
                navigationHandler,
                masterHandler,
                preparationHandler,
                combatHandler
        );
        this.chatListener = this::onChatMessage;
        context.getEventBus().subscribe(ChatMessageEvent.class, chatListener);
        refreshClientTrackers();
        this.status = taskTracker.snapshot().toStatusLine();
        println("Started.");
    }

    @Override
    public void doRun() {
        refreshClientTrackers();
        controller.onLoop();
        SlayerTaskSnapshot snapshot = taskTracker.snapshot();
        status = controller.getStatus() + " | " + snapshot.toStatusLine();
        logLoopHeartbeat(snapshot);
    }

    @Override
    protected void onShutdown() {
        if (chatListener != null) {
            context().getEventBus().unsubscribe(ChatMessageEvent.class, chatListener);
        }
        if (configuration != null && settings != null) {
            configuration.save(settings);
        }
    }

    @Override
    protected int defaultLoopDelayMs() {
        return 300;
    }

    public String getStatus() {
        return status;
    }

    public boolean isStarted() {
        return settings.isStarted();
    }

    public SlayerMaster getSelectedMaster() {
        return settings.getSelectedMaster();
    }

    public SlayerMaster getBonusTaskMaster() {
        return settings.getBonusTaskMaster();
    }

    public void setSelectedMaster(String masterName) {
        settings.setSelectedMaster(SlayerMaster.fromDisplayName(masterName));
        save();
    }

    public SlayerTask getCurrentSlayerTask() {
        return taskTracker.getCurrentTask();
    }

    public String getCurrentSlayerTaskName() {
        return taskTracker.getCurrentTaskName();
    }

    public int getCurrentSlayerKillsRemaining() {
        return taskTracker.getCurrentKillsRemaining();
    }

    public int getCurrentSlayerStreak() {
        return taskTracker.getCurrentStreak();
    }

    public SlayerMaster getActiveMaster() {
        if (!settings.isPointFarmEnabled()) {
            return settings.getSelectedMaster();
        }
        if (isCurrentTaskBonusTask() || isNextTaskBonusTask()) {
            return settings.getBonusTaskMaster();
        }
        return SlayerMaster.JACQUELYN;
    }

    public Coordinate getSelectedMasterLocation() {
        return navigationHandler.resolveMasterLocation(getActiveMaster(), this);
    }

    public boolean isPriorityListEnabled() {
        return settings.isPriorityListEnabled();
    }

    public boolean isNpcContactEnabled() {
        return settings.isUseNpcContact();
    }

    public boolean isBankAfterTaskCompletionEnabled() {
        return settings.isBankAfterTaskCompletion();
    }

    public SlayerCombatProfile getResolvedCombatProfile(SlayerTask task) {
        return settings.getResolvedCombatProfile(task);
    }

    public int getTaskPriority(SlayerTask task) {
        return settings.getTaskPriority(task);
    }

    public SlayerTaskLocation getSelectedTaskLocation(SlayerTask task) {
        return navigationHandler.resolveTaskLocation(task, settings.getSelectedTaskLocation(task), this);
    }

    public SlayerTaskLocation getCurrentTaskLocation() {
        return getSelectedTaskLocation(getCurrentSlayerTask());
    }

    public GameAPI getApi() {
        return context().getGameAPI();
    }

    public Map<String, GameAPI> getClientApis() {
        Map<String, GameAPI> apis = new LinkedHashMap<>();
        for (Client client : context().getClientProvider().getClients()) {
            apis.put(client.getName(), client.getGameAPI());
        }
        return Map.copyOf(apis);
    }

    public Map<String, SlayerTaskSnapshot> getClientTaskSnapshots() {
        Map<String, SlayerTaskSnapshot> snapshots = new LinkedHashMap<>();
        for (Map.Entry<String, SlayerTaskTracker> entry : clientTrackers.entrySet()) {
            snapshots.put(entry.getKey(), entry.getValue().snapshot());
        }
        return Map.copyOf(snapshots);
    }

    public List<String> getSelectedTaskMonsterNames(SlayerTask task) {
        SlayerTaskLocation location = getSelectedTaskLocation(task);
        if (location != null && !location.getMonsterNames().isEmpty()) {
            return location.getMonsterNames();
        }
        return SlayerTaskMonsterRegistry.getDefaultMonsterNames(task);
    }

    public List<String> getCurrentTaskMonsterNames() {
        return getSelectedTaskMonsterNames(getCurrentSlayerTask());
    }

    public int getTaskCombatAreaRadius(SlayerTask task, SlayerTaskLocation location) {
        return settings.getTaskCombatAreaRadius(task, location == null ? null : location.getKey());
    }

    public boolean isCurrentTaskBonusTask() {
        return !taskTracker.hasNoActiveTask() && getCurrentSlayerStreak() > 0 && getCurrentSlayerStreak() % 10 == 0;
    }

    public boolean isNextTaskBonusTask() {
        return taskTracker.hasNoActiveTask() && (getCurrentSlayerStreak() + 1) % 10 == 0;
    }

    public boolean isPostTaskBankingPending() {
        return postTaskBankingPending;
    }

    public void markPostTaskBankingRequired() {
        postTaskBankingPending = true;
    }

    public void completePostTaskBanking() {
        postTaskBankingPending = false;
    }

    public boolean isLootBankingPending() {
        return lootBankingPending;
    }

    public void requestLootBanking() {
        lootBankingPending = true;
    }

    public void clearLootBankingRequest() {
        lootBankingPending = false;
    }

    public void save() {
        configuration.save(settings);
    }

    public String exportShareCode() {
        return SlayerShareCodec.encode(settings);
    }

    public ImportResult importShareCode(String shareCode) {
        SlayerShareCodec.DecodeResult decoded = SlayerShareCodec.decode(shareCode);
        if (!decoded.success() || decoded.settings() == null) {
            return ImportResult.failure(decoded.message());
        }
        boolean wasStarted = settings.isStarted();
        settings.replaceWith(decoded.settings());
        settings.setStarted(wasStarted);
        save();
        return ImportResult.success("Config imported and saved.");
    }

    public SlayerTask[] getSelectableTasks() {
        return Arrays.stream(SlayerTask.values())
                .filter(task -> task != SlayerTask.UNKNOWN && task != SlayerTask.NOTHING)
                .toArray(SlayerTask[]::new);
    }

    public void logSuccess(String message) {
        println("[SUCCESS] " + message);
    }

    private void logLoopHeartbeat(SlayerTaskSnapshot snapshot) {
        long now = System.currentTimeMillis();
        if (now - lastLoopLogAt < LOOP_LOG_INTERVAL_MS) {
            return;
        }
        lastLoopLogAt = now;
        println("[LOOP] " + status);
    }

    private void refreshClientTrackers() {
        clientTrackers.clear();
        for (Client client : context().getClientProvider().getClients()) {
            clientTrackers.put(client.getName(), new SlayerTaskTracker(client.getGameAPI()));
        }
    }

    private void onChatMessage(ChatMessageEvent event) {
        if (event == null || event.getMessage() == null || event.getMessage().text() == null) {
            return;
        }
        if (postTaskBankingPending && event.getMessage().text().contains("Your preset is being withdrawn")) {
            completePostTaskBanking();
        }
    }

    public record ImportResult(boolean success, String message) {
        public static ImportResult success(String message) {
            return new ImportResult(true, message);
        }

        public static ImportResult failure(String message) {
            return new ImportResult(false, message);
        }
    }
}
