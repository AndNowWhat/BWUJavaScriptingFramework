package com.botwithus.bot.scripts.slayerwithus.master;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.entities.Npc;
import com.botwithus.bot.api.entities.Npcs;
import com.botwithus.bot.api.inventory.ActionTypes;
import com.botwithus.bot.api.model.GameAction;
import com.botwithus.bot.scripts.slayerwithus.SlayerWithUs;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTask;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskTracker;
import com.botwithus.bot.scripts.slayerwithus.util.DialogHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class SlayerMasterHandler {
    private static final int ASSIGNMENT_CONFIRM_HASH = 78053391;
    private static final int NPC_CONTACT_INTERFACE_ID = 88;
    private static final int NPC_CONTACT_PARENT_HASH = 5767169;

    private final SlayerWithUs script;
    private final GameAPI api;
    private final Npcs npcs;
    private final SlayerTaskTracker taskTracker;

    public SlayerMasterHandler(SlayerWithUs script, GameAPI api, SlayerTaskTracker taskTracker) {
        this.script = script;
        this.api = api;
        this.npcs = new Npcs(api);
        this.taskTracker = taskTracker;
    }

    public boolean handleTaskAcquisition() {
        if (!taskTracker.hasNoActiveTask()) {
            return true;
        }
        if (script.isNpcContactEnabled() && tryNpcContactDialog()) {
            return !taskTracker.hasNoActiveTask();
        }
        handleNearbyMasterInteraction();
        handleDialog();
        return !taskTracker.hasNoActiveTask();
    }

    private boolean tryNpcContactDialog() {
        if (!api.isInterfaceOpen(NPC_CONTACT_INTERFACE_ID)) {
            return false;
        }
        int componentIndex = getNpcContactMasterComponentIndex(script.getActiveMaster());
        if (componentIndex < 0) {
            return false;
        }
        api.queueAction(new GameAction(ActionTypes.COMPONENT, 1, componentIndex, NPC_CONTACT_PARENT_HASH));
        return true;
    }

    private void handleNearbyMasterInteraction() {
        Npc master = findSelectedMaster();
        if (master == null) {
            return;
        }
        if (!master.interact("Get task") && !master.interact("Get-task")) {
            master.interact("Talk-to");
        }
    }

    private void handleDialog() {
        if (!DialogHelper.isOpen(api)) {
            return;
        }
        if (api.isInterfaceOpen(1191)) {
            api.queueAction(new GameAction(ActionTypes.DIALOGUE, 0, -1, ASSIGNMENT_CONFIRM_HASH));
            return;
        }
        List<String> options = DialogHelper.getOptions(api);
        if (!options.isEmpty()) {
            DialogHelper.chooseOption(api, chooseOption(options));
            return;
        }
        DialogHelper.continueDialog(api);
    }

    private int chooseOption(List<String> options) {
        if (options.size() == 2
                && SlayerTask.fromDialogOption(options.get(0)).isPresent()
                && SlayerTask.fromDialogOption(options.get(1)).isPresent()) {
            if (!script.isPriorityListEnabled()) {
                return 0;
            }
            int bestIndex = 0;
            int bestPriority = Integer.MIN_VALUE;
            for (int i = 0; i < options.size(); i++) {
                Optional<SlayerTask> task = SlayerTask.fromDialogOption(options.get(i));
                if (task.isPresent()) {
                    int priority = script.getTaskPriority(task.get());
                    if (priority > bestPriority) {
                        bestPriority = priority;
                        bestIndex = i;
                    }
                }
            }
            return bestIndex;
        }
        for (int i = 0; i < options.size(); i++) {
            String normalized = options.get(i).toLowerCase();
            if (normalized.contains("assignment") || normalized.contains("task")) {
                return i;
            }
        }
        return 0;
    }

    private Npc findSelectedMaster() {
        for (String name : lookupNames(script.getActiveMaster())) {
            Npc master = npcs.query()
                    .named(name)
                    .withinDistance(12)
                    .filter(npc -> npc.hasOption("Get task") || npc.hasOption("Get-task") || npc.hasOption("Talk-to"))
                    .nearest();
            if (master != null) {
                return master;
            }
        }
        return null;
    }

    private static List<String> lookupNames(SlayerMaster master) {
        if (master == SlayerMaster.TURAEL) {
            return Arrays.asList("Turael", "Spria");
        }
        return List.of(master.getDisplayName());
    }

    private int getNpcContactMasterComponentIndex(SlayerMaster master) {
        if (master == null) {
            return -1;
        }
        return switch (master) {
            case TURAEL, SPRIA -> 3;
            case SUMONA -> 5;
            case MAZCHNA, ACHTRYN -> 6;
            case LAPALOK, DURADEL -> 7;
            case VANNAKA -> 8;
            case CHAELDAR -> 10;
            case KURADAL -> 13;
            case MORVRAN -> 20;
            case LANIAKEA -> 22;
            case JACQUELYN -> 24;
            case MANDRITH -> 25;
            case THE_RAPTOR -> 27;
        };
    }
}
