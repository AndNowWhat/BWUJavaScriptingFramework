package com.botwithus.bot.scripts.slayerwithus.loot;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.inventory.ActionTypes;
import com.botwithus.bot.api.inventory.Backpack;
import com.botwithus.bot.api.model.GameAction;
import com.botwithus.bot.api.query.EntityFilter;
import com.botwithus.bot.scripts.slayerwithus.config.SlayerSettings;

import java.util.Comparator;

public final class SlayerLootHandler {
    private final GameAPI api;
    private final Backpack backpack;
    private final SlayerSettings settings;

    public SlayerLootHandler(GameAPI api, SlayerSettings settings) {
        this.api = api;
        this.backpack = new Backpack(api);
        this.settings = settings;
    }

    public boolean handleLooting() {
        if (!settings.isAreaLootEnabled() && !settings.isLootAll() && settings.getLootItemPatterns().isEmpty()) {
            return false;
        }
        var player = api.getLocalPlayer();
        if (player == null) {
            return false;
        }

        var stacks = api.queryGroundItems(EntityFilter.builder()
                .tileX(player.tileX())
                .tileY(player.tileY())
                .plane(player.plane())
                .radius(8)
                .maxResults(50)
                .build());

        var target = stacks.stream()
                .filter(stack -> stack.items() != null && !stack.items().isEmpty())
                .filter(this::matchesLootRules)
                .min(Comparator.comparingInt(stack ->
                        Math.max(Math.abs(stack.tileX() - player.tileX()), Math.abs(stack.tileY() - player.tileY()))))
                .orElse(null);
        if (target == null) {
            return false;
        }

        api.queueAction(new GameAction(ActionTypes.GROUND_ITEM1, 0, target.handle(), 0));
        return true;
    }

    public boolean shouldTriggerLootBanking() {
        return backpack.isFull();
    }

    private boolean matchesLootRules(com.botwithus.bot.api.model.GroundItemStack stack) {
        return stack.items().stream().anyMatch(item -> {
            var type = api.getItemType(item.itemId());
            if (type == null || type.name() == null) {
                return false;
            }

            String name = type.name();
            boolean patternMatch = settings.getLootItemPatterns().stream()
                    .anyMatch(pattern -> !pattern.isBlank() && name.toLowerCase().contains(pattern.toLowerCase()));

            if (settings.isLootAll()) {
                return true;
            }
            if (patternMatch) {
                return true;
            }
            if (settings.isLootAllStackables() && type.stackable()) {
                if (settings.isLootAllStackablesIgnoreValueThreshold()) {
                    return true;
                }
                return !settings.isUseLootValueThreshold()
                        || estimatedValue(type, item.quantity()) >= settings.getLootValueThreshold();
            }
            if (settings.isUseLootValueThreshold()) {
                return estimatedValue(type, item.quantity()) >= settings.getLootValueThreshold();
            }
            return settings.isAreaLootEnabled() && type.stackable();
        });
    }

    private int estimatedValue(com.botwithus.bot.api.model.ItemType type, int quantity) {
        return Math.max(0, type.shopPrice()) * Math.max(1, quantity);
    }
}
