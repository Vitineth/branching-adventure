package com.github.vitineth.branch.program.utils;

import java.awt.*;
import java.util.*;

/**
 * Utilities to help the drawing of elements.
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 01/09/2016.
 *
 * @author Ryan (vitineth)
 * @since 01/09/2016
 */
public class DrawUtils {

    /**
     * Wraps the given string using the given font supplied with the {@link Graphics2D} and the given max width.
     * It tries to cut at spaces but if one cannot be achieved it will crop it at the raw character.
     * @param text {@link String} The raw text
     * @param maxWidth <code>int</code> The maximum length the text can reach.
     * @param g {@link Graphics2D} The graphics object.
     * @return {@link String}[] The array of wrapped lines.
     */
    public static String[] wrapLines(String text, int maxWidth, Graphics2D g) {
        java.util.List<String> lines = new ArrayList<>();
        int lastCut = 0;
        int active = 0;
        int lastSpace = -1;

        while ((active += 1) < text.length()) {
            if (text.charAt(active) == ' ') lastSpace = active;
            if (g.getFontMetrics().charsWidth(text.substring(lastCut, active).toCharArray(), 0, active - lastCut) >= maxWidth) {
                if (lastSpace != -1) {
                    lines.add(text.substring(lastCut, lastSpace).trim());

                    active = lastSpace + 1;
                    lastCut = lastSpace;
                    lastSpace = -1;
                } else {
                    lines.add(text.substring(lastCut, active).trim());

                    lastCut = active;
                }
            }
        }
        if (lastCut != active) lines.add(text.substring(lastCut, text.length()));
        return lines.toArray(new String[lines.size()]);
    }

}
