package com.botwithus.bot.scripts.slayerwithus.config;

import com.botwithus.bot.scripts.slayerwithus.task.SlayerTask;
import com.botwithus.bot.scripts.slayerwithus.master.SlayerMaster;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public final class SlayerShareCodec {
    private static final String PREFIX = "SWU1-";
    private static final int FORMAT_VERSION = 2;
    private static final int RECOMMENDED_MAX_CODE_LENGTH = 1024;
    private static final int MAX_DECOMPRESSED_BYTES = 65536;
    private static final SlayerTask[] SHAREABLE_TASKS = Arrays.stream(SlayerTask.values())
            .filter(task -> task != SlayerTask.UNKNOWN)
            .toArray(SlayerTask[]::new);

    private SlayerShareCodec() {
    }

    public static int getRecommendedMaxCodeLength() {
        return RECOMMENDED_MAX_CODE_LENGTH;
    }

    public static String encode(SlayerSettings settings) {
        SlayerSettings source = settings == null ? new SlayerSettings() : settings;
        SlayerSettings defaults = new SlayerSettings();

        try {
            byte[] serialized = serialize(source, defaults);
            byte[] compressed = compress(serialized);
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(compressed);
            String checksum = toHex(calculateChecksum(compressed));
            return PREFIX + payload + "." + checksum;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to encode Slayer share code", e);
        }
    }

    public static DecodeResult decode(String code) {
        if (code == null || code.isBlank()) {
            return DecodeResult.failure("Share code is empty.");
        }

        String trimmed = code.trim();
        if (!trimmed.startsWith(PREFIX)) {
            return DecodeResult.failure("Unsupported share code prefix.");
        }

        int checksumSeparator = trimmed.lastIndexOf('.');
        if (checksumSeparator <= PREFIX.length() || checksumSeparator == trimmed.length() - 1) {
            return DecodeResult.failure("Share code checksum is missing.");
        }

        String payload = trimmed.substring(PREFIX.length(), checksumSeparator);
        String checksumText = trimmed.substring(checksumSeparator + 1);
        long expectedChecksum;
        try {
            expectedChecksum = parseHex(checksumText);
        } catch (NumberFormatException e) {
            return DecodeResult.failure("Share code checksum format is invalid.");
        }

        final byte[] compressed;
        try {
            compressed = Base64.getUrlDecoder().decode(payload);
        } catch (IllegalArgumentException e) {
            return DecodeResult.failure("Share code payload is not valid Base64.");
        }

        long actualChecksum = calculateChecksum(compressed);
        if (actualChecksum != expectedChecksum) {
            return DecodeResult.failure("Share code checksum mismatch.");
        }

        final byte[] decompressed;
        try {
            decompressed = decompress(compressed);
        } catch (IOException e) {
            return DecodeResult.failure("Share code payload could not be decompressed.");
        }

        try {
            SlayerSettings settings = deserialize(decompressed);
            return DecodeResult.success(settings);
        } catch (IOException e) {
            return DecodeResult.failure("Share code payload is malformed.");
        }
    }

    private static byte[] serialize(SlayerSettings settings, SlayerSettings defaults) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(byteStream)) {
            out.writeByte(FORMAT_VERSION);
            out.writeUTF(settings.getSelectedMaster().name());
            out.writeBoolean(settings.isPointFarmEnabled());
            out.writeUTF(settings.getBonusTaskMaster().name());
            out.writeBoolean(settings.isPriorityListEnabled());
            out.writeBoolean(settings.isBankAfterTaskCompletion());
            out.writeBoolean(settings.isAreaLootEnabled());
            out.writeBoolean(settings.isLootAll());
            out.writeBoolean(settings.isLootAllStackables());
            out.writeBoolean(settings.isLootAllStackablesIgnoreValueThreshold());
            out.writeBoolean(settings.isUseLootValueThreshold());
            out.writeInt(settings.getLootValueThreshold());
            out.writeBoolean(settings.isUseHerbBag());
            out.writeBoolean(settings.isUseUpgradedHerbBag());
            out.writeBoolean(settings.isUseGemBag());

            writeCombatProfile(out, settings.getDefaultCombatProfile());

            List<String> lootPatterns = settings.getLootItemPatterns();
            out.writeShort(Math.min(lootPatterns.size(), Short.MAX_VALUE));
            for (int i = 0; i < lootPatterns.size() && i < Short.MAX_VALUE; i++) {
                out.writeUTF(lootPatterns.get(i));
            }

            writeTaskPriorityDiffs(out, settings, defaults);
            writeTaskLocationDiffs(out, settings, defaults);
            writeTaskIntegerDiffs(out, settings, defaults, TaskIntegerKind.COMBAT_RADIUS);
            writeTaskBooleanDiffs(out, settings, defaults, TaskBooleanKind.TELEPORT);
            writeTaskBooleanDiffs(out, settings, defaults, TaskBooleanKind.SURGE);
            writeTaskBooleanDiffs(out, settings, defaults, TaskBooleanKind.DIVE);
            writeTaskBooleanDiffs(out, settings, defaults, TaskBooleanKind.COMBAT_OVERRIDE);
            writeTaskCombatProfileDiffs(out, settings, defaults);
        }
        return byteStream.toByteArray();
    }

    private static SlayerSettings deserialize(byte[] bytes) throws IOException {
        SlayerSettings settings = new SlayerSettings();
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int version = in.readUnsignedByte();
            if (version < 1 || version > FORMAT_VERSION) {
                throw new IOException("Unsupported format version: " + version);
            }

            settings.setSelectedMaster(parseMaster(in.readUTF(), settings.getSelectedMaster()));
            settings.setPointFarmEnabled(in.readBoolean());
            settings.setBonusTaskMaster(parseMaster(in.readUTF(), settings.getBonusTaskMaster()));
            settings.setPriorityListEnabled(in.readBoolean());
            settings.setBankAfterTaskCompletion(in.readBoolean());
            settings.setAreaLootEnabled(in.readBoolean());
            settings.setLootAll(in.readBoolean());
            settings.setLootAllStackables(in.readBoolean());
            if (version >= 2) {
                settings.setLootAllStackablesIgnoreValueThreshold(in.readBoolean());
            }
            settings.setUseLootValueThreshold(in.readBoolean());
            settings.setLootValueThreshold(in.readInt());
            settings.setUseHerbBag(in.readBoolean());
            settings.setUseUpgradedHerbBag(in.readBoolean());
            settings.setUseGemBag(in.readBoolean());

            SlayerCombatProfile defaultProfile = readCombatProfile(in);
            settings.getDefaultCombatProfile().setUseFood(defaultProfile.isUseFood());
            settings.getDefaultCombatProfile().setHealThresholdPercent(defaultProfile.getHealThresholdPercent());
            settings.getDefaultCombatProfile().setUsePrayerRestore(defaultProfile.isUsePrayerRestore());
            settings.getDefaultCombatProfile().setPrayerThresholdPercent(defaultProfile.getPrayerThresholdPercent());
            settings.getDefaultCombatProfile().setUsePrayerRenewal(defaultProfile.isUsePrayerRenewal());
            settings.getDefaultCombatProfile().setUseOverloads(defaultProfile.isUseOverloads());
            settings.getDefaultCombatProfile().setPrayerMode(defaultProfile.getPrayerMode());
            settings.getDefaultCombatProfile().setUseCurses(defaultProfile.isUseCurses());
            settings.getDefaultCombatProfile().setAttackStyle(defaultProfile.getAttackStyle());
            settings.getDefaultCombatProfile().setHybridSoulSplitHealthPercent(
                    defaultProfile.getHybridSoulSplitHealthPercent()
            );

            int patternCount = in.readUnsignedShort();
            List<String> patterns = new ArrayList<>(patternCount);
            for (int i = 0; i < patternCount; i++) {
                String pattern = in.readUTF();
                if (pattern != null && !pattern.isBlank()) {
                    patterns.add(pattern.trim());
                }
            }
            settings.setLootItemPatterns(patterns);

            readTaskPriorityDiffs(in, settings);
            readTaskLocationDiffs(in, settings);
            readTaskIntegerDiffs(in, settings, TaskIntegerKind.COMBAT_RADIUS);
            readTaskBooleanDiffs(in, settings, TaskBooleanKind.TELEPORT);
            readTaskBooleanDiffs(in, settings, TaskBooleanKind.SURGE);
            readTaskBooleanDiffs(in, settings, TaskBooleanKind.DIVE);
            readTaskBooleanDiffs(in, settings, TaskBooleanKind.COMBAT_OVERRIDE);
            readTaskCombatProfileDiffs(in, settings);
        }

        return settings;
    }

    private static void writeTaskPriorityDiffs(DataOutputStream out, SlayerSettings settings, SlayerSettings defaults)
            throws IOException {
        List<SlayerTask> changed = new ArrayList<>();
        for (SlayerTask task : SHAREABLE_TASKS) {
            if (settings.getTaskPriority(task) != defaults.getTaskPriority(task)) {
                changed.add(task);
            }
        }
        out.writeShort(changed.size());
        for (SlayerTask task : changed) {
            out.writeShort(task.getId());
            out.writeInt(settings.getTaskPriority(task));
        }
    }

    private static void readTaskPriorityDiffs(DataInputStream in, SlayerSettings settings) throws IOException {
        int count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            SlayerTask task = SlayerTask.fromId(in.readUnsignedShort());
            int value = in.readInt();
            if (task != SlayerTask.UNKNOWN) {
                settings.setTaskPriority(task, value);
            }
        }
    }

    private static void writeTaskLocationDiffs(DataOutputStream out, SlayerSettings settings, SlayerSettings defaults)
            throws IOException {
        List<Map.Entry<SlayerTask, String>> changed = settings.getTaskLocationSelections().entrySet().stream()
                .filter(entry -> entry.getKey() != null
                        && entry.getKey() != SlayerTask.UNKNOWN
                        && entry.getValue() != null
                        && !entry.getValue().isBlank()
                        && !entry.getValue().equals(defaults.getTaskLocationSelections().get(entry.getKey())))
                .toList();
        out.writeShort(changed.size());
        for (Map.Entry<SlayerTask, String> entry : changed) {
            out.writeShort(entry.getKey().getId());
            out.writeUTF(entry.getValue());
        }
    }

    private static void readTaskLocationDiffs(DataInputStream in, SlayerSettings settings) throws IOException {
        int count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            SlayerTask task = SlayerTask.fromId(in.readUnsignedShort());
            String locationKey = in.readUTF();
            if (task != SlayerTask.UNKNOWN) {
                settings.setSelectedTaskLocationKey(task, locationKey);
            }
        }
    }

    private static void writeTaskIntegerDiffs(DataOutputStream out, SlayerSettings settings, SlayerSettings defaults,
                                              TaskIntegerKind kind) throws IOException {
        List<SlayerTask> changed = new ArrayList<>();
        for (SlayerTask task : SHAREABLE_TASKS) {
            int current = switch (kind) {
                case COMBAT_RADIUS -> settings.getTaskCombatAreaRadius(task);
            };
            int fallback = switch (kind) {
                case COMBAT_RADIUS -> defaults.getTaskCombatAreaRadius(task);
            };
            if (current != fallback) {
                changed.add(task);
            }
        }

        out.writeShort(changed.size());
        for (SlayerTask task : changed) {
            out.writeShort(task.getId());
            int value = switch (kind) {
                case COMBAT_RADIUS -> settings.getTaskCombatAreaRadius(task);
            };
            out.writeInt(value);
        }
    }

    private static void readTaskIntegerDiffs(DataInputStream in, SlayerSettings settings, TaskIntegerKind kind)
            throws IOException {
        int count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            SlayerTask task = SlayerTask.fromId(in.readUnsignedShort());
            int value = in.readInt();
            if (task == SlayerTask.UNKNOWN) {
                continue;
            }
            switch (kind) {
                case COMBAT_RADIUS -> settings.setTaskCombatAreaRadius(task, value);
            }
        }
    }

    private static void writeTaskBooleanDiffs(DataOutputStream out, SlayerSettings settings, SlayerSettings defaults,
                                              TaskBooleanKind kind) throws IOException {
        List<SlayerTask> changed = new ArrayList<>();
        for (SlayerTask task : SHAREABLE_TASKS) {
            boolean current = switch (kind) {
                case TELEPORT -> settings.isTaskTeleportsEnabled(task);
                case SURGE -> settings.isTaskSurgeEnabled(task);
                case DIVE -> settings.isTaskDiveEnabled(task);
                case COMBAT_OVERRIDE -> settings.isTaskCombatOverrideEnabled(task);
            };
            boolean fallback = switch (kind) {
                case TELEPORT -> defaults.isTaskTeleportsEnabled(task);
                case SURGE -> defaults.isTaskSurgeEnabled(task);
                case DIVE -> defaults.isTaskDiveEnabled(task);
                case COMBAT_OVERRIDE -> defaults.isTaskCombatOverrideEnabled(task);
            };
            if (current != fallback) {
                changed.add(task);
            }
        }

        out.writeShort(changed.size());
        for (SlayerTask task : changed) {
            out.writeShort(task.getId());
            boolean value = switch (kind) {
                case TELEPORT -> settings.isTaskTeleportsEnabled(task);
                case SURGE -> settings.isTaskSurgeEnabled(task);
                case DIVE -> settings.isTaskDiveEnabled(task);
                case COMBAT_OVERRIDE -> settings.isTaskCombatOverrideEnabled(task);
            };
            out.writeBoolean(value);
        }
    }

    private static void readTaskBooleanDiffs(DataInputStream in, SlayerSettings settings, TaskBooleanKind kind)
            throws IOException {
        int count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            SlayerTask task = SlayerTask.fromId(in.readUnsignedShort());
            boolean value = in.readBoolean();
            if (task == SlayerTask.UNKNOWN) {
                continue;
            }
            switch (kind) {
                case TELEPORT -> settings.setTaskTeleportsEnabled(task, value);
                case SURGE -> settings.setTaskSurgeEnabled(task, value);
                case DIVE -> settings.setTaskDiveEnabled(task, value);
                case COMBAT_OVERRIDE -> settings.setTaskCombatOverrideEnabled(task, value);
            }
        }
    }

    private static void writeTaskCombatProfileDiffs(DataOutputStream out, SlayerSettings settings, SlayerSettings defaults)
            throws IOException {
        List<SlayerTask> changed = new ArrayList<>();
        for (SlayerTask task : SHAREABLE_TASKS) {
            SlayerCombatProfile current = settings.getTaskCombatProfile(task);
            SlayerCombatProfile fallback = defaults.getTaskCombatProfile(task);
            if (!combatProfilesEqual(current, fallback)) {
                changed.add(task);
            }
        }

        out.writeShort(changed.size());
        for (SlayerTask task : changed) {
            out.writeShort(task.getId());
            writeCombatProfile(out, settings.getTaskCombatProfile(task));
        }
    }

    private static void readTaskCombatProfileDiffs(DataInputStream in, SlayerSettings settings) throws IOException {
        int count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            SlayerTask task = SlayerTask.fromId(in.readUnsignedShort());
            SlayerCombatProfile profile = readCombatProfile(in);
            if (task != SlayerTask.UNKNOWN) {
                settings.setTaskCombatProfile(task, profile);
            }
        }
    }

    private static void writeCombatProfile(DataOutputStream out, SlayerCombatProfile profile) throws IOException {
        out.writeBoolean(profile.isUseFood());
        out.writeInt(profile.getHealThresholdPercent());
        out.writeBoolean(profile.isUsePrayerRestore());
        out.writeInt(profile.getPrayerThresholdPercent());
        out.writeBoolean(profile.isUsePrayerRenewal());
        out.writeBoolean(profile.isUseOverloads());
        out.writeUTF(profile.getPrayerMode().name());
        out.writeBoolean(profile.isUseCurses());
        out.writeUTF(profile.getAttackStyle().name());
        out.writeInt(profile.getHybridSoulSplitHealthPercent());
    }

    private static SlayerCombatProfile readCombatProfile(DataInputStream in) throws IOException {
        SlayerCombatProfile profile = new SlayerCombatProfile();
        profile.setUseFood(in.readBoolean());
        profile.setHealThresholdPercent(in.readInt());
        profile.setUsePrayerRestore(in.readBoolean());
        profile.setPrayerThresholdPercent(in.readInt());
        profile.setUsePrayerRenewal(in.readBoolean());
        profile.setUseOverloads(in.readBoolean());
        profile.setPrayerMode(parsePrayerMode(in.readUTF(), profile.getPrayerMode()));
        profile.setUseCurses(in.readBoolean());
        profile.setAttackStyle(parseAttackStyle(in.readUTF(), profile.getAttackStyle()));
        profile.setHybridSoulSplitHealthPercent(in.readInt());
        return profile;
    }

    private static boolean combatProfilesEqual(SlayerCombatProfile left, SlayerCombatProfile right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return left.isUseFood() == right.isUseFood()
                && left.getHealThresholdPercent() == right.getHealThresholdPercent()
                && left.isUsePrayerRestore() == right.isUsePrayerRestore()
                && left.getPrayerThresholdPercent() == right.getPrayerThresholdPercent()
                && left.isUsePrayerRenewal() == right.isUsePrayerRenewal()
                && left.isUseOverloads() == right.isUseOverloads()
                && left.getPrayerMode() == right.getPrayerMode()
                && left.isUseCurses() == right.isUseCurses()
                && left.getAttackStyle() == right.getAttackStyle()
                && left.getHybridSoulSplitHealthPercent() == right.getHybridSoulSplitHealthPercent();
    }

    private static SlayerMaster parseMaster(String value, SlayerMaster fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return SlayerMaster.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private static SlayerPrayerMode parsePrayerMode(String value, SlayerPrayerMode fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return SlayerPrayerMode.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private static SlayerAttackStyle parseAttackStyle(String value, SlayerAttackStyle fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return SlayerAttackStyle.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private static byte[] compress(byte[] raw) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflater = new DeflaterOutputStream(out, new Deflater(Deflater.BEST_COMPRESSION))) {
            deflater.write(raw);
        }
        return out.toByteArray();
    }

    private static byte[] decompress(byte[] compressed) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(compressed))) {
            byte[] buffer = new byte[1024];
            int total = 0;
            int read;
            while ((read = inflater.read(buffer)) != -1) {
                total += read;
                if (total > MAX_DECOMPRESSED_BYTES) {
                    throw new IOException("Share payload exceeded maximum size");
                }
                out.write(buffer, 0, read);
            }
        }
        return out.toByteArray();
    }

    private static long calculateChecksum(byte[] payload) {
        CRC32 crc32 = new CRC32();
        crc32.update(payload);
        return crc32.getValue();
    }

    private static String toHex(long value) {
        return String.format("%08X", value);
    }

    private static long parseHex(String text) {
        return Long.parseLong(text, 16);
    }

    public record DecodeResult(boolean success, String message, SlayerSettings settings) {
        private static DecodeResult success(SlayerSettings settings) {
            return new DecodeResult(true, "OK", settings);
        }

        private static DecodeResult failure(String message) {
            return new DecodeResult(false, message, null);
        }
    }

    private enum TaskBooleanKind {
        TELEPORT,
        SURGE,
        DIVE,
        COMBAT_OVERRIDE
    }

    private enum TaskIntegerKind {
        COMBAT_RADIUS
    }
}
