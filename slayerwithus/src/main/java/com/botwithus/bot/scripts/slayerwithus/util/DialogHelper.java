package com.botwithus.bot.scripts.slayerwithus.util;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.inventory.ActionTypes;
import com.botwithus.bot.api.model.Component;
import com.botwithus.bot.api.model.GameAction;
import com.botwithus.bot.api.query.ComponentFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DialogHelper {
    private static final int[] DIALOG_INTERFACES = {1184, 1186, 1188, 1189, 1191};
    private static final int[] OPTION_HASHES = {77856776, 77856781, 77856786, 77856791, 77856796};

    private DialogHelper() {
    }

    public static boolean isOpen(GameAPI api) {
        for (int interfaceId : DIALOG_INTERFACES) {
            if (api.isInterfaceOpen(interfaceId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean continueDialog(GameAPI api) {
        int hash = defaultHash(api);
        if (hash == 0) {
            return false;
        }
        api.queueAction(new GameAction(ActionTypes.DIALOGUE, 0, -1, hash));
        return true;
    }

    public static List<String> getOptions(GameAPI api) {
        if (!api.isInterfaceOpen(1188)) {
            return Collections.emptyList();
        }
        List<Component> components = api.queryComponents(ComponentFilter.builder().interfaceId(1188).build());
        List<String> options = new ArrayList<>();
        for (Component component : components) {
            int id = component.componentId();
            if (id == 6 || id == 33 || id == 35 || id == 37 || id == 39) {
                String text = api.getComponentText(component.interfaceId(), component.componentId());
                if (text != null && !text.isBlank()) {
                    options.add(text);
                }
            }
        }
        return options;
    }

    public static boolean chooseOption(GameAPI api, int index) {
        if (!api.isInterfaceOpen(1188) || index < 0 || index >= OPTION_HASHES.length) {
            return false;
        }
        api.queueAction(new GameAction(ActionTypes.DIALOGUE, 0, -1, OPTION_HASHES[index]));
        return true;
    }

    private static int defaultHash(GameAPI api) {
        if (api.isInterfaceOpen(1184)) {
            return 77594639;
        }
        if (api.isInterfaceOpen(1186)) {
            return 77725700;
        }
        if (api.isInterfaceOpen(1189)) {
            return 77922323;
        }
        if (api.isInterfaceOpen(1191)) {
            return 78053391;
        }
        return 0;
    }
}
