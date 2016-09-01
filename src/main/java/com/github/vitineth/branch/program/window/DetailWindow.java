package com.github.vitineth.branch.program.window;

import com.github.vitineth.branch.program.node.Node;
import com.github.vitineth.branch.program.passbacks.NodePassback;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A class allowing the modification of a node.
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 31/08/2016.
 *
 * @author Ryan (vitineth)
 * @since 31/08/2016
 */
public class DetailWindow extends JFrame {

    private Node node;
    private JTextField idField;
    private JTextArea promptField;
    private JTextArea responseField;
    private NodePassback passback;

    public DetailWindow(Node node, NodePassback passback) throws HeadlessException {
        super("Edit Node " + node.getId());
        this.node = node;
        this.passback = passback;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(680, 300);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveChanges();
                super.windowClosing(e);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                dispose();
                saveChanges();
                super.windowDeactivated(e);
            }
        });

        configureWindow();
    }

    /**
     * Saves the changes made to the supplied node by using the {@link #passback}.
     */
    private void saveChanges() {
        Node n = new Node(node.getX(), node.getY(), idField.getText(), promptField.getText(), responseField.getText());
        n.setConnections(node.getConnections());
        n.setHeight(node.getHeight());
        n.setWidth(node.getWidth());
        passback.detailsAltered(node, n);
        node = n;
    }

    /**
     * Sets up the window components and layouts.
     */
    private void configureWindow() {
        setLayout(new GridBagLayout());

        JLabel idLabel = new JLabel("ID: ");
        JLabel promptLabel = new JLabel("Prompt: ");
        JLabel responseLabel = new JLabel("Response: ");

        GridBagConstraints idLabelGBC = new GridBagConstraints(0, 0, 1, 1, 0d, 0d, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints promptLabelGBC = new GridBagConstraints(0, 1, 1, 1, 0d, 0d, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints responseLabelGBC = new GridBagConstraints(0, 2, 1, 1, 0d, 0d, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        add(idLabel, idLabelGBC);
        add(promptLabel, promptLabelGBC);
        add(responseLabel, responseLabelGBC);

        idField = new JTextField(node.getId());
        promptField = new JTextArea(node.getPrompt());
        responseField = new JTextArea(node.getResponse());

        JScrollPane promptFieldPane = new JScrollPane(promptField);
        JScrollPane responseFieldPane = new JScrollPane(responseField);

        idField.setBorder(new MatteBorder(1, 1, 1, 1, Color.lightGray));

        GridBagConstraints idFieldGBC = new GridBagConstraints(1, 0, 1, 1, 1d, 0d, 10, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints promptFieldGBC = new GridBagConstraints(1, 1, 1, 1, 1d, 1d, 10, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints responseFieldGBC = new GridBagConstraints(1, 2, 1, 1, 1d, 1d, 10, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);

        add(idField, idFieldGBC);
        add(promptFieldPane, promptFieldGBC);
        add(responseFieldPane, responseFieldGBC);

        JButton submit = new JButton("Submit");

        submit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
                dispose();
            }
        });

        GridBagConstraints submitGBC = new GridBagConstraints(1, 3, 1, 1, 0d, 0d, GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        add(submit, submitGBC);
    }

}
