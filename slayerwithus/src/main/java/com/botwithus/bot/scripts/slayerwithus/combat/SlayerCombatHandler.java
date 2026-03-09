package com.botwithus.bot.scripts.slayerwithus.combat;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.entities.Npc;
import com.botwithus.bot.api.entities.Npcs;
import com.botwithus.bot.api.inventory.Backpack;
import com.botwithus.bot.api.model.LocalPlayer;
import com.botwithus.bot.scripts.slayerwithus.SlayerWithUs;
import com.botwithus.bot.scripts.slayerwithus.config.SlayerCombatProfile;
import com.botwithus.bot.scripts.slayerwithus.loot.SlayerLootHandler;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTask;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskLocation;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskMonsterRegistry;

import java.util.List;

public final class SlayerCombatHandler {
    private final SlayerWithUs script;
    private final Npcs npcs;
    private final Backpack backpack;
    private final SlayerLootHandler lootHandler;

    public SlayerCombatHandler(SlayerWithUs script, GameAPI api, SlayerLootHandler lootHandler) {
        this.script = script;
        this.npcs = new Npcs(api);
        this.backpack = new Backpack(api);
        this.lootHandler = lootHandler;
    }

    public void handleCombat() {
        LocalPlayer player = script.getApi().getLocalPlayer();
        if (player == null) {
            return;
        }

        lootHandler.handleLooting();
        if (lootHandler.shouldTriggerLootBanking()) {
            script.requestLootBanking();
            return;
        }

        SlayerTask task = script.getCurrentSlayerTask();
        SlayerTaskLocation location = script.getCurrentTaskLocation();
        if (task == null || location == null || location.getCoordinate() == null) {
            return;
        }

        handleSustain(player, script.getResolvedCombatProfile(task));
        if (player.targetIndex() != -1 || player.animationId() != -1 || player.isMoving()) {
            return;
        }

        List<String> names = SlayerTaskMonsterRegistry.getPrioritizedMonsterNames(task, script.getCurrentTaskMonsterNames());
        Npc target = npcs.query()
                .withinDistance(script.getTaskCombatAreaRadius(task, location))
                .filter(npc -> names.contains(npc.name()) && npc.hasOption("Attack"))
                .nearest();
        if (target != null) {
            target.interact("Attack");
        }
    }

    private void handleSustain(LocalPlayer player, SlayerCombatProfile profile) {
        if (profile == null || !profile.isUseFood() || player.maxHealth() <= 0) {
            return;
        }
        int percent = (int) ((player.health() * 100.0) / player.maxHealth());
        if (percent <= profile.getHealThresholdPercent()) {
            backpack.getItems().stream()
                    .filter(item -> item.itemId() > 0)
                    .findFirst()
                    .ifPresent(item -> backpack.interact(item.itemId(), "Eat"));
        }
    }
}
