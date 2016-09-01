package com.github.vitineth.branch.program.node;

import com.github.vitineth.branch.program.utils.DrawUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An option node.
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 31/08/2016.
 *
 * @author Ryan (vitineth)
 * @since 31/08/2016
 */
public class Node {

    /**
     * The default font size.
     */
    private final float BASE_FONT_SIZE = 12;
    /**
     * The x location of the node
     */
    private int x;
    /**
     * The y location of the node
     */
    private int y;
    /**
     * The width of the node
     */
    private int width = 120;
    /**
     * The height of the node
     */
    private int height = 160;
    /**
     * The last supplied draw offset to be applied when a raw x and y value are supplied.
     */
    private int ox;
    /**
     * The last supplied draw offset to be applied when a raw x and y value are supplied.
     */
    private int oy;
    /**
     * The string id of the node
     */
    private String id;
    /**
     * The nodes prompt
     */
    private String prompt;
    /**
     * The nodes response
     */
    private String response;
    /**
     * All connected nodes.
     */
    private List<Node> connections = new ArrayList<>();

    public Node(int x, int y, String id, String prompt, String response) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.prompt = prompt;
        this.response = response;
    }

    public void draw(Graphics2D g, boolean selected, int ox, int oy) {
        //Alter coordinates to include offsets.
        int dx = x;
        int dy = y;
        this.ox = ox;
        this.oy = oy;
        x = x + ox;
        y = y + oy;

        //Selection modifier.
        g.setColor(selected ? new Color(230, 230, 255) : Color.WHITE);
        g.setFont(g.getFont().deriveFont(BASE_FONT_SIZE));
        g.fillRoundRect(x, y, width, height, 10, 10);

        //Text details.
        g.setColor(Color.BLACK);

        //Text
        g.drawString(id, x + 3, y + 15 - g.getFontMetrics().getDescent());
        g.setFont(g.getFont().deriveFont(g.getFont().getSize() - 4f));
        drawString(getPrompt(), x + 3, y + 15 + g.getFontMetrics().getHeight(), g);
        drawString(getResponse(), x + 3, ((y + 15 + y + height) / 2) + g.getFontMetrics().getHeight(), g);

        //Outline color.
        g.setColor(new Color(0, 150, 0));

        //Inner lines.
        g.drawLine(x, y + 15, x + width, y + 15);
        g.drawLine(x, (y + 15 + y + height) / 2, x + width, (y + 15 + y + height) / 2);

        //Outer border.
        g.drawRoundRect(x, y, width, height, 10, 10);

        //Node connector.
        g.setColor(g.getColor().darker());
        g.fillOval(x - 4, ((y + 15 + y + height) / 2) - 4, 8, 8);
        g.fillOval((x + width) - 4, ((y + 15 + y + height) / 2) - 4, 8, 8);
        g.setColor(g.getColor().brighter());

        //Connections.
        drawConnections(g);

        //Reset coordinates.
        x = dx;
        y = dy;
    }

    /**
     * Draws a cyan line between all connected components.
     * @param g {@link Graphics2D} The graphics object to draw on to.
     */
    private void drawConnections(Graphics2D g) {
        Color c = g.getColor();
        g.setColor(Color.CYAN);
        for (Node n : connections) {
            int ox = n.getX() + n.ox;
            int oy = n.getY() + n.oy;
            g.drawLine(x + width, (y + 15 + y + height) / 2,
                    ox, (oy + 15 + oy + height) / 2);
        }
        g.setColor(c);
    }

    /**
     * Draws a given string using line wrapping at the starting x and y on the given {@link Graphics2D} value.
     * @param text {@link String} The raw text.
     * @param startX <code>int</code> The x location;
     * @param startY <code>int</code> The y location;
     * @param g {@link Graphics2D} The graphics object to draw on to.
     */
    private void drawString(String text, int startX, int startY, Graphics2D g) {
        String[] parts = DrawUtils.wrapLines(text, width - ((startX - x) * 2), g);
        if (parts.length > 7) parts = Arrays.copyOf(parts, 7);
        int y = startY;
        for (String s : parts) {
            g.drawString(s, startX, y);
            y += g.getFontMetrics().getHeight();
        }
    }

    /**
     * If the given x and y coordinate is within the node. This factors in the last supplied drawing offset.
     * @param x <code>int</code> The raw x location.
     * @param y <code>int</code> The raw y location.
     * @return <code>boolean</code> if the point is contained.
     */
    public boolean contains(int x, int y) {
        return x > getX() + ox && y > getY() + oy && x < getX() + ox + width && y < getY() + oy + height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Node> getConnections() {
        return connections;
    }

    public void setConnections(List<Node> connections) {
        this.connections = connections;
    }
}
