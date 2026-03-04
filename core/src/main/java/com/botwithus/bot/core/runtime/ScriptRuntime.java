package com.botwithus.bot.core.runtime;

import com.botwithus.bot.api.BotScript;
import com.botwithus.bot.api.ScriptContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages multiple ScriptRunners and their lifecycles.
 */
public class ScriptRuntime {

    private final ScriptContext context;
    private final List<ScriptRunner> runners = new CopyOnWriteArrayList<>();

    public ScriptRuntime(ScriptContext context) {
        this.context = context;
    }

    public void startScript(BotScript script) {
        ScriptRunner runner = new ScriptRunner(script, context);
        runners.add(runner);
        runner.start();
        System.out.println("[Runtime] Started script: " + runner.getScriptName());
    }

    public void startAll(List<BotScript> scripts) {
        for (BotScript script : scripts) {
            startScript(script);
        }
    }

    public void stopAll() {
        for (ScriptRunner runner : runners) {
            runner.stop();
            System.out.println("[Runtime] Stopped script: " + runner.getScriptName());
        }
        runners.clear();
    }

    public List<ScriptRunner> getRunners() {
        return List.copyOf(runners);
    }
}
