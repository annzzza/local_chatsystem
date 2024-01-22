package com.insa.gui.view;

import javax.swing.*;
import java.awt.*;

/**
    Class for creating a text field with a placeholder
    Extends JTextField
 */
public class PlaceholderTextField extends JTextField {

    private String placeholder;

    public PlaceholderTextField() {
    }

    public PlaceholderTextField(final String text) {
        super(text);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(final String text) {
        placeholder = text;
    }

    @Override
    protected void paintComponent(final java.awt.Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty()) {
            return;
        }

        final java.awt.Graphics2D g = (java.awt.Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, getInsets().left, pG.getFontMetrics()
                .getMaxAscent() + getInsets().top);
    }
}
