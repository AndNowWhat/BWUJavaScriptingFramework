package com.botwithus.bot.api.inventory;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.model.Component;
import com.botwithus.bot.api.model.GameAction;
import com.botwithus.bot.api.model.InventoryItem;
import com.botwithus.bot.api.query.ComponentFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Provides access to the player's equipment (inventory ID 94, interface 1464).
 * Ported from the legacy BotWithUs API to use the pipe RPC.
 */
public final class Equipment {

    public static final int INVENTORY_ID = 94;
    public static final int INTERFACE_ID = 1464;
    public static final int COMPONENT_ID = 15;

    private final GameAPI api;
    private final InventoryContainer container;

    public Equipment(GameAPI api) {
        this.api = api;
        this.container = new InventoryContainer(api, INVENTORY_ID);
    }

    public InventoryContainer container() {
        return container;
    }

    // ========================== Query Methods ==========================

    public boolean contains(int itemId) {
        return container.contains(itemId);
    }

    public List<InventoryItem> getItems() {
        return container.getItems();
    }

    /**
     * Get the item in a specific equipment slot.
     */
    public InventoryItem getSlot(Slot slot) {
        return container.slot(slot.getIndex());
    }

    // ========================== Interaction Methods ==========================

    /**
     * Interact with an equipment slot by slot and option string.
     *
     * @param slot   the equipment slot
     * @param option the right-click option (e.g. "Remove", "Check")
     * @return true if the action was queued
     */
    public boolean interact(Slot slot, String option) {
        Component comp = findComponentBySubIndex(slot.getIndex());
        if (comp == null) return false;
        return interactComponent(comp, option);
    }

    /**
     * Interact with an equipment slot by slot and option index (1-based).
     */
    public boolean interact(Slot slot, int optionIndex) {
        Component comp = findComponentBySubIndex(slot.getIndex());
        if (comp == null) return false;
        return queueComponentAction(comp, optionIndex);
    }

    /**
     * Interact with an equipped item by item ID and option string.
     */
    public boolean interact(int itemId, String option) {
        Component comp = findComponentByItem(itemId);
        if (comp == null) return false;
        return interactComponent(comp, option);
    }

    // ========================== Helpers ==========================

    private Component findComponentBySubIndex(int subIndex) {
        // Equipment items are sub-components of the main equipment component
        List<Component> children = api.getComponentChildren(INTERFACE_ID, COMPONENT_ID);
        return children.stream()
                .filter(c -> c.subComponentId() == subIndex)
                .findFirst().orElse(null);
    }

    private Component findComponentByItem(int itemId) {
        List<Component> comps = api.queryComponents(ComponentFilter.builder()
                .interfaceId(INTERFACE_ID)
                .itemId(itemId)
                .build());
        return comps.isEmpty() ? null : comps.getFirst();
    }

    private boolean interactComponent(Component comp, String option) {
        List<String> options = api.getComponentOptions(comp.interfaceId(), comp.componentId());
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).equalsIgnoreCase(option)) {
                return queueComponentAction(comp, i + 1);
            }
        }
        return false;
    }

    private boolean queueComponentAction(Component comp, int optionIndex) {
        int hash = comp.interfaceId() << 16 | comp.componentId();
        api.queueAction(new GameAction(
                ActionTypes.COMPONENT, optionIndex, comp.subComponentId(), hash));
        return true;
    }

    // ========================== Equipment Slots ==========================

    public enum Slot {
        HEAD(0),
        CAPE(1),
        NECK(2),
        WEAPON(3),
        BODY(4),
        SHIELD(5),
        LEGS(7),
        HANDS(9),
        FEET(10),
        RING(12),
        AMMUNITION(13),
        AURA(14),
        POCKET(17);

        private final int index;

        Slot(int index) {
            this.index = index;
        }

        private static final Slot[] SLOTS = values();

        public static Slot resolve(int index) {
            return Arrays.stream(SLOTS)
                    .filter(slot -> slot.index == index)
                    .findFirst().orElse(null);
        }

        public int getIndex() {
            return this.index;
        }
    }
}
