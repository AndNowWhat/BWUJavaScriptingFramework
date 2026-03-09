package com.botwithus.bot.scripts.slayerwithus.controller;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.scripts.slayerwithus.SlayerWithUs;
import com.botwithus.bot.scripts.slayerwithus.combat.SlayerCombatHandler;
import com.botwithus.bot.scripts.slayerwithus.master.SlayerMasterHandler;
import com.botwithus.bot.scripts.slayerwithus.navigation.SlayerNavigationHandler;
import com.botwithus.bot.scripts.slayerwithus.preparation.SlayerPreparationHandler;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTask;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskLocation;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskTracker;
import net.botwithus.rs3.game.Coordinate;

public final class SlayerController {
    private static final int MASTER_RADIUS = 3;
    private static final int TASK_RADIUS = 5;

    private final SlayerWithUs script;
    private final GameAPI api;
    private final SlayerTaskTracker taskTracker;
    private final SlayerNavigationHandler navigationHandler;
    private final SlayerMasterHandler masterHandler;
    private final SlayerPreparationHandler preparationHandler;
    private final SlayerCombatHandler combatHandler;

    private SlayerBotState currentState = SlayerBotState.IDLE;
    private SlayerBotState lastLoggedState = SlayerBotState.IDLE;
    private boolean hadActiveTaskLastLoop;
    private SlayerTask lastTask = SlayerTask.NOTHING;

    public SlayerController(
            SlayerWithUs script,
            GameAPI api,
            SlayerTaskTracker taskTracker,
            SlayerNavigationHandler navigationHandler,
            SlayerMasterHandler masterHandler,
            SlayerPreparationHandler preparationHandler,
            SlayerCombatHandler combatHandler
    ) {
        this.script = script;
        this.api = api;
        this.taskTracker = taskTracker;
        this.navigationHandler = navigationHandler;
        this.masterHandler = masterHandler;
        this.preparationHandler = preparationHandler;
        this.combatHandler = combatHandler;
    }

    public void onLoop() {
        if (api.getLocalPlayer() == null) {
            currentState = SlayerBotState.IDLE;
            script.delay(1);
            return;
        }

        taskTracker.logTaskIfChanged(script::logSuccess);
        detectTaskCompletionTransition();
        detectTaskTransition();
        currentState = determineState();
        logStateTransition();

        switch (currentState) {
            case IDLE -> script.delay(1);
            case NAVIGATING_TO_MASTER -> handleNavigateToMaster();
            case GETTING_TASK -> handleGetTask();
            case PREPARING_FOR_TASK -> script.delay(1);
            case NAVIGATING_TO_POST_TASK_BANK -> handleNavigateToPostTaskBank();
            case BANKING_AFTER_TASK -> handleBankAfterTask();
            case NAVIGATING_TO_TASK -> handleNavigateToTask();
            case COMBAT -> handleCombat();
        }
    }

    public String getStatus() {
        return script.isStarted() ? currentState.name() : "STOPPED";
    }

    public void reset() {
        currentState = SlayerBotState.IDLE;
        lastLoggedState = SlayerBotState.IDLE;
        hadActiveTaskLastLoop = false;
        lastTask = SlayerTask.NOTHING;
    }

    private SlayerBotState determineState() {
        if (!script.isStarted()) {
            return SlayerBotState.IDLE;
        }
        if (script.isPostTaskBankingPending()) {
            return preparationHandler.isAtBank(script.getActiveMaster())
                    ? SlayerBotState.BANKING_AFTER_TASK
                    : SlayerBotState.NAVIGATING_TO_POST_TASK_BANK;
        }
        if (taskTracker.hasNoActiveTask()) {
            Coordinate master = script.getSelectedMasterLocation();
            return navigationHandler.isAt(master) ? SlayerBotState.GETTING_TASK : SlayerBotState.NAVIGATING_TO_MASTER;
        }
        SlayerTaskLocation location = script.getCurrentTaskLocation();
        if (location == null || location.getCoordinate() == null) {
            return SlayerBotState.IDLE;
        }
        return navigationHandler.isAt(location, TASK_RADIUS) ? SlayerBotState.COMBAT : SlayerBotState.NAVIGATING_TO_TASK;
    }

    private void handleNavigateToMaster() {
        navigationHandler.moveTo(script.getSelectedMasterLocation(), MASTER_RADIUS);
        script.delay(1, 2);
    }

    private void handleGetTask() {
        masterHandler.handleTaskAcquisition();
        script.delay(1, 2);
    }

    private void handleNavigateToPostTaskBank() {
        navigationHandler.moveTo(script.getSelectedMasterLocation(), MASTER_RADIUS);
        script.delay(1, 2);
    }

    private void handleBankAfterTask() {
        if (preparationHandler.ensureBankOpen(script.getActiveMaster()) && preparationHandler.loadLastPreset()) {
            script.completePostTaskBanking();
        }
        script.delay(1, 2);
    }

    private void handleNavigateToTask() {
        navigationHandler.moveTo(script.getCurrentSlayerTask(), script.getCurrentTaskLocation(), TASK_RADIUS, script);
        script.delay(1, 2);
    }

    private void handleCombat() {
        combatHandler.handleCombat();
        script.delay(1);
    }

    private void logStateTransition() {
        if (currentState != lastLoggedState) {
            script.println("State -> " + currentState.name());
            lastLoggedState = currentState;
        }
    }

    private void detectTaskCompletionTransition() {
        boolean hasActiveTask = !taskTracker.hasNoActiveTask();
        if (hadActiveTaskLastLoop && !hasActiveTask && script.isBankAfterTaskCompletionEnabled()) {
            script.markPostTaskBankingRequired();
        }
        hadActiveTaskLastLoop = hasActiveTask;
    }

    private void detectTaskTransition() {
        SlayerTask currentTask = taskTracker.getCurrentTask();
        if (currentTask != lastTask) {
            lastTask = currentTask;
        }
    }
}
