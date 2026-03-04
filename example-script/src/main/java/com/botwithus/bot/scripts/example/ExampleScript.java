package com.botwithus.bot.scripts.example;

import com.botwithus.bot.api.BotScript;
import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.ScriptContext;
import com.botwithus.bot.api.ScriptManifest;

@ScriptManifest(
        name = "Example Script",
        version = "1.0",
        author = "BotWithUs",
        description = "A demo script showing ServiceLoader integration"
)
public class ExampleScript implements BotScript {

    private ScriptContext ctx;
    private int loopCount;

    @Override
    public void onStart(ScriptContext ctx) {
        this.ctx = ctx;
        this.loopCount = 0;
        System.out.println("[ExampleScript] Started!");
    }

    @Override
    public int onLoop() {
        loopCount++;
        GameAPI api = ctx.getGameAPI();

        System.out.println("[ExampleScript] Loop #" + loopCount
                + " | Game cycle: " + api.getGameCycle()
                + " | Login state: " + api.getLoginState().state());

        if (loopCount >= 10) {
            System.out.println("[ExampleScript] Completed 10 loops, stopping.");
            return -1;
        }

        return 1000; // 1 second delay
    }

    @Override
    public void onStop() {
        System.out.println("[ExampleScript] Stopped after " + loopCount + " loops.");
    }
}
