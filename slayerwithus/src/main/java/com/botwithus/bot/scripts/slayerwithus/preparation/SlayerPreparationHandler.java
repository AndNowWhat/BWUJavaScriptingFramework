package com.botwithus.bot.scripts.slayerwithus.preparation;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.entities.SceneObject;
import com.botwithus.bot.api.entities.SceneObjects;
import com.botwithus.bot.api.inventory.Bank;
import com.botwithus.bot.scripts.slayerwithus.master.SlayerMaster;

public final class SlayerPreparationHandler {
    private final SceneObjects objects;
    private final Bank bank;

    public SlayerPreparationHandler(GameAPI api) {
        this.objects = new SceneObjects(api);
        this.bank = new Bank(api);
    }

    public boolean isAtBank(SlayerMaster master) {
        return bank.isOpen() || findBank() != null;
    }

    public boolean ensureBankOpen(SlayerMaster master) {
        if (bank.isOpen()) {
            return true;
        }
        SceneObject bankObject = findBank();
        if (bankObject == null) {
            return false;
        }
        return bankObject.interact("Bank") || bankObject.interact("Use");
    }

    public boolean loadLastPreset() {
        return bank.isOpen() && bank.withdrawPreset(1);
    }

    private SceneObject findBank() {
        return objects.query()
                .named("Bank chest")
                .withinDistance(8)
                .nearest();
    }
}
