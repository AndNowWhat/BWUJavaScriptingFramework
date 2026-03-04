package com.botwithus.bot.api;

import com.botwithus.bot.api.model.*;
import com.botwithus.bot.api.query.ComponentFilter;
import com.botwithus.bot.api.query.EntityFilter;
import com.botwithus.bot.api.query.InventoryFilter;

import java.util.List;

public interface GameAPI {

    // --- System ---
    boolean ping();
    List<String> listMethods();
    void subscribe(String event);
    void unsubscribe(String event);
    int getClientCount();

    // --- Actions ---
    void queueAction(GameAction action);
    int queueActions(List<GameAction> actions);
    int getActionQueueSize();
    void clearActionQueue();
    List<ActionEntry> getActionHistory(int maxResults, int actionIdFilter);
    long getLastActionTime();
    void setBehaviorMod(int modId, float value);
    void clearBehaviorMod(int modId);
    float getBehaviorMod(int modId);
    boolean areActionsBlocked();
    void setActionsBlocked(boolean blocked);

    // --- Entity queries ---
    List<Entity> queryEntities(EntityFilter filter);
    EntityInfo getEntityInfo(int handle);
    String getEntityName(int handle);
    EntityHealth getEntityHealth(int handle);
    EntityPosition getEntityPosition(int handle);
    boolean isEntityValid(int handle);
    List<Hitmark> getEntityHitmarks(int handle);
    int getEntityAnimation(int handle);
    String getEntityOverheadText(int handle);
    int getAnimationLength(int animationId);

    // --- Ground items ---
    List<GroundItemStack> queryGroundItems(EntityFilter filter);
    List<GroundItem> getObjStackItems(int handle);
    List<Entity> queryObjStacks(EntityFilter filter);

    // --- Projectiles ---
    List<Projectile> queryProjectiles(int projectileId, int plane, int maxResults);

    // --- Spot anims ---
    List<SpotAnim> querySpotAnims(int animId, int plane, int maxResults);

    // --- Hint arrows ---
    List<HintArrow> queryHintArrows(int maxResults);

    // --- Worlds ---
    List<World> queryWorlds(boolean includeActivity);
    World getCurrentWorld();
    int computeNameHash(String name);
    void updateQueryContext();
    void invalidateQueryContext();

    // --- Components & Interfaces ---
    List<Component> queryComponents(ComponentFilter filter);
    boolean isComponentValid(int interfaceId, int componentId, int subComponentId);
    String getComponentText(int interfaceId, int componentId);
    InventoryItem getComponentItem(int interfaceId, int componentId, int subComponentId);
    ComponentPosition getComponentPosition(int interfaceId, int componentId);
    List<String> getComponentOptions(int interfaceId, int componentId);
    int getComponentSpriteId(int interfaceId, int componentId);
    ComponentTypeInfo getComponentType(int interfaceId, int componentId);
    List<Component> getComponentChildren(int interfaceId, int componentId);
    int getComponentByHash(int interfaceId, int componentId, int subComponentId);
    List<OpenInterface> getOpenInterfaces();
    boolean isInterfaceOpen(int interfaceId);

    // --- Game Variables ---
    int getVarp(int varId);
    int getVarbit(int varbitId);
    int getVarcInt(int varcId);
    String getVarcString(int varcId);
    List<VarbitValue> queryVarbits(List<Integer> varbitIds);

    // --- Script Execution ---
    long getScriptHandle(int scriptId);
    ScriptResult executeScript(long handle, int[] intArgs, String[] stringArgs, String[] returns);
    void destroyScriptHandle(long handle);
    void fireKeyTrigger(int interfaceId, int componentId, String input);

    // --- Game State ---
    LocalPlayer getLocalPlayer();
    int getGameCycle();
    LoginState getLoginState();
    List<MiniMenuEntry> getMiniMenu();
    List<GrandExchangeOffer> getGrandExchangeOffers();
    ScreenPosition getWorldToScreen(int tileX, int tileY);
    List<ScreenPosition> batchWorldToScreen(List<int[]> tiles);
    ViewportInfo getViewportInfo();
    List<EntityScreenPosition> getEntityScreenPositions(List<Integer> handles);
    GameWindowRect getGameWindowRect();
    CacheFile getCacheFile(int indexId, int archiveId, int fileId);
    int getCacheFileCount(int indexId, int archiveId, int shift);
    void setWorld(int worldId);
    void changeLoginState(int oldState, int newState);
    void scheduleBreak(int durationMs);
    void interruptBreak();
    CacheFile getNavigationArchive();

    // --- Inventory & Items ---
    List<InventoryInfo> queryInventories();
    List<InventoryItem> queryInventoryItems(InventoryFilter filter);
    InventoryItem getInventoryItem(int inventoryId, int slot);
    List<ItemVar> getItemVars(int inventoryId, int slot);
    int getItemVarValue(int inventoryId, int slot, int varId);
    boolean isInventoryItemValid(int inventoryId, int slot);

    // --- Player Stats ---
    List<PlayerStat> getPlayerStats();
    PlayerStat getPlayerStat(int skillId);
    int getPlayerStatCount();

    // --- Chat ---
    List<ChatMessage> queryChatHistory(int messageType, int maxResults);
    String getChatMessageText(int index);
    String getChatMessagePlayer(int index);
    int getChatMessageType(int index);
    int getChatHistorySize();
}
