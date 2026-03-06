package com.botwithus.bot.cli.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Separate JFrame that displays one or more live game video feeds in a grid.
 * Each cell shows a connection name label and the latest JPEG frame.
 */
public class StreamWindow extends JFrame {

    private final Map<String, StreamCell> cells = new LinkedHashMap<>();
    private final JPanel gridPanel;
    private Runnable onCloseCallback;

    public StreamWindow() {
        super("JBot — Live Stream");
        setSize(960, 540);
        setMinimumSize(new Dimension(480, 270));
        setLocationRelativeTo(null);
        getContentPane().setBackground(GuiTheme.BG);

        gridPanel = new JPanel();
        gridPanel.setBackground(GuiTheme.BG);
        setContentPane(gridPanel);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            }
        });
    }

    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }

    /**
     * Add or get existing cell for a connection. Must be called on EDT.
     */
    public StreamCell addCell(String connectionName) {
        StreamCell existing = cells.get(connectionName);
        if (existing != null) return existing;

        StreamCell cell = new StreamCell(connectionName);
        cells.put(connectionName, cell);
        rebuildGrid();
        return cell;
    }

    /**
     * Remove a cell. Must be called on EDT.
     */
    public void removeCell(String connectionName) {
        StreamCell cell = cells.remove(connectionName);
        if (cell != null) {
            gridPanel.remove(cell);
            rebuildGrid();
        }
    }

    public boolean hasCells() {
        return !cells.isEmpty();
    }

    private void rebuildGrid() {
        gridPanel.removeAll();
        int count = cells.size();
        if (count == 0) {
            gridPanel.revalidate();
            gridPanel.repaint();
            return;
        }
        int cols = (int) Math.ceil(Math.sqrt(count));
        int rows = (int) Math.ceil((double) count / cols);
        gridPanel.setLayout(new GridLayout(rows, cols, 2, 2));
        for (StreamCell cell : cells.values()) {
            gridPanel.add(cell);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * A single cell in the stream grid: label + image panel.
     */
    public static class StreamCell extends JPanel {
        private final ImagePanel imagePanel;

        StreamCell(String connectionName) {
            setLayout(new BorderLayout());
            setBackground(GuiTheme.BG);

            JLabel label = new JLabel(connectionName, SwingConstants.CENTER);
            label.setForeground(GuiTheme.ACCENT);
            label.setFont(GuiTheme.monoFont(12));
            label.setOpaque(true);
            label.setBackground(GuiTheme.INPUT_BG);
            add(label, BorderLayout.NORTH);

            imagePanel = new ImagePanel();
            add(imagePanel, BorderLayout.CENTER);
        }

        /**
         * Update the displayed frame from raw JPEG bytes.
         * Safe to call from any thread.
         */
        public void updateFrame(byte[] jpegData) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(jpegData));
                if (img != null) {
                    imagePanel.setImage(img);
                }
            } catch (IOException ignored) {}
        }
    }

    /**
     * Panel that paints the latest BufferedImage, scaled to fit.
     * Uses volatile reference swap for thread safety.
     */
    private static class ImagePanel extends JPanel {
        private volatile BufferedImage currentImage;

        ImagePanel() {
            setBackground(Color.BLACK);
        }

        void setImage(BufferedImage img) {
            currentImage = img;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            BufferedImage img = currentImage;
            if (img == null) return;

            int pw = getWidth();
            int ph = getHeight();
            int iw = img.getWidth();
            int ih = img.getHeight();

            double scale = Math.min((double) pw / iw, (double) ph / ih);
            int dw = (int) (iw * scale);
            int dh = (int) (ih * scale);
            int x = (pw - dw) / 2;
            int y = (ph - dh) / 2;

            g.drawImage(img, x, y, dw, dh, null);
        }
    }
}
