package com.github.vitineth.branch.program.window.primary;

import com.github.vitineth.branch.program.utils.DrawUtils;
import com.github.vitineth.branch.program.utils.FileUtils;
import com.github.vitineth.branch.program.node.Node;
import com.github.vitineth.branch.program.passbacks.NodePassback;
import com.github.vitineth.branch.program.passbacks.TitlePassback;
import com.github.vitineth.branch.program.window.DetailWindow;
import com.google.gson.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The actual mapping component of the program. It handles all nodes and their respective drawing. The add method is
 * disabled (but only one to prevent cluttering).
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 31/08/2016.
 *
 * @author Ryan (vitineth)
 * @since 31/08/2016
 */
public class BranchComponent extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

    /**
     * The x offset from the top left corner of a node when the mouse button is pressed and reset to 0 when released or
     * the x location if it matches no nodes (the offset from the top left corner of the component).
     */
    private int dragOffsetX;
    /**
     * The y offset from the top left corner of a node when the mouse button is pressed and reset to 0 when released or
     * the y location if it matches no nodes (the offset from the top left corner of the component).
     */
    private int dragOffsetY;

    /**
     * The x offset of the canvas determined during the drag event if no nodes are valid.
     */
    private int offsetX = 0;
    /**
     * The y offset of the canvas determined during the drag event if no nodes are valid.
     */
    private int offsetY = 0;

    /**
     * The index of the node that is being moved at that moment or -1 if nothing is in the process of being moved (also
     * used for canvas movement).
     */
    private int moving = -1;
    /**
     * The last location of the mouse determined through either the {@link #mouseMoved(MouseEvent)} event or the
     * {@link #mouseDragged(MouseEvent)} event if the canvas is being used.
     */
    private Point mouseLocation;
    /**
     * If the layout was loaded from a file.
     */
    private boolean loadedFromFile = false;
    /**
     * The file the layout was loaded from it it has been.
     */
    private File loadedFile;
    /**
     * If the layout has been modified since it was loaded or last saved.
     */
    private boolean modified;
    /**
     * A passback interface to allow for the adjustment of the title.
     */
    private TitlePassback titlePassback;

    /**
     * The list of nodes currently active on the canvas.
     */
    private java.util.List<Node> nodes = new ArrayList<>();

    /**
     * The last applied modifiers to any key pressed. Reset to 0 when the key is released.
     */
    private int modifiers = 0;
    /**
     * The list of selected nodes (references the id of the {@link #nodes} list).
     */
    private java.util.List<Integer> selections = new ArrayList<>();
    /**
     * The list of active notifications
     */
    private java.util.List<Notification> notifications = new ArrayList<>();

    public BranchComponent(TitlePassback passback) {
        super(true);
        this.titlePassback = passback;
        enableInputMethods(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);

        Node n1 = new Node(30, 30, generateID(), "Welcome to the branching story program.", "Hover over each of the buttons above to see what they do.");
        Node n2 = new Node(180, 30, generateID(), "This is the PROMPT. This is what is shown as one of the choices when the story is played.", "This is the RESPONSE. It's shown when the option is clicked above the PROMPT");
        nodes.add(n1);
        nodes.add(n2);

        updateTitle();

        n1.getConnections().add(n2);
    }

    @Override
    public Component add(Component comp) {
        throw new InvalidParameterException("Cannot add components to this panel.");
    }

    @Override
    protected void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g);

        g.fillRect(0, 0, getWidth(), getHeight());
        drawGrid(g, getWidth(), getHeight());

        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).draw(g, selections.contains(i), offsetX, offsetY);
        }

        for (Notification n : notifications){
            n.draw(g, getWidth(), getHeight());
        }
    }

    /**
     * Draws a default grid onto the supplied graphics object with the given width and height. It uses the default
     * color of {@link Color#GRAY} for the backround and variations on the {@link Color#DARK_GRAY} for the lines.
     *
     * @param g {@link Graphics2D} The graphics object to paint on top of.
     * @param w <code>int</code> The width of the grid to draw.
     * @param h <code>int</code> The height of the grid to draw.
     */
    private void drawGrid(Graphics2D g, int w, int h) {
        int gridOffset = 25;
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);

        Color c = Color.DARK_GRAY;

        boolean xs = true;
        boolean ys = true;
        for (int x = 0; x < w; x += gridOffset / 2) {
            for (int y = 0; y < h; y += gridOffset / 2) {
                if (xs) {
                    g.setStroke(new BasicStroke(0.8f));
                    g.setColor(c.darker());
                    g.drawLine(x, 0, x, h);
                } else {
                    g.setStroke(new BasicStroke(1f));
                    g.setColor(c.brighter());
                    g.drawLine(x, 0, x, h);
                }
                if (ys) {
                    g.setStroke(new BasicStroke(0.8f));
                    g.setColor(c.darker());
                    g.drawLine(0, y, w, y);
                } else {
                    g.setStroke(new BasicStroke(1f));
                    g.setColor(c.brighter());
                    g.drawLine(0, y, w, y);
                }
                ys = !ys;
            }
            xs = !xs;
        }
    }

    /**
     * Exports the entire canvas to a file including all nodes and connection with the background grid. It will show a
     * {@link JFileChooser} to determine where to store the file launched through {@link FileUtils#showSystemOpenDialog(Component)}
     */
    private void exportToImageFile() {
        int lowestX = nodes.get(0).getX();
        int lowestY = nodes.get(0).getY();
        int greatestX = lowestX;
        int greatestY = lowestY;
        for (Node n : nodes) {
            if (n.getX() < lowestX) lowestX = n.getX();
            if (n.getY() < lowestY) lowestY = n.getY();

            if (n.getX() + n.getWidth() > greatestX) greatestX = n.getX() + n.getWidth();
            if (n.getY() + n.getHeight() > greatestY) greatestY = n.getY() + n.getHeight();
        }

        BufferedImage img = new BufferedImage(greatestX - lowestX, greatestY - lowestY, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = img.createGraphics();
        drawGrid(g, img.getWidth(), img.getHeight());
        for (Node n : nodes) {
            Node dn = new Node(n.getX() - lowestX, n.getY() - lowestY, n.getId(), n.getPrompt(), n.getResponse());
            dn.draw(g, false, 0, 0);
        }

        try {
            File selected = FileUtils.showSystemSaveDialog(this);
            ImageIO.write(img, "PNG", selected);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        if (moving != -1 && moving < nodes.size()) {
            moveNode(e.getX(), e.getY());
        } else {
            moveCanvas(e.getX(), e.getY());
        }
    }

    /**
     * Move the canvas using the given x and y coordinates. The x and y values are not how much to move it in each
     * direction, they should be values such as those returned by {@link #mouseDragged(MouseEvent)} (a valid value on
     * the component). They will be offset with the current mouse location to return how much to move by and then
     * updated, repainted and the mouse location updated.
     *
     * @param x <code>int</code> The x location on the component.
     * @param y <code>int</code> The y location on the component.
     */
    private void moveCanvas(int x, int y) {
        offsetX += x - mouseLocation.getX();
        offsetY += y - mouseLocation.getY();
        repaint();
        mouseLocation = new Point(x, y);
    }

    /**
     * Moves the active node. It uses the x and y coordinates and offsets them by the drag offset values
     * ({@link #dragOffsetX}, {@link #dragOffsetY}) to see how much to move by and will then update the node before
     * repainting. If the current {@link #moving} value is not valid it will just return without executing any further.
     *
     * @param x <code>int</code> The x location on the component.
     * @param y <code>int</code> The y location on the component.
     */
    private void moveNode(int x, int y) {
        if (moving == -1 || moving > nodes.size() - 1) return;
        Node n = nodes.get(moving);
        n.setX(x - dragOffsetX);
        n.setY(y - dragOffsetY);
        modified = true;
        updateTitle();
        repaint();
    }

    /**
     * Updates the {@link #mouseLocation} value with the current point of the mouse.
     *
     * @param e {@link Point} the current point on the component.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation = e.getPoint();
    }

    /**
     * Defers to either {@link #performSelection(int, int)} or {@link #performDetailWindow(int, int)} depending on
     * whether the click count is 1 or 2 respectively.
     *
     * @param e {@link MouseEvent} The raw event.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            performSelection(e.getX(), e.getY());
        } else if (e.getClickCount() == 2) {
            performDetailWindow(e.getX(), e.getY());
        }

        for (Notification notification : notifications) {
            if (notification.contains(e.getX(), e.getY())){
                notifications.remove(notification);
                calculateNotifications();
                repaint();
                return;
            }
        }
    }

    /**
     * This will attempt to open a detail window on the given node. If no node is found the method will just return.
     * It will open a {@link DetailWindow} with the given node and set up a {@link NodePassback} with the old and the
     * new node to allow the changing without the passing of the {@link #nodes} list.
     *
     * @param x <code>int</code> The x location on the component.
     * @param y <code>int</code> The y location on the component.
     */
    private void performDetailWindow(int x, int y) {
        for (Node n : nodes) {
            if (n.contains(x, y)) {
                DetailWindow window = new DetailWindow(n, (oldNode, newNode) -> {
                    nodes.set(nodes.indexOf(oldNode), newNode);
                    repaint();

                    modified = true;
                    updateTitle();
                });
                window.setVisible(true);
                break;
            }
        }
    }

    /**
     * Attempts to perform a selection on any node on the canvas. It will determine if the cursor is within any of the
     * nodes and will either reset selections and then select the node or add it to the list of selections if the
     * shift modifier is pressed as determined by {@link #modifiers}. If no nodes are found it will just clear the
     * selections before repainting.
     *
     * @param x <int>x</int> The x location on the canvas.
     * @param y <int>y</int> The y location on the canvas.
     */
    private void performSelection(int x, int y) {
        boolean modified = false;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).contains(x, y)) {
                if (modifiers == KeyEvent.SHIFT_MASK || modifiers == KeyEvent.SHIFT_MASK + KeyEvent.BUTTON1_MASK) {
                    selections.add(i);
                    modified = true;
                } else {
                    selections.clear();
                    selections.add(i);
                    modified = true;
                }
            }
        }
        if (!modified) selections.clear();
        repaint();
    }

    /**
     * Sets up the offset values. It will use the first found node at the x and y locations. If no nodes are found it
     * will set it to the x and y locations of the mouse (as the effective offset between them and the top left corner).
     *
     * @param e {@link MouseEvent} The raw event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).contains(e.getX(), e.getY())) {
                moving = i;
                dragOffsetX = e.getX() - nodes.get(i).getX();
                dragOffsetY = e.getY() - nodes.get(i).getY();
                return;
            }
        }

        dragOffsetX = e.getX();
        dragOffsetY = e.getY();
    }

    /**
     * Resets the {@link #moving} value.
     *
     * @param e {@link MouseEvent} the raw event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        moving = -1;
        setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handles the disconnect hotkey when two nodes are selected through {@link #selections}. It will remove the origin
     * node from the destinations connections if it is listed and visa versa. It will then repaint once it is done.
     *
     * @param origin      {@link Node} The first node selected
     * @param destination {@link Node} The second node selected.
     */
    private void processDisconnect(Node origin, Node destination) {
        if (origin.getConnections().contains(destination)) {
            removeConnection(origin, destination);
            modified = true;
            updateTitle();
        } else if (destination.getConnections().contains(origin)) {
            removeConnection(destination, origin);
            modified = true;
            updateTitle();
        }
        repaint();
    }

    /**
     * Removes the destination node from the origin node.
     *
     * @param o {@link Node} the origin node
     * @param d {@link Node} the destination node to remove.
     */
    private void removeConnection(Node o, Node d) {
        java.util.List<Node> n = o.getConnections();
        n.remove(d);
        o.setConnections(n);
    }

    /**
     * Processes the connection of two nodes. It will add the destination node to the origin node if it is not listed
     * already. It will set the connection and then repaint once finished.
     *
     * @param origin      {@link Node} The first node selected.
     * @param destination {@link Node} The second node selected.
     */
    private void processConnect(Node origin, Node destination) {
        if (destination.getConnections().contains(origin)) return;

        java.util.List<Node> o = origin.getConnections();
        o.add(destination);
        origin.setConnections(o);

        modified = true;
        updateTitle();

        repaint();
    }

    /**
     * Processes the selection of nodes or adding of nodes. If the key is pressed with the {@link InputEvent#CTRL_MASK}
     * then it will add every node to the selection. If it is not then it will add a node to the canvas at the given
     * location adding the offset x and y. It will repaint once finished.
     */
    private void processAddSelection() {
        if (modifiers == InputEvent.CTRL_MASK) {
            selections.clear();
            for (int i = 0; i < nodes.size(); i++) {
                selections.add(i);
            }
        } else {
            nodes.add(new Node((int) mouseLocation.getX() - offsetX, (int) mouseLocation.getY() - offsetY, generateID(), "Prompt", "Response"));
            selections.clear();
            selections.add(nodes.size() - 1);
            modified = true;
            updateTitle();
        }
        repaint();
    }

    /**
     * Processed the deletion of nodes. If there are no selections then it will just return. If the length of the
     * selections equals that of the nodes then it will just empty the nodes list. Otherwise it will iterate through
     * the selections and both delete the node and remove it from every connection list of other nodes. It will then
     * clear the selections array before repainting.
     */
    private void processDelete() {
        if (selections.size() == 0) return;

        if (selections.size() == nodes.size()) {
            nodes.clear();
            modified = true;
            updateTitle();
        } else {
            for (int selection : selections) {
                nodes.stream().filter(n -> n.getConnections().contains(nodes.get(selection))).forEach(n -> {
                    java.util.List<Node> ns = n.getConnections();
                    ns.remove(nodes.get(selection));
                    n.setConnections(ns);
                });
                nodes.remove(selection);
                modified = true;
                updateTitle();
            }
        }
        selections.clear();
        repaint();
    }

    /**
     * Displays the help in the form of a {@link JOptionPane} through
     * {@link JOptionPane#showMessageDialog(Component, Object, String, int)}.
     */
    private void showHelp() {
        JOptionPane.showMessageDialog(this, "" +
                "Help:\n" +
                "Key Commands:\n" +
                "[C]: Create connection between two selected nodes.\n" +
                "[D]: Delete connection between two selected nodes.\n" +
                "[A]: Add new node.\n" +
                "[Ctrl+A]: Select all nodes.\n" +
                "[X]: Delete selected nodes.\n" +
                "[Ctrl+E]: Export to image.\n" +
                "[Ctrl+S]: Export to JSON.\n" +
                "[H]: Open help\n" +
                "General Help:\n" +
                "Click and drag to move components\n" +
                "Double click to edit a nodes properties" +
                "</html>", "Help", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Handles the loading of a JSON file that is exported through {@link #exportToFile()}. It will load the file or
     * error out and defer to {@link #parseJSON(String)}.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void processOpen() {
        File file = FileUtils.showSystemOpenDialog(this);
        if (file != null) {
            if (file.getAbsolutePath().endsWith(".json")) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] d = new byte[fis.available()];
                    fis.read(d);

                    parseJSON(new String(d));

                    loadedFromFile = true;
                    loadedFile = file;
                    modified = false;
                    updateTitle();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    logError("Invalid JSON", "Could not load the JSON file. IOException: " + e1.getMessage());
//                    JOptionPane.showMessageDialog(this, "Could not load the JSON file.", "Invalid JSON", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                logError("Invalid JSON", "This is not a valid JSON file. It cannot be opened.");
//                JOptionPane.showMessageDialog(this, "This is not a valid JSON file. It cannot be opened.", "Invalid JSON", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Defers to one of the following methods depending on keys:
     * <table>
     * <thead>
     * <tr>
     * <td>Key Combination</td>
     * <td>Method</td>
     * <td>Conditions</td>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td><code>C</code></td>
     * <td>{@link #processConnect(Node, Node)}</td>
     * <td><code>selections.size() == 2</code></td>
     * </tr>
     * <tr>
     * <td><code>D</code></td>
     * <td>{@link #processDisconnect(Node, Node)}</td>
     * <td><code>selections.size() == 2</code></td>
     * </tr>
     * <tr>
     * <td><code>A</code></td>
     * <td>{@link #processAddSelection()}</td>
     * <td>N/A</td>
     * </tr>
     * <tr>
     * <td><code>X</code></td>
     * <td>{@link #processDelete()}</td>
     * <td>N/A</td>
     * </tr>
     * <tr>
     * <td><code>H</code></td>
     * <td>{@link #showHelp()}</td>
     * <td>N/A</td>
     * </tr>
     * <tr>
     * <td><code>Ctrl+E</code></td>
     * <td>{@link #exportToImageFile()}</td>
     * <td>N/A</td>
     * </tr>
     * <tr>
     * <td><code>Ctrl+S</code></td>
     * <td>{@link #exportToFile()}</td>
     * <td>N/A</td>
     * </tr>
     * <tr>
     * <td><code>O</code></td>
     * <td>{@link #processOpen()}</td>
     * <td>N/A</td>
     * </tr>
     * </tbody>
     * </table>
     * It will also update the {@link #modifiers} value.
     *
     * @param e {@link KeyEvent} The raw event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        modifiers = e.getModifiers();

        if (selections.size() == 2) {
            Node origin = nodes.get(selections.get(0));
            Node destination = nodes.get(selections.get(1));
            if (e.getKeyCode() == KeyEvent.VK_C) processConnect(origin, destination);
            if (e.getKeyCode() == KeyEvent.VK_D) processDisconnect(origin, destination);
        }

        if (e.getKeyCode() == KeyEvent.VK_A) processAddSelection();
        if (e.getKeyCode() == KeyEvent.VK_X) processDelete();
        if (e.getKeyCode() == KeyEvent.VK_H) showHelp();

        if (e.getKeyCode() == KeyEvent.VK_E && e.getModifiers() == InputEvent.CTRL_MASK) exportToImageFile();
        if (e.getKeyCode() == KeyEvent.VK_S && e.getModifiers() == InputEvent.CTRL_MASK) exportToFile();

        if (e.getKeyCode() == KeyEvent.VK_O && e.getModifiers() == InputEvent.CTRL_MASK) processOpen();

        if (e.getKeyCode() == KeyEvent.VK_T) logError("Test", "Test error with key");
    }

    /**
     * Parses a JSON string as a node layout. It uses the same format as the {@link #exportToFile()} in the following
     * format:
     * <br>
     * <pre>
     * {
     *   "node.id": {
     *     "id": "node.id",
     *     "prompt": "node.prompt",
     *     "response": "node.response",
     *     "draw": {
     *       "x": node.x,
     *       "y": node.y,
     *       "w": node.width,
     *       "h": node.height
     *     },
     *     "connections": [
     *       "node.connections[0]",
     *       "node.connections[1]"
     *     ]
     *   }
     * }
     * </pre>
     * On error it will show a {@link JOptionPane} message dialog.
     *
     * @param json {@link String} The JSON string.
     */
    private void parseJSON(String json) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject base = (JsonObject) parser.parse(json);

            HashMap<String, java.util.List<String>> connections = new HashMap<>();
            HashMap<String, Node> nodeLoads = new HashMap<>();

            for (Map.Entry<String, JsonElement> entry : base.entrySet()) {
                JsonObject node = (JsonObject) entry.getValue();
                String id = node.get("id").getAsString();
                String prompt = node.get("prompt").getAsString();
                String response = node.get("response").getAsString();

                JsonObject draw = (JsonObject) node.get("draw");
                int x = draw.get("x").getAsInt();
                int y = draw.get("y").getAsInt();
                int w = draw.get("w").getAsInt();
                int h = draw.get("h").getAsInt();

                JsonArray conn = (JsonArray) node.get("connections");
                java.util.List<String> cs = new ArrayList<>();
                for (JsonElement el : conn) {
                    cs.add(el.getAsString());
                }
                connections.put(id, cs);

                Node n = new Node(x, y, id, prompt, response);
                n.setWidth(w);
                n.setHeight(h);

                nodeLoads.put(id, n);
            }

            for (String s : connections.keySet()) {
                java.util.List<String> c = connections.get(s);
                Node active = nodeLoads.get(s);

                java.util.List<Node> conns = new ArrayList<>();
                for (String connection : c) {
                    if (!nodeLoads.containsKey(connection)) {
                        System.err.println("No matching noe.");
                        continue;
                    }
                    conns.add(nodeLoads.get(connection));
                }

                active.setConnections(conns);
            }

            nodes = nodeLoads.keySet().stream().map(nodeLoads::get).collect(Collectors.toList());
            repaint();
        } catch (JsonSyntaxException e) {
            logError("Invalid JSON", "This is not a valid JSON file. It cannot be opened.");
//            JOptionPane.showMessageDialog(this, "Failed to load the JSON data: " + e.getMessage(), "JSON Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets the {@link #modifiers} value to <code>0</code>
     *
     * @param e {@link KeyEvent} The raw event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        modifiers = 0;
    }

    /**
     * Exports the node layout and configuration to file. This is a file that can both be loaded into an arbitrary
     * program and parsed or loaded back into the program using {@link #parseJSON(String)}. It contains drawing details
     * such as x, y, width and height so the layout will be identical. JSON format:
     * <pre>
     * {
     *   "node.id": {
     *     "id": "node.id",
     *     "prompt": "node.prompt",
     *     "response": "node.response",
     *     "draw": {
     *       "x": node.x,
     *       "y": node.y,
     *       "w": node.width,
     *       "h": node.height
     *     },
     *     "connections": [
     *       "node.connections[0]",
     *       "node.connections[1]"
     *     ]
     *   }
     * }
     * </pre>
     */
    private void exportToFile() {
        JsonObject base = new JsonObject();
        for (Node n : nodes) {
            JsonObject node = new JsonObject();
            node.addProperty("id", n.getId());
            node.addProperty("prompt", n.getPrompt());
            node.addProperty("response", n.getResponse());

            JsonObject draw = new JsonObject();
            draw.addProperty("x", n.getX());
            draw.addProperty("y", n.getY());
            draw.addProperty("w", n.getWidth());
            draw.addProperty("h", n.getHeight());

            JsonArray connections = new JsonArray();
            for (Node conn : n.getConnections()) {
                connections.add(conn.getId());
            }

            node.add("draw", draw);
            node.add("connections", connections);

            base.add(n.getId(), node);
        }

        try {
            File output = loadedFromFile ? loadedFile : FileUtils.showSystemSaveDialog(this);
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(new Gson().toJson(base).getBytes());

            loadedFromFile = true;
            loadedFile = output;
            modified = false;
            updateTitle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a 8 character code using an alphabetical character as the first value (so they can be directly parsed
     * into variables for example). It uses alphanumerics giving it the following possibilities:
     * <code>32<sup>8</sup> combination = 1,099,511,627,776</code>
     *
     * @return {@link String} The unique ID.
     */
    private String generateID() {
        int length = 8;

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String s = "abcdefghijklmnopqrstuvwxyz1234567890";

        sb.append(s.charAt(random.nextInt(26)));
        for (int i = 0; i < length - 1; i++) {
            sb.append(s.charAt(random.nextInt(s.length())));
        }

        return sb.toString();
    }

    /**
     * Wrapper for the delete key event of <code>X</code>. It then re-requests focus for the component as it will have
     * lost it when the buttons are pressed disabling the key listeners.
     */
    public void remove() {
        keyPressed(new KeyEvent(this, -1, System.currentTimeMillis(), 0, KeyEvent.VK_X, 'x'));
        requestFocusInWindow();
    }

    /**
     * Wrapper for the delete key event of <code>A</code>. It then re-requests focus for the component as it will have
     * lost it when the buttons are pressed disabling the key listeners.
     */
    public void add() {
        keyPressed(new KeyEvent(this, -1, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a'));
        requestFocusInWindow();
    }

    /**
     * Wrapper for the delete key event of <code>C</code>. It then re-requests focus for the component as it will have
     * lost it when the buttons are pressed disabling the key listeners.
     */
    public void connect() {
        keyPressed(new KeyEvent(this, -1, System.currentTimeMillis(), 0, KeyEvent.VK_C, 'c'));
        requestFocusInWindow();
    }

    /**
     * Wrapper for the delete key event of <code>D</code>. It then re-requests focus for the component as it will have
     * lost it when the buttons are pressed disabling the key listeners.
     */
    public void disconnect() {
        keyPressed(new KeyEvent(this, -1, System.currentTimeMillis(), 0, KeyEvent.VK_D, 'd'));
        requestFocusInWindow();
    }

    /**
     * Wrapper for the delete key event of <code>A</code>. It then re-requests focus for the component as it will have
     * lost it when the buttons are pressed disabling the key listeners.
     */
    public void select() {
        keyPressed(new KeyEvent(this, -1, System.currentTimeMillis(), InputEvent.CTRL_MASK, KeyEvent.VK_A, 'a'));
        requestFocusInWindow();
    }

    /**
     * Wrapper for the delete key event of <code>E</code>. It then re-requests focus for the component as it will have
     * lost it when the buttons are pressed disabling the key listeners.
     */
    public void export() {
        keyPressed(new KeyEvent(this, -1, System.currentTimeMillis(), 0, KeyEvent.VK_E, 'e'));
        requestFocusInWindow();
    }

    /**
     * Wrapper for the delete key event of <code>Ctrl+E</code>. It then re-requests focus for the component as it will have
     * lost it when the buttons are pressed disabling the key listeners.
     */
    public void imageExport() {
        keyPressed(new KeyEvent(this, -1, System.currentTimeMillis(), InputEvent.CTRL_MASK, KeyEvent.VK_E, 'e'));
        requestFocusInWindow();
    }

    /**
     * Wrapper for the delete key event of <code>H</code>. It then re-requests focus for the component as it will have
     * lost it when the buttons are pressed disabling the key listeners.
     */
    public void help() {
        keyPressed(new KeyEvent(this, -1, System.currentTimeMillis(), 0, KeyEvent.VK_H, 'h'));
        requestFocusInWindow();
    }

    /**
     * Updates the title based on whether it is loaded from a file, is modifier or is just the base launch.
     */
    private void updateTitle() {
        if (loadedFromFile) {
            if (modified) {
                titlePassback.updateTitle("Branching Adventure Designer - " + loadedFile.getName() + " *");
            } else {
                titlePassback.updateTitle("Branching Adventure Designer - " + loadedFile.getName());
            }
        } else {
            titlePassback.updateTitle("Branching Adventure Designer - *");
        }
    }

    /**
     * Calculates the positions of the notifications relative to each other and their offsets.
     */
    private void calculateNotifications(){
        java.util.List<Notification> reverse = notifications;
        Collections.reverse(reverse);

        int yo = 0;
        for (Notification notification : reverse){
            notification.setyOffset(yo);
            yo += notification.getHeight();
        }

        repaint();
    }

    /**
     * Inserts an error notifications and calls for a recalculation which will trigger a repaint.
     * @param title {@link String} the title of the message.
     * @param body {@link String} the body of the message.
     */
    private void logError(String title, String body){
        notifications.add(new Notification(title, body, Notification.NotificationType.ERROR, 0));
        calculateNotifications();
    }

}