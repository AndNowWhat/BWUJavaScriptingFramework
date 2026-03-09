package com.botwithus.bot.scripts.slayerwithus.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SlayerTaskMonsterRegistry {
    private static final EnumMap<SlayerTask, List<String>> DEFAULT_MONSTER_NAMES = loadDefaultMonsterNames();
    private static final EnumMap<SlayerTask, List<String>> SPECIAL_MONSTER_VARIANTS = loadSpecialMonsterVariants();

    private SlayerTaskMonsterRegistry() {
    }

    public static List<String> getDefaultMonsterNames(SlayerTask task) {
        if (task == null) {
            return List.of();
        }
        return DEFAULT_MONSTER_NAMES.getOrDefault(task, List.of(task.getDisplayName()));
    }

    public static List<String> getPrioritizedMonsterNames(SlayerTask task, List<String> configuredNames) {
        Set<String> prioritizedNames = new LinkedHashSet<>();
        prioritizedNames.addAll(getSpecialVariants(task));
        if (configuredNames != null && !configuredNames.isEmpty()) {
            prioritizedNames.addAll(configuredNames);
        } else {
            prioritizedNames.addAll(getDefaultMonsterNames(task));
        }
        return List.copyOf(prioritizedNames);
    }

    public static List<String> getSpecialVariants(SlayerTask task) {
        if (task == null) {
            return List.of();
        }
        return SPECIAL_MONSTER_VARIANTS.getOrDefault(task, List.of());
    }

    private static EnumMap<SlayerTask, List<String>> loadDefaultMonsterNames() {
        EnumMap<SlayerTask, List<String>> names = new EnumMap<>(SlayerTask.class);

        register(names, SlayerTask.ABERRANT_SPECTRES, "Aberrant spectre");
        register(names, SlayerTask.ABYSSAL_BEASTS, "Abyssal beast");
        register(names, SlayerTask.ABYSSAL_DEMONS_42, "Abyssal demon");
        register(names, SlayerTask.ABYSSAL_DEMONS_180, "Abyssal demon");
        register(names, SlayerTask.ABYSSAL_LORDS, "Abyssal lord");
        register(names, SlayerTask.ABYSSAL_SAVAGES, "Abyssal savage");
        register(names, SlayerTask.ACHERON_MAMMOTHS, "Acheron mammoth");
        register(names, SlayerTask.ADAMANT_DRAGONS, "Adamant dragon");
        register(names, SlayerTask.AIRUT, "Airut");
        register(names, SlayerTask.ANKOU, "Ankou");
        register(names, SlayerTask.AQUANITES, "Aquanite");
        register(names, SlayerTask.ARMOURED_PHANTOMS, "Armoured phantom");
        register(names, SlayerTask.ASCENSION_MEMBERS, "Rorarius");
        register(names, SlayerTask.AVIANSIES, "Aviansie");
        register(names, SlayerTask.BANSHEES, "Banshee");
        register(names, SlayerTask.BASILISKS, "Basilisk");
        register(names, SlayerTask.BATS, "Warped bat");
        register(names, SlayerTask.BEASTMASTERS_HOUNDS, "Beastmaster's hound");
        register(names, SlayerTask.BEARS, "Black bear");
        register(names, SlayerTask.BLACK_DEMONS, "Black demon");
        register(names, SlayerTask.BLACK_DRAGONS, "Black dragon");
        register(names, SlayerTask.BLOODVELD, "Bloodveld");
        register(names, SlayerTask.BLUE_DRAGONS, "Blue dragon");
        register(names, SlayerTask.BOUND_SKELETONS, "Bound skeleton");
        register(names, SlayerTask.BRINE_RATS, "Brine rat");
        register(names, SlayerTask.BRONZE_DRAGONS, "Bronze dragon");
        register(names, SlayerTask.CAMEL_WARRIORS, "Camel Warrior");
        register(names, SlayerTask.CATABLEPON, "Catablepon");
        register(names, SlayerTask.CAVE_BUGS, "Cave bug");
        register(names, SlayerTask.CAVE_CRAWLERS, "Cave crawler");
        register(names, SlayerTask.CAVE_HORRORS, "Cave horror");
        register(names, SlayerTask.CAVE_SLIMES, "Cave slime");
        register(names, SlayerTask.CELESTIAL_DRAGONS, "Celestial dragon");
        register(names, SlayerTask.CHAOS_GIANTS, "Chaos Giant");
        register(names, SlayerTask.COCKATRICE, "Cockatrice");
        register(names, SlayerTask.COCKROACHES, "Cockroach soldier");
        register(names, SlayerTask.CORRUPTED_CREATURES, "Corrupted scorpion");
        register(names, SlayerTask.COWS, "Cow");
        register(names, SlayerTask.CRAWLING_HANDS, "Crawling hand");
        register(names, SlayerTask.CREATURES_OF_DAEMONHEIM, "Kal'gerion demon");
        register(names, SlayerTask.CRESS_CREATIONS, "Automaton Generator");
        register(names, SlayerTask.CROCODILES, "Crocodile");
        register(names, SlayerTask.CRYSTAL_SHAPESHIFTERS, "Crystal Shapeshifter");
        register(names, SlayerTask.CYCLOPES, "Cyclops");
        register(names, SlayerTask.DAGANNOTH, "Dagannoth");
        register(names, SlayerTask.DARK_BEASTS, "Dark beast");
        register(names, SlayerTask.DESERT_LIZARDS, "Desert lizard");
        register(names, SlayerTask.DESERT_STRYKEWYRMS, "Desert strykewyrm");
        register(names, SlayerTask.DEMONS, "Abyssal demon");
        register(names, SlayerTask.DINOSAURS, "Feral Dinosaur");
        register(names, SlayerTask.DOGS, "Guard dog");
        register(names, SlayerTask.DRAGONS, "Green dragon");
        register(names, SlayerTask.DUST_DEVILS, "Dust devil");
        register(names, SlayerTask.EARTH_WARRIORS, "Earth warrior");
        register(names, SlayerTask.EDIMMU, "Edimmu");
        register(names, SlayerTask.ELVES, "Elf warrior");
        register(names, SlayerTask.FETID_ZOMBIES, "Fetid zombie");
        register(names, SlayerTask.FEVER_SPIDERS, "Fever spider");
        register(names, SlayerTask.FIRE_GIANTS, "Fire giant");
        register(names, SlayerTask.FLESHCRAWLERS, "Flesh Crawler");
        register(names, SlayerTask.FROGS, "Big frog");
        register(names, SlayerTask.FUNGAL_MAGI, "Fungal mage");
        register(names, SlayerTask.GANODERMIC_CREATURES, "Ganodermic beast");
        register(names, SlayerTask.GARGOYLES, "Gargoyle");
        register(names, SlayerTask.GELATINOUS_ABOMINATIONS, "Gelatinous abomination");
        register(names, SlayerTask.GEMSTONE_DRAGONS, "Gemstone dragon");
        register(names, SlayerTask.GHOSTS, "Ghost");
        register(names, SlayerTask.GHOULS, "Ghoul");
        register(names, SlayerTask.GLACORS, "Glacor");
        register(names, SlayerTask.GOBLINS, "Skoblin");
        register(names, SlayerTask.GORAKS, "Gorak");
        register(names, SlayerTask.GREATER_DEMONS, "Greater demon");
        register(names, SlayerTask.GREEN_DRAGONS, "Green dragon");
        register(names, SlayerTask.GRIFOLAPINES, "Grifolapine");
        register(names, SlayerTask.GRIFOLAROOS, "Grifolaroo");
        register(names, SlayerTask.GROTWORMS, "Young grotworm");
        register(names, SlayerTask.HARPIE_BUG_SWARMS, "Harpie bug swarm");
        register(names, SlayerTask.HELLHOUNDS, "Hellhound");
        register(names, SlayerTask.HILL_GIANTS, "Hill Giant");
        register(names, SlayerTask.HOBGOBLINS, "Hobgoblin");
        register(names, SlayerTask.ICE_GIANTS, "Ice giant");
        register(names, SlayerTask.ICE_STRYKEWYRMS, "Ice strykewyrm");
        register(names, SlayerTask.ICE_WARRIORS, "Ice warrior");
        register(names, SlayerTask.ICEFIENDS, "Icefiend");
        register(names, SlayerTask.INFERNAL_MAGES, "Infernal Mage");
        register(names, SlayerTask.IRON_DRAGONS, "Iron dragon");
        register(names, SlayerTask.JELLIES, "Jelly");
        register(names, SlayerTask.JUNGLE_HORRORS, "Jungle horror");
        register(names, SlayerTask.JUNGLE_STRYKEWYRMS, "Jungle strykewyrm");
        register(names, SlayerTask.KALGERION_DEMONS, "Kal'gerion demon");
        register(names, SlayerTask.KALPHITE, "Kalphite Soldier");
        register(names, SlayerTask.KILLERWATTS, "Killerwatt");
        register(names, SlayerTask.KURASK, "Kurask");
        register(names, SlayerTask.LAVA_STRYKEWYRMS, "Lava strykewyrm");
        register(names, SlayerTask.LESSER_DEMONS, "Lesser demon");
        register(names, SlayerTask.LIVING_ROCK_CREATURES, "Living rock protector");
        register(names, SlayerTask.LIVING_WYVERNS, "Wyvern");
        register(names, SlayerTask.CREATURES_OF_THE_LOST_GROVE, "Vinecrawler");
        register(names, SlayerTask.MITHRIL_DRAGONS, "Mithril dragon");
        register(names, SlayerTask.MINOTAURS, "Minotaur");
        register(names, SlayerTask.MOGRES, "Mogre");
        register(names, SlayerTask.MOLANISKS, "Molanisk");
        register(names, SlayerTask.MOSS_GIANTS, "Moss giant");
        register(names, SlayerTask.MUSPAH, "Muspah");
        register(names, SlayerTask.MUTATED_JADINKOS, "Mutated jadinko baby");
        register(names, SlayerTask.MUTATED_ZYGOMITES, "Mutated zygomite");
        register(names, SlayerTask.NECHRYAEL, "Nechryael");
        register(names, SlayerTask.NIGHTMARE_CREATURES, "Nightmare creature");
        register(names, SlayerTask.NIHIL, "Nihil");
        register(names, SlayerTask.NODON_DRAGONKIN, "Nodon guard");
        register(names, SlayerTask.OGRES, "Ogre");
        register(names, SlayerTask.ONYX_DRAGONS, "Onyx dragon");
        register(names, SlayerTask.OTHERWORLDLY_BEINGS, "Otherworldly being");
        register(names, SlayerTask.PROFANE_SCABARITES, "Profane Scabarite");
        register(names, SlayerTask.PYREFIENDS, "Pyrefiend");
        register(names, SlayerTask.RATS, "Warped rat");
        register(names, SlayerTask.RED_DRAGONS, "Red dragon");
        register(names, SlayerTask.REVENANTS, "Revenant");
        register(names, SlayerTask.RIPPER_DEMONS, "Ripper demon");
        register(names, SlayerTask.RISEN_GHOSTS, "Risen ghost");
        register(names, SlayerTask.ROCKSLUGS, "Rock slug");
        register(names, SlayerTask.RUNE_DRAGONS, "Rune dragon");
        register(names, SlayerTask.SCABARITES, "Scarab mage");
        register(names, SlayerTask.SCORPIONS, "Scorpion");
        register(names, SlayerTask.SEA_SNAKES, "Sea Snake Young");
        register(names, SlayerTask.SHADES, "Shade");
        register(names, SlayerTask.SHADOW_CREATURES, "Truthful shadow");
        register(names, SlayerTask.SHADOW_WARRIORS, "Shadow warrior");
        register(names, SlayerTask.SKELETAL_WYVERNS, "Skeletal wyvern");
        register(names, SlayerTask.SKELETONS, "Skeleton");
        register(names, SlayerTask.SOUL_DEVOURERS, "Salawa akh");
        register(names, SlayerTask.SOULGAZERS, "Soulgazer");
        register(names, SlayerTask.SPIDERS, "Corpse spider");
        register(names, SlayerTask.SPIRITUAL_MAGES, "Spiritual mage");
        register(names, SlayerTask.SPIRITUAL_WARRIORS, "Spiritual warrior");
        register(names, SlayerTask.STALKER_CREATURES, "Seeker");
        register(names, SlayerTask.STEEL_DRAGONS, "Steel dragon");
        register(names, SlayerTask.STRYKEWYRMS, "Jungle strykewyrm");
        register(names, SlayerTask.SUQAHS, "Suqah");
        register(names, SlayerTask.TERROR_DOGS, "Terror dog");
        register(names, SlayerTask.TORMENTED_DEMONS, "Tormented demon");
        register(names, SlayerTask.TROLLS, "Troll shaman");
        register(names, SlayerTask.BIRDS, "Chicken");
        register(names, SlayerTask.TUROTH, "Turoth");
        register(names, SlayerTask.TZHAAR, "TzHaar-Ket");
        register(names, SlayerTask.UNDEAD, "Ankou");
        register(names, SlayerTask.VAMPYRES, "Feral vampyre");
        register(names, SlayerTask.VILE_BLOOMS, "Devil's snare");
        register(names, SlayerTask.VYREWATCH, "Vyrewatch");
        register(names, SlayerTask.WALL_BEASTS, "Wall beast");
        register(names, SlayerTask.WARPED_TERRORBIRDS, "Warped terrorbird");
        register(names, SlayerTask.WARPED_TORTOISES, "Warped tortoise");
        register(names, SlayerTask.WATERFIENDS, "Waterfiend");
        register(names, SlayerTask.WEREWOLVES, "Werewolf");
        register(names, SlayerTask.WOLVES, "Wolf");
        register(names, SlayerTask.ZEMOUREGALS_UNDEAD, "Bound skeleton");
        register(names, SlayerTask.ZOMBIES, "Corpse spider");

        for (Map.Entry<SlayerTask, List<String>> entry : names.entrySet()) {
            entry.setValue(Collections.unmodifiableList(entry.getValue()));
        }

        return names;
    }

    private static EnumMap<SlayerTask, List<String>> loadSpecialMonsterVariants() {
        EnumMap<SlayerTask, List<String>> variants = new EnumMap<>(SlayerTask.class);

        registerVariants(variants, SlayerTask.LIVING_ROCK_CREATURES,
                "Living rock striker",
                "Living rock patriarch");

        registerVariants(variants, SlayerTask.TZHAAR,
                "TzHaar-Mej",
                "TzHaar-Hur");

        registerVariants(variants, SlayerTask.TERROR_DOGS,
                "Mutant tarn",
                "Tarn");

        registerVariants(variants, SlayerTask.MUSPAH,
                "Force muspah",
                "Throwing muspah",
                "Bladed muspah");

        registerVariants(variants, SlayerTask.NIHIL,
                "Ice nihil",
                "Shadow nihil",
                "Smoke nihil",
                "Blood nihil");

        registerVariants(variants, SlayerTask.CAMEL_WARRIORS,
                "Smoke mirage",
                "Blood mirage",
                "Shadow mirage",
                "Camel Warrior");

        registerVariants(variants, SlayerTask.SHADOW_CREATURES,
                "Manifest shadow",
                "Blissful shadow");

        registerEliteVariants(variants, SlayerTask.CAVE_HORRORS, "Cave horror");
        registerEliteVariants(variants, SlayerTask.ABYSSAL_DEMONS_42, "Abyssal demon");
        registerEliteVariants(variants, SlayerTask.ABYSSAL_DEMONS_180, "Abyssal demon");
        registerEliteVariants(variants, SlayerTask.GREATER_DEMONS, "Greater demon");
        registerEliteVariants(variants, SlayerTask.DARK_BEASTS, "Dark beast");
        registerEliteVariants(variants, SlayerTask.WATERFIENDS, "Waterfiend");
        registerEliteVariants(variants, SlayerTask.ANKOU, "Ankou");
        registerEliteVariants(variants, SlayerTask.AQUANITES, "Aquanite");
        registerEliteVariants(variants, SlayerTask.DUST_DEVILS, "Dust devil");
        registerEliteVariants(variants, SlayerTask.EARTH_WARRIORS, "Earth warrior");
        registerEliteVariants(variants, SlayerTask.HARPIE_BUG_SWARMS, "Harpie bug swarm");
        registerEliteVariants(variants, SlayerTask.HOBGOBLINS, "Hobgoblin");
        registerEliteVariants(variants, SlayerTask.INFERNAL_MAGES, "Infernal Mage");
        registerEliteVariants(variants, SlayerTask.NECHRYAEL, "Nechryael");
        registerEliteVariants(variants, SlayerTask.LIVING_WYVERNS, "Wyvern");

        registerVariants(variants, SlayerTask.RIPPER_DEMONS, "Slasher Demon");
        registerVariants(variants, SlayerTask.COWS, "Super Cow");

        for (Map.Entry<SlayerTask, List<String>> entry : variants.entrySet()) {
            entry.setValue(Collections.unmodifiableList(entry.getValue()));
        }

        return variants;
    }

    private static void register(Map<SlayerTask, List<String>> names, SlayerTask task, String... monsterNames) {
        if (task == null || monsterNames == null || monsterNames.length == 0) {
            return;
        }
        names.put(task, List.of(monsterNames));
    }

    private static void registerVariants(Map<SlayerTask, List<String>> names, SlayerTask task, String... monsterNames) {
        if (task == null || monsterNames == null || monsterNames.length == 0) {
            return;
        }
        names.put(task, new ArrayList<>(Arrays.asList(monsterNames)));
    }

    private static void registerEliteVariants(Map<SlayerTask, List<String>> names, SlayerTask task, String monsterName) {
        registerVariants(names, task, generateEliteVariants(monsterName).toArray(String[]::new));
    }

    private static List<String> generateEliteVariants(String monsterName) {
        List<String> variants = new ArrayList<>();
        List<String> prefixes = Arrays.asList(
                "Cruel", "Dangerous", "Infamous", "Monstrous",
                "Notorious", "Powerful", "Renowned"
        );
        List<String> suffixes = Arrays.asList(
                "of Death", "of Doom", "of Fear", "of Pain",
                "of Peril", "of Suffering"
        );

        for (String prefix : prefixes) {
            for (String suffix : suffixes) {
                variants.add(prefix + " " + monsterName + " " + suffix);
            }
        }
        return variants;
    }
}
