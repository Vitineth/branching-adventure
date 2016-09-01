package com.github.vitineth.branch.program.window.primary;

import com.github.vitineth.branch.program.utils.DrawUtils;

import java.awt.*;

/**
 * The notification value detailing its title and body as well as handling all drawing.
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 01/09/2016.
 *
 * @author Ryan (vitineth)
 * @since 01/09/2016
 */
public class Notification {

    private final int width = 200;
    private final int baseFont = 12;
    private String title;
    private String body;
    private NotificationType type;
    private int height;
    private int globalWidth;
    private int globalHeight;
    private int yOffset;

    public Notification(String title, String body, NotificationType type, int yOffset) {
        this.title = title;
        this.body = body;
        this.type = type;
        this.yOffset = yOffset;
    }

    public void draw(Graphics2D g, int w, int h) {
        globalWidth = w;
        globalHeight = h;

        String[] lines = DrawUtils.wrapLines(body, width - 40, g);
        g.setFont(g.getFont().deriveFont(baseFont + 1f));

        height = 10;
        g.setFont(g.getFont().deriveFont(Font.BOLD));
        height += g.getFontMetrics().getHeight();
        g.setFont(g.getFont().deriveFont(Font.PLAIN));
        height += g.getFontMetrics().getHeight() * lines.length;

        g.setColor(new Color(49, 51, 54));
        g.fillRoundRect(w - width - 10, h - height - 10 - yOffset, width, height, 10, 10);
        g.setColor(type.getOutlineColor());
        g.drawRoundRect(w - width - 10, h - height - 10 - yOffset, width, height, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD));
        g.drawString(title, w - width - 5, h - height - 10 + g.getFontMetrics().getHeight() - yOffset);

        int y = h - height - 10 + (2 * g.getFontMetrics().getHeight()) - yOffset;
        g.setFont(g.getFont().deriveFont(Font.PLAIN));
        for (String line : lines) {
            g.drawString(line.trim(), w - width - 5, y);
            y += g.getFontMetrics().getHeight();
        }
    }

    /**
     * Checks whether the given point lies within the notification factoring in the offsets.
     * @param x <code>int</code> The given x coordinate
     * @param y <code>int</code> The given y coordinate
     * @return <code>boolean</code> If the point lies within the notification.
     */
    public boolean contains(int x, int y) {
        return x > globalWidth - width - 10 && y > globalHeight - height - 10 - yOffset && x < globalWidth - 10 && y < globalHeight - yOffset;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public int getGlobalWidth() {
        return globalWidth;
    }

    public void setGlobalWidth(int globalWidth) {
        this.globalWidth = globalWidth;
    }

    public int getGlobalHeight() {
        return globalHeight;
    }

    public void setGlobalHeight(int globalHeight) {
        this.globalHeight = globalHeight;
    }

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public enum NotificationType {
        NOTICE(new Color(55, 112, 159)),
        ERROR(new Color(104, 0, 0));

        private Color outlineColor;

        NotificationType(Color outlineColor) {
            this.outlineColor = outlineColor;
        }

        public Color getOutlineColor() {
            return outlineColor;
        }
    }
}
