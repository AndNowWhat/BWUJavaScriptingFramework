package com.botwithus.bot.cli.gui;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;

/**
 * Color constants and imgui style setup matching the original GuiTheme palette.
 * All colors in 0.0-1.0 float range.
 */
public final class ImGuiTheme {

    // Background & surface
    public static final float BG_R = 0x1a / 255f, BG_G = 0x1a / 255f, BG_B = 0x2e / 255f;
    public static final float INPUT_BG_R = 0x22 / 255f, INPUT_BG_G = 0x22 / 255f, INPUT_BG_B = 0x38 / 255f;
    public static final float SELECTION_R = 0x3a / 255f, SELECTION_G = 0x3a / 255f, SELECTION_B = 0x55 / 255f;

    // Text
    public static final float TEXT_R = 0xe0 / 255f, TEXT_G = 0xdc / 255f, TEXT_B = 0xd0 / 255f;
    public static final float DIM_TEXT_R = 0x88 / 255f, DIM_TEXT_G = 0x85 / 255f, DIM_TEXT_B = 0x7a / 255f;

    // Accent
    public static final float ACCENT_R = 0xc1 / 255f, ACCENT_G = 0x5f / 255f, ACCENT_B = 0x3c / 255f;

    // ANSI colors
    public static final float RED_R = 0xf8 / 255f, RED_G = 0x71 / 255f, RED_B = 0x71 / 255f;
    public static final float GREEN_R = 0x4a / 255f, GREEN_G = 0xde / 255f, GREEN_B = 0x80 / 255f;
    public static final float YELLOW_R = 0xfb / 255f, YELLOW_G = 0xbf / 255f, YELLOW_B = 0x24 / 255f;
    public static final float BLUE_R = 0x60 / 255f, BLUE_G = 0xa5 / 255f, BLUE_B = 0xfa / 255f;
    public static final float MAGENTA_R = 0xc0 / 255f, MAGENTA_G = 0x84 / 255f, MAGENTA_B = 0xfc / 255f;
    public static final float CYAN_R = 0x67 / 255f, CYAN_G = 0xe8 / 255f, CYAN_B = 0xf9 / 255f;

    private ImGuiTheme() {}

    /**
     * Map SGR color code (30-37) to float[]{r, g, b}.
     */
    public static float[] ansiColorFloat(int code) {
        return switch (code) {
            case 30 -> new float[]{BG_R, BG_G, BG_B};           // black → background
            case 31 -> new float[]{RED_R, RED_G, RED_B};
            case 32 -> new float[]{GREEN_R, GREEN_G, GREEN_B};
            case 33 -> new float[]{YELLOW_R, YELLOW_G, YELLOW_B};
            case 34 -> new float[]{BLUE_R, BLUE_G, BLUE_B};
            case 35 -> new float[]{MAGENTA_R, MAGENTA_G, MAGENTA_B};
            case 36 -> new float[]{CYAN_R, CYAN_G, CYAN_B};
            case 37 -> new float[]{TEXT_R, TEXT_G, TEXT_B};       // white → text
            default -> new float[]{TEXT_R, TEXT_G, TEXT_B};
        };
    }

    /**
     * Apply the dark theme to the current imgui context with DPI scale factor of 1.0.
     */
    public static void apply() {
        apply(1.0f);
    }

    /**
     * Apply the dark theme to the current imgui context, scaling padding/sizes by the given factor.
     */
    public static void apply(float scale) {
        ImGuiStyle style = ImGui.getStyle();

        // Rounding (scaled)
        style.setWindowRounding(0f);
        style.setFrameRounding(4f * scale);
        style.setScrollbarRounding(4f * scale);
        style.setGrabRounding(2f * scale);

        // Padding (scaled)
        style.setWindowPadding(8f * scale, 8f * scale);
        style.setFramePadding(6f * scale, 4f * scale);
        style.setItemSpacing(8f * scale, 4f * scale);
        style.setItemInnerSpacing(4f * scale, 4f * scale);
        style.setScrollbarSize(12f * scale);

        // Colors
        style.setColor(ImGuiCol.WindowBg, BG_R, BG_G, BG_B, 1f);
        style.setColor(ImGuiCol.ChildBg, BG_R, BG_G, BG_B, 1f);
        style.setColor(ImGuiCol.PopupBg, INPUT_BG_R, INPUT_BG_G, INPUT_BG_B, 0.95f);
        style.setColor(ImGuiCol.Border, SELECTION_R, SELECTION_G, SELECTION_B, 0.5f);
        style.setColor(ImGuiCol.FrameBg, INPUT_BG_R, INPUT_BG_G, INPUT_BG_B, 1f);
        style.setColor(ImGuiCol.FrameBgHovered, SELECTION_R, SELECTION_G, SELECTION_B, 0.6f);
        style.setColor(ImGuiCol.FrameBgActive, SELECTION_R, SELECTION_G, SELECTION_B, 0.8f);
        style.setColor(ImGuiCol.TitleBg, INPUT_BG_R, INPUT_BG_G, INPUT_BG_B, 1f);
        style.setColor(ImGuiCol.TitleBgActive, INPUT_BG_R, INPUT_BG_G, INPUT_BG_B, 1f);
        style.setColor(ImGuiCol.Text, TEXT_R, TEXT_G, TEXT_B, 1f);
        style.setColor(ImGuiCol.TextDisabled, DIM_TEXT_R, DIM_TEXT_G, DIM_TEXT_B, 1f);
        style.setColor(ImGuiCol.Button, ACCENT_R, ACCENT_G, ACCENT_B, 0.6f);
        style.setColor(ImGuiCol.ButtonHovered, ACCENT_R, ACCENT_G, ACCENT_B, 0.8f);
        style.setColor(ImGuiCol.ButtonActive, ACCENT_R, ACCENT_G, ACCENT_B, 1f);
        style.setColor(ImGuiCol.Header, SELECTION_R, SELECTION_G, SELECTION_B, 0.5f);
        style.setColor(ImGuiCol.HeaderHovered, SELECTION_R, SELECTION_G, SELECTION_B, 0.7f);
        style.setColor(ImGuiCol.HeaderActive, SELECTION_R, SELECTION_G, SELECTION_B, 0.9f);
        style.setColor(ImGuiCol.Separator, SELECTION_R, SELECTION_G, SELECTION_B, 0.5f);
        style.setColor(ImGuiCol.ScrollbarBg, BG_R, BG_G, BG_B, 0.5f);
        style.setColor(ImGuiCol.ScrollbarGrab, SELECTION_R, SELECTION_G, SELECTION_B, 0.6f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, SELECTION_R, SELECTION_G, SELECTION_B, 0.8f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, ACCENT_R, ACCENT_G, ACCENT_B, 1f);
        style.setColor(ImGuiCol.CheckMark, ACCENT_R, ACCENT_G, ACCENT_B, 1f);
        style.setColor(ImGuiCol.SliderGrab, ACCENT_R, ACCENT_G, ACCENT_B, 0.8f);
        style.setColor(ImGuiCol.SliderGrabActive, ACCENT_R, ACCENT_G, ACCENT_B, 1f);
        style.setColor(ImGuiCol.PlotHistogram, ACCENT_R, ACCENT_G, ACCENT_B, 1f);
        style.setColor(ImGuiCol.PlotHistogramHovered, ACCENT_R + 0.1f, ACCENT_G + 0.1f, ACCENT_B + 0.1f, 1f);
        style.setColor(ImGuiCol.TextSelectedBg, SELECTION_R, SELECTION_G, SELECTION_B, 0.7f);
    }
}
