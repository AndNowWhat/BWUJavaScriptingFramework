package com.botwithus.bot.core;

import com.botwithus.bot.api.BotScript;
import com.botwithus.bot.core.impl.EventBusImpl;
import com.botwithus.bot.core.impl.GameAPIImpl;
import com.botwithus.bot.core.impl.ScriptContextImpl;
import com.botwithus.bot.core.pipe.PipeClient;
import com.botwithus.bot.core.rpc.RpcClient;
import com.botwithus.bot.core.runtime.ScriptLoader;
import com.botwithus.bot.core.runtime.ScriptRuntime;

import java.util.List;

/**
 * Entry point: connects the pipe, loads scripts, and starts the runtime.
 */
public class JBotApplication {

    public static void main(String[] args) {
        System.out.println("[JBot] Connecting to BotWithUs pipe...");
        try (PipeClient pipe = new PipeClient()) {
            RpcClient rpc = new RpcClient(pipe);
            EventBusImpl eventBus = new EventBusImpl();
            GameAPIImpl gameAPI = new GameAPIImpl(rpc);
            ScriptContextImpl context = new ScriptContextImpl(gameAPI, eventBus);

            // Route pipe events to the event bus
            rpc.setEventHandler(event -> {
                // Events can be extended here to dispatch typed events
                String eventType = (String) event.get("event");
                System.out.println("[JBot] Received event: " + eventType);
            });

            // Discover scripts from scripts/ directory (drop JARs there)
            List<BotScript> scripts = ScriptLoader.loadScripts();
            System.out.println("[JBot] Discovered " + scripts.size() + " script(s)");

            ScriptRuntime runtime = new ScriptRuntime(context);
            runtime.startAll(scripts);

            // Keep main thread alive until interrupted
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[JBot] Shutting down...");
                runtime.stopAll();
                rpc.close();
            }));

            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[JBot] Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
