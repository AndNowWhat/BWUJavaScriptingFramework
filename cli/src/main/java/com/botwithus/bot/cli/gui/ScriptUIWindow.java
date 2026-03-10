package com.botwithus.bot.cli.gui;

import com.botwithus.bot.core.runtime.ScriptRunner;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

/**
 * Floating ImGui window that renders a script's custom {@link com.botwithus.bot.api.ui.ScriptUI}.
 * Opened via the Config button for scripts that implement {@code getUI()}.
 */
public class ScriptUIWindow {

    private ScriptRunner runner;
    private final ImBoolean open = new ImBoolean(false);

    public void open(ScriptRunner runner) {
        this.runner = runner;
        open.set(true);
        ImGui.setNextWindowSize(925, 690, ImGuiCond.FirstUseEver);
    }

    public boolean isOpen() {
        return open.get();
    }

    public void render() {
        if (!open.get() || runner == null) return;

        ImGui.setNextWindowSize(925, 690, ImGuiCond.FirstUseEver);
        if (ImGui.begin(runner.getScriptName() + " Config###scriptUIWindow", open,
                ImGuiWindowFlags.NoCollapse)) {
            try {
                runner.drawGui();
            } catch (Exception e) {
                ImGui.textColored(ImGuiTheme.RED_R, ImGuiTheme.RED_G, ImGuiTheme.RED_B, 1f,
                        "UI error: " + e.getMessage());
            }
        }
        ImGui.end();
    }
}
