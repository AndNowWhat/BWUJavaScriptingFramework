package com.botwithus.bot.api.inventory;

/**
 * Common RS3 action type IDs used for component interactions.
 * These correspond to the action_id values in the game's mini menu system.
 */
public final class ActionTypes {

    private ActionTypes() {}

    /** Standard component click (CC_OP). */
    public static final int COMPONENT = 1;

    /** Selectable component action (e.g. "Use" item). */
    public static final int SELECTABLE_COMPONENT = 8;
}
