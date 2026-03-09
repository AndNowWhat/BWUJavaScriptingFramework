package com.botwithus.bot.scripts.slayerwithus.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum SlayerTask {
    NOTHING(0, "Nothing"),
    MONKEYS(1, "Monkeys"),
    GOBLINS(2, "Goblins"),
    RATS(3, "Rats"),
    SPIDERS(4, "Spiders"),
    BIRDS(5, "Birds"),
    COWS(6, "Cows"),
    SCORPIONS(7, "Scorpions"),
    BATS(8, "Bats"),
    WOLVES(9, "Wolves"),
    ZOMBIES(10, "Zombies"),
    SKELETONS(11, "Skeletons"),
    GHOSTS(12, "Ghosts"),
    BEARS(13, "Bears"),
    HILL_GIANTS(14, "Hill giants"),
    ICE_GIANTS(15, "Ice giants"),
    FIRE_GIANTS(16, "Fire giants"),
    MOSS_GIANTS(17, "Moss giants"),
    TROLLS(18, "Trolls"),
    ICE_WARRIORS(19, "Ice warriors"),
    OGRES(20, "Ogres"),
    HOBGOBLINS(21, "Hobgoblins"),
    DOGS(22, "Dogs"),
    GHOULS(23, "Ghouls"),
    GREEN_DRAGONS(24, "Green dragons"),
    BLUE_DRAGONS(25, "Blue dragons"),
    RED_DRAGONS(26, "Red dragons"),
    BLACK_DRAGONS(27, "Black dragons"),
    LESSER_DEMONS(28, "Lesser demons"),
    GREATER_DEMONS(29, "Greater demons"),
    BLACK_DEMONS(30, "Black demons"),
    HELLHOUNDS(31, "Hellhounds"),
    SHADOW_WARRIORS(32, "Shadow warriors"),
    WEREWOLVES(33, "Werewolves"),
    VAMPYRES(34, "Vampyres"),
    DAGANNOTH(35, "Dagannoth"),
    TUROTH(36, "Turoth"),
    CAVE_CRAWLERS(37, "Cave crawlers"),
    BANSHEES(38, "Banshees"),
    CRAWLING_HANDS(39, "Crawling hands"),
    INFERNAL_MAGES(40, "Infernal mages"),
    ABERRANT_SPECTRES(41, "Aberrant spectres"),
    ABYSSAL_DEMONS_42(42, "Abyssal Demons"),
    BASILISKS(43, "Basilisks"),
    COCKATRICE(44, "Cockatrice"),
    KURASK(45, "Kurask"),
    GARGOYLES(46, "Gargoyles"),
    PYREFIENDS(47, "Pyrefiends"),
    BLOODVELD(48, "Bloodveld"),
    DUST_DEVILS(49, "Dust devils"),
    JELLIES(50, "Jellies"),
    ROCKSLUGS(51, "Rockslugs"),
    NECHRYAEL(52, "Nechryael"),
    KALPHITE(53, "Kalphite"),
    EARTH_WARRIORS(54, "Earth warriors"),
    OTHERWORLDLY_BEINGS(55, "Otherworldly beings"),
    ELVES(56, "Elves"),
    DWARVES(57, "Dwarves"),
    BRONZE_DRAGONS(58, "Bronze dragons"),
    IRON_DRAGONS(59, "Iron dragons"),
    STEEL_DRAGONS(60, "Steel dragons"),
    WALL_BEASTS(61, "Wall beasts"),
    CAVE_SLIMES(62, "Cave slimes"),
    CAVE_BUGS(63, "Cave bugs"),
    SHADES(64, "Shades"),
    CROCODILES(65, "Crocodiles"),
    DARK_BEASTS(66, "Dark beasts"),
    MOGRES(67, "Mogres"),
    DESERT_LIZARDS(68, "Desert lizards"),
    FEVER_SPIDERS(69, "Fever spiders"),
    HARPIE_BUG_SWARMS(70, "Harpie bug swarms"),
    SEA_SNAKES(71, "Sea snakes"),
    SKELETAL_WYVERNS(72, "Skeletal wyverns"),
    KILLERWATTS(73, "Killerwatts"),
    MUTATED_ZYGOMITES(74, "Mutated zygomites"),
    ICEFIENDS(75, "Icefiends"),
    MINOTAURS(76, "Minotaurs"),
    FLESHCRAWLERS(77, "Fleshcrawlers"),
    CATABLEPON(78, "Catablepon"),
    ANKOU(79, "Ankou"),
    CAVE_HORRORS(80, "Cave horrors"),
    JUNGLE_HORRORS(81, "Jungle horrors"),
    GORAKS(82, "Goraks"),
    SUQAHS(83, "Suqahs"),
    BRINE_RATS(84, "Brine rats"),
    SCABARITES(85, "Scabarites"),
    TERROR_DOGS(86, "Terror dogs"),
    MOLANISKS(87, "Molanisks"),
    WATERFIENDS(88, "Waterfiends"),
    SPIRITUAL_WARRIORS(89, "Spiritual warriors"),
    SPIRITUAL_RANGERS(90, "Spiritual rangers"),
    SPIRITUAL_MAGES(91, "Spiritual mages"),
    WARPED_TORTOISES(92, "Warped tortoises"),
    WARPED_TERRORBIRDS(93, "Warped terrorbirds"),
    MITHRIL_DRAGONS(94, "Mithril dragons"),
    AQUANITES(95, "Aquanites"),
    GANODERMIC_CREATURES(96, "Ganodermic creatures"),
    GRIFOLAPINES(97, "Grifolapines"),
    GRIFOLAROOS(98, "Grifolaroos"),
    FUNGAL_MAGI(99, "Fungal magi"),
    POLYPORE_CREATURES(100, "Polypore creatures"),
    TZHAAR(101, "TzHaar"),
    VOLCANIC_CREATURES(102, "Volcanic creatures"),
    JUNGLE_STRYKEWYRMS(103, "Jungle strykewyrms"),
    DESERT_STRYKEWYRMS(104, "Desert strykewyrms"),
    ICE_STRYKEWYRMS(105, "Ice strykewyrms"),
    LIVING_ROCK_CREATURES(106, "Living rock creatures"),
    CYCLOPES(108, "Cyclopes"),
    MUTATED_JADINKOS(109, "Mutated jadinkos"),
    VYREWATCH(110, "Vyrewatch"),
    GELATINOUS_ABOMINATIONS(111, "Gelatinous abominations"),
    GROTWORMS(112, "Grotworms"),
    CRESS_CREATIONS(113, "Cres's creations"),
    AVIANSIES(114, "Aviansies"),
    ASCENSION_MEMBERS(115, "Ascension members"),
    PIGS(116, "Pigs"),
    AIRUT(117, "Airut"),
    CELESTIAL_DRAGONS(118, "Celestial dragons"),
    MUSPAH(119, "Muspah"),
    NIHIL(120, "Nihil"),
    KALGERION_DEMONS(121, "Kal'gerion demons"),
    GLACORS(122, "Glacors"),
    TORMENTED_DEMONS(123, "Tormented demons"),
    EDIMMU(124, "Edimmu"),
    SHADOW_CREATURES(125, "Shadow creatures"),
    LAVA_STRYKEWYRMS(126, "Lava strykewyrms"),
    ADAMANT_DRAGONS(127, "Adamant dragons"),
    RUNE_DRAGONS(128, "Rune dragons"),
    CRYSTAL_SHAPESHIFTERS(129, "Crystal shapeshifters"),
    LIVING_WYVERNS(130, "Living wyverns"),
    RIPPER_DEMONS(131, "Ripper demons"),
    CAMEL_WARRIORS(132, "Camel warriors"),
    ACHERON_MAMMOTHS(133, "Acheron mammoths"),
    CHAOS_GIANTS(134, "Chaos giants"),
    NIGHTMARE_CREATURES(135, "Nightmare creatures"),
    ONYX_DRAGONS(137, "Onyx dragons"),
    HYDRIX_DRAGONS(138, "Hydrix dragons"),
    GEMSTONE_DRAGONS(139, "Gemstone dragons"),
    CORRUPTED_SCORPIONS(140, "Corrupted scorpions"),
    CORRUPTED_SCARABS(141, "Corrupted scarabs"),
    CORRUPTED_LIZARDS(142, "Corrupted lizards"),
    CORRUPTED_DUST_DEVILS(143, "Corrupted dust devils"),
    CORRUPTED_KALPHITES(144, "Corrupted kalphites"),
    CORRUPTED_WORKER(145, "Corrupted worker"),
    SALAWA_AKH(146, "Salawa akh"),
    FELINE_AKH(147, "Feline akh"),
    GORILLA_AKH(148, "Gorilla akh"),
    CROCODILE_AKH(149, "Crocodile akh"),
    SCARAB_AKH(150, "Scarab akh"),
    IMPERIAL_GUARD_AKH(151, "Imperial guard akh"),
    CORRUPTED_CREATURES(152, "Corrupted creatures"),
    SOUL_DEVOURERS(153, "Soul devourers"),
    CREATURES_OF_THE_LOST_GROVE(154, "Creatures of the Lost Grove"),
    SOULGAZERS(159, "Soulgazers"),
    STALKER_CREATURES(160, "Stalker creatures"),
    REVENANTS(161, "Revenants"),
    FROGS(162, "Frogs"),
    DINOSAURS(171, "Dinosaurs"),
    VILE_BLOOMS(172, "Vile blooms"),
    DRAGONS(173, "Dragons"),
    DEMONS(174, "Demons"),
    CREATURES_OF_DAEMONHEIM(175, "Creatures of Daemonheim"),
    ZAROSIAN_CREATURES(176, "Zarosian creatures"),
    STRYKEWYRMS(177, "Strykewyrms"),
    COCKROACHES(178, "Cockroaches"),
    NODON_DRAGONKIN(179, "Nodon dragonkin"),
    ABYSSAL_DEMONS_180(180, "Abyssal Demons"),
    ABYSSAL_SAVAGES(181, "Abyssal savages"),
    ABYSSAL_BEASTS(182, "Abyssal beasts"),
    ABYSSAL_LORDS(183, "Abyssal lords"),
    GREATER_DEMON_BERSERKERS_AND_ASH_LORDS(184, "Greater demon berserkers and ash lords"),
    FETID_ZOMBIES(185, "Fetid zombies"),
    BOUND_SKELETONS(186, "Bound skeletons"),
    RISEN_GHOSTS(187, "Risen ghosts"),
    ARMOURED_PHANTOMS(188, "Armoured phantoms"),
    ZEMOUREGALS_UNDEAD(189, "Zemouregal's undead"),
    PHANTOMS(190, "Phantoms"),
    UNDEAD(191, "Undead"),
    PROFANE_SCABARITES(192, "Profane Scabarites"),
    BEASTMASTERS_HOUNDS(194, "Beastmaster's hounds"),
    UNKNOWN(-1, "Unknown");

    private static final Map<Integer, SlayerTask> BY_ID = new HashMap<>();

    static {
        for (SlayerTask task : values()) {
            BY_ID.put(task.id, task);
        }
    }

    private final int id;
    private final String displayName;

    SlayerTask(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SlayerTask fromId(int id) {
        return BY_ID.getOrDefault(id, UNKNOWN);
    }

    public static Optional<SlayerTask> fromDialogOption(String optionText) {
        if (optionText == null || optionText.isBlank()) {
            return Optional.empty();
        }
        String normalized = optionText.toLowerCase();
        for (SlayerTask task : values()) {
            if (task == UNKNOWN || task == NOTHING) {
                continue;
            }
            if (normalized.contains(task.displayName.toLowerCase())) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }
}
