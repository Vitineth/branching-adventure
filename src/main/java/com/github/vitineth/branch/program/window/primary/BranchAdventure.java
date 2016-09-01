package com.github.vitineth.branch.program.window.primary;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The parent frame for the {@link BranchComponent}. It contains a header bar with a collection of buttons which link
 * to the key combinations shown in {@link BranchComponent#keyPressed(KeyEvent)}. They use the default images in
 * the buttons.png file.
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 31/08/2016.
 *
 * @author Ryan (vitineth)
 * @since 31/08/2016
 */
public class BranchAdventure extends JFrame {

    /**
     * The main component of the program.
     */
    private final BranchComponent c;

    public BranchAdventure() throws HeadlessException {
        super("Branching Adventure");

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize((int) (1920 * (2d / 3d)), (int) (1080 * (2d / 3d)));
        setLocation(30, 30);

        c = new BranchComponent(this::setTitle);

        JPanel header = new JPanel(new GridBagLayout());
        JButton minusLabel;
        JButton addLabel;
        JButton connectLabel;
        JButton disconnectLabel;
        JButton selectAllLabel;
        JButton exportLabel;
        JButton exportImageLabel;
        JButton helpLabel;
        JPanel spacer;
        try {
            BufferedImage fullButtons = ImageIO.read(getClass().getResourceAsStream("/buttons.png"));
            if (fullButtons == null) throw new IOException("");
            int imgSize = 30;

            BufferedImage minus = scale(fullButtons.getSubimage(0, 0, 70, 70), imgSize, imgSize);
            BufferedImage add = scale(fullButtons.getSubimage(70, 0, 70, 70), imgSize, imgSize);
            BufferedImage connect = scale(fullButtons.getSubimage(140, 0, 70, 70), imgSize, imgSize);
            BufferedImage disconnect = scale(fullButtons.getSubimage(210, 0, 70, 70), imgSize, imgSize);
            BufferedImage selectAll = scale(fullButtons.getSubimage(280, 0, 70, 70), imgSize, imgSize);
            BufferedImage export = scale(fullButtons.getSubimage(350, 0, 70, 70), imgSize, imgSize);
            BufferedImage exportImage = scale(fullButtons.getSubimage(420, 0, 70, 70), imgSize, imgSize);
            BufferedImage help = scale(fullButtons.getSubimage(490, 0, 70, 70), imgSize, imgSize);

            minusLabel = new JButton(new ImageIcon(minus));
            minusLabel.setToolTipText("Remove Node [X]");

            addLabel = new JButton(new ImageIcon(add));
            addLabel.setToolTipText("Add Node [A]");

            connectLabel = new JButton(new ImageIcon(connect));
            connectLabel.setToolTipText("Connect Nodes [C]");

            disconnectLabel = new JButton(new ImageIcon(disconnect));
            disconnectLabel.setToolTipText("Disconnect Nodes [D]");

            selectAllLabel = new JButton(new ImageIcon(selectAll));
            selectAllLabel.setToolTipText("Select All [Ctrl+A]");

            exportLabel = new JButton(new ImageIcon(export));
            exportLabel.setToolTipText("Export as JSON [Ctrl+S]");

            exportImageLabel = new JButton(new ImageIcon(exportImage));
            exportImageLabel.setToolTipText("Export as Image [Ctrl+E]");

            helpLabel = new JButton(new ImageIcon(help));
            helpLabel.setToolTipText("Help [H]");

            spacer = new JPanel();
        } catch (IOException e) {
            header.removeAll();
            minusLabel = new JButton("Remove Node");
            minusLabel.setToolTipText("Remove Node [X]");

            addLabel = new JButton("Add Node");
            addLabel.setToolTipText("Add Node [A]");

            connectLabel = new JButton("Connect Nodes");
            connectLabel.setToolTipText("Connect Nodes [C]");

            disconnectLabel = new JButton("Disconnect Nodes");
            disconnectLabel.setToolTipText("Disconnect Nodes [D]");

            selectAllLabel = new JButton("Select All");
            selectAllLabel.setToolTipText("Select All [Ctrl+A]");

            exportLabel = new JButton("Export as JSON");
            exportLabel.setToolTipText("Export as JSON [Ctrl+S]");

            exportImageLabel = new JButton("Export as Image");
            exportImageLabel.setToolTipText("Export as Image [Ctrl+E]");

            helpLabel = new JButton("Help");
            helpLabel.setToolTipText("Help [H]");

            spacer = new JPanel();
        }
        minusLabel.addActionListener(l -> c.remove());
        addLabel.addActionListener(l -> c.add());
        connectLabel.addActionListener(l -> c.connect());
        disconnectLabel.addActionListener(l -> c.disconnect());
        selectAllLabel.addActionListener(l -> c.select());
        exportLabel.addActionListener(l -> c.export());
        exportImageLabel.addActionListener(l -> c.imageExport());
        helpLabel.addActionListener(l -> c.help());

        GridBagConstraints minusLabelGBC = new GridBagConstraints(0, 0, 1, 1, 0d, 0d, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints addLabelGBC = new GridBagConstraints(1, 0, 1, 1, 0d, 0d, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints connectLabelGBC = new GridBagConstraints(2, 0, 1, 1, 0d, 0d, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints disconnectLabelGBC = new GridBagConstraints(3, 0, 1, 1, 0d, 0d, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints selectAllLabelGBC = new GridBagConstraints(4, 0, 1, 1, 0d, 0d, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints exportLabelGBC = new GridBagConstraints(5, 0, 1, 1, 0d, 0d, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints exportImageLabelGBC = new GridBagConstraints(6, 0, 1, 1, 0d, 0d, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints helpLabelGBC = new GridBagConstraints(7, 0, 1, 1, 0d, 0d, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints spacerGBC = new GridBagConstraints(8, 0, 1, 1, 1d, 1d, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        header.add(minusLabel, minusLabelGBC);
        header.add(addLabel, addLabelGBC);
        header.add(connectLabel, connectLabelGBC);
        header.add(disconnectLabel, disconnectLabelGBC);
        header.add(selectAllLabel, selectAllLabelGBC);
        header.add(exportLabel, exportLabelGBC);
        header.add(exportImageLabel, exportImageLabelGBC);
        header.add(helpLabel, helpLabelGBC);
        header.add(spacer, spacerGBC);

        add(header, BorderLayout.NORTH);
        add(c, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BranchAdventure().setVisible(true));
    }

    /**
     * Scales the given {@link BufferedImage} by creating a new image and drawing it back on top scaled.
     *
     * @param b {@link BufferedImage} The original image
     * @param w <code>int</code> The new width
     * @param h <code>int</code> The new height.
     * @return {@link BufferedImage} The new scaled image.
     */
    private BufferedImage scale(BufferedImage b, int w, int h) {
        BufferedImage n = new BufferedImage(w, h, b.getType());
        Graphics2D g = n.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(b, 0, 0, w, h, null);
        g.dispose();

        return n;
    }

    /**
     * And overwrite of the default set visible to request focus back to the main {@link BranchComponent}.
     *
     * @param b <code>boolean</code> Set visible status.
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        c.requestFocusInWindow();
    }

}

