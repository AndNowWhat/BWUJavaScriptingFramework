package com.botwithus.bot.api;

/**
 * Service Provider Interface for bot scripts.
 * Discovered via {@link java.util.ServiceLoader}.
 */
public interface BotScript {

    void onStart(ScriptContext ctx);

    /**
     * @return delay in milliseconds before the next loop iteration, or -1 to stop
     */
    int onLoop();

    void onStop();
}
