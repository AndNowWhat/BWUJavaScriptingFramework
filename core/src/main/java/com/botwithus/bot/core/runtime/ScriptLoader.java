package com.botwithus.bot.core.runtime;

import com.botwithus.bot.api.BotScript;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Discovers BotScript implementations from JAR files in a scripts directory.
 * Each JAR is a Java module that {@code provides BotScript with ...}.
 * Loaded into a child ModuleLayer so ServiceLoader can find them.
 */
public final class ScriptLoader {

    private static final String DEFAULT_SCRIPTS_DIR = "scripts";

    private ScriptLoader() {}

    /**
     * Loads all BotScript providers from JARs in the default {@code scripts/} directory.
     */
    public static List<BotScript> loadScripts() {
        return loadScripts(Path.of(DEFAULT_SCRIPTS_DIR));
    }

    /**
     * Loads all BotScript providers from JARs in the given directory.
     */
    public static List<BotScript> loadScripts(Path scriptsDir) {
        if (!Files.isDirectory(scriptsDir)) {
            System.out.println("[ScriptLoader] Scripts directory not found: " + scriptsDir.toAbsolutePath());
            System.out.println("[ScriptLoader] Creating it — drop script JARs there and restart.");
            try {
                Files.createDirectories(scriptsDir);
            } catch (IOException e) {
                System.err.println("[ScriptLoader] Failed to create scripts directory: " + e.getMessage());
            }
            return List.of();
        }

        // Find all JARs in the scripts directory
        List<Path> jars;
        try (var stream = Files.list(scriptsDir)) {
            jars = stream.filter(p -> p.toString().endsWith(".jar")).toList();
        } catch (IOException e) {
            System.err.println("[ScriptLoader] Failed to scan scripts directory: " + e.getMessage());
            return List.of();
        }

        if (jars.isEmpty()) {
            System.out.println("[ScriptLoader] No JARs found in " + scriptsDir.toAbsolutePath());
            return List.of();
        }

        System.out.println("[ScriptLoader] Found " + jars.size() + " JAR(s) in " + scriptsDir.toAbsolutePath());

        // Build a ModuleLayer from the script JARs
        ModuleLayer bootLayer = ModuleLayer.boot();
        ModuleFinder finder = ModuleFinder.of(scriptsDir);
        // Resolve all modules found in the scripts directory
        List<String> moduleNames = finder.findAll().stream()
                .map(ref -> ref.descriptor().name())
                .toList();

        if (moduleNames.isEmpty()) {
            System.out.println("[ScriptLoader] No valid Java modules found in JARs. "
                    + "Ensure each JAR has a module-info with 'provides BotScript with ...'");
            return List.of();
        }

        System.out.println("[ScriptLoader] Resolved modules: " + moduleNames);

        Configuration cfg = bootLayer.configuration().resolve(
                finder, ModuleFinder.of(), moduleNames
        );

        ModuleLayer scriptLayer = bootLayer.defineModulesWithOneLoader(
                cfg, ClassLoader.getSystemClassLoader()
        );

        // Discover BotScript providers from the script layer
        List<BotScript> scripts = new ArrayList<>();
        ServiceLoader<BotScript> loader = ServiceLoader.load(scriptLayer, BotScript.class);
        for (BotScript script : loader) {
            scripts.add(script);
        }
        return scripts;
    }
}
