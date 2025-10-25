package com.satya.portal;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private final Image background;
    private final float overlayAlpha;

    public BackgroundPanel(Image bg, float overlayAlpha) {
        this.background = bg;
        this.overlayAlpha = overlayAlpha;
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, getWidth(), getHeight(), this);
            if (overlayAlpha > 0f) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255,255,255, Math.round(255 * overlayAlpha)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        }
    }
}
