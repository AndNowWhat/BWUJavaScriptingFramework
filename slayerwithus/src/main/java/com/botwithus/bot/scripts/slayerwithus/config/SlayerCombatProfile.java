package com.botwithus.bot.scripts.slayerwithus.config;

public final class SlayerCombatProfile {
    private boolean useFood;
    private int healThresholdPercent;
    private boolean usePrayerRestore;
    private int prayerThresholdPercent;
    private boolean usePrayerRenewal;
    private boolean useOverloads;
    private SlayerPrayerMode prayerMode;
    private boolean useCurses;
    private SlayerAttackStyle attackStyle;
    private int hybridSoulSplitHealthPercent;

    public SlayerCombatProfile() {
        this.useFood = false;
        this.healThresholdPercent = 60;
        this.usePrayerRestore = false;
        this.prayerThresholdPercent = 25;
        this.usePrayerRenewal = false;
        this.useOverloads = false;
        this.prayerMode = SlayerPrayerMode.DISABLED;
        this.useCurses = true;
        this.attackStyle = SlayerAttackStyle.AUTO;
        this.hybridSoulSplitHealthPercent = 75;
    }

    public SlayerCombatProfile copy() {
        SlayerCombatProfile copy = new SlayerCombatProfile();
        copy.useFood = useFood;
        copy.healThresholdPercent = healThresholdPercent;
        copy.usePrayerRestore = usePrayerRestore;
        copy.prayerThresholdPercent = prayerThresholdPercent;
        copy.usePrayerRenewal = usePrayerRenewal;
        copy.useOverloads = useOverloads;
        copy.prayerMode = prayerMode;
        copy.useCurses = useCurses;
        copy.attackStyle = attackStyle;
        copy.hybridSoulSplitHealthPercent = hybridSoulSplitHealthPercent;
        return copy;
    }

    public boolean isUseFood() {
        return useFood;
    }

    public void setUseFood(boolean useFood) {
        this.useFood = useFood;
    }

    public int getHealThresholdPercent() {
        return healThresholdPercent;
    }

    public void setHealThresholdPercent(int healThresholdPercent) {
        this.healThresholdPercent = clampPercent(healThresholdPercent);
    }

    public boolean isUsePrayerRestore() {
        return usePrayerRestore;
    }

    public void setUsePrayerRestore(boolean usePrayerRestore) {
        this.usePrayerRestore = usePrayerRestore;
    }

    public int getPrayerThresholdPercent() {
        return prayerThresholdPercent;
    }

    public void setPrayerThresholdPercent(int prayerThresholdPercent) {
        this.prayerThresholdPercent = clampPercent(prayerThresholdPercent);
    }

    public boolean isUsePrayerRenewal() {
        return usePrayerRenewal;
    }

    public void setUsePrayerRenewal(boolean usePrayerRenewal) {
        this.usePrayerRenewal = usePrayerRenewal;
    }

    public boolean isUseOverloads() {
        return useOverloads;
    }

    public void setUseOverloads(boolean useOverloads) {
        this.useOverloads = useOverloads;
    }

    public SlayerPrayerMode getPrayerMode() {
        return prayerMode;
    }

    public void setPrayerMode(SlayerPrayerMode prayerMode) {
        this.prayerMode = prayerMode == null ? SlayerPrayerMode.DISABLED : prayerMode;
    }

    public boolean isUseCurses() {
        return useCurses;
    }

    public void setUseCurses(boolean useCurses) {
        this.useCurses = useCurses;
    }

    public SlayerAttackStyle getAttackStyle() {
        return attackStyle;
    }

    public void setAttackStyle(SlayerAttackStyle attackStyle) {
        this.attackStyle = attackStyle == null ? SlayerAttackStyle.AUTO : attackStyle;
    }

    public int getHybridSoulSplitHealthPercent() {
        return hybridSoulSplitHealthPercent;
    }

    public void setHybridSoulSplitHealthPercent(int hybridSoulSplitHealthPercent) {
        this.hybridSoulSplitHealthPercent = clampPercent(hybridSoulSplitHealthPercent);
    }

    private int clampPercent(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
