package com.github.vitineth.branch.program.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Contains some wrapper functions for the {@link JFileChooser} class which will launch them with the system layout in
 * the current working directory. All method will use the system laf when launched and reset back to what it was
 * beforehand.
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 31/08/2016.
 *
 * @author Ryan (vitineth)
 * @since 31/08/2016
 */
public class FileUtils {

    /**
     * Shows a {@link JFileChooser} using the {@link JFileChooser#showOpenDialog(Component)} styled  using the systems
     * LAF supplied by {@link UIManager#getSystemLookAndFeelClassName()}. The old LAF is cached and restored once the
     * user completes the selection,
     * <hr>
     * Wrapper function for {@link #showSystemOpenDialog(Component, File)} using a <code>new File("")</code> instance.
     *
     * @param parent {@link Component} The parent component to anchor the JFileChooser to.
     * @return {@link File} The file selected by the user or <code>null</code> if they do not press the approve option.
     */
    public static File showSystemOpenDialog(Component parent) {
        return showSystemOpenDialog(parent, new File(""));
    }

    /**
     * Shows a {@link JFileChooser} using the {@link JFileChooser#showOpenDialog(Component)} styled  using the systems
     * LAF supplied by {@link UIManager#getSystemLookAndFeelClassName()}. The old LAF is cached and restored once the
     * user completes the selection,
     *
     * @param parent {@link Component} The parent component to anchor the JFileChooser to.
     * @return {@link File} The file selected by the user or <code>null</code> if they do not press the approve option.
     */
    public static File showSystemOpenDialog(Component parent, File workingDirectory) {
        try {
            LookAndFeel laf = UIManager.getLookAndFeel();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JFileChooser chooser = new JFileChooser(workingDirectory);
            int option = chooser.showOpenDialog(parent);
            if (option == JFileChooser.APPROVE_OPTION) {
                UIManager.setLookAndFeel(laf);
                return chooser.getSelectedFile();
            }

            UIManager.setLookAndFeel(laf);
            return null;
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Shows a {@link JFileChooser} using the {@link JFileChooser#showSaveDialog(Component)} styled using the systems
     * LAF supplied by {@link UIManager#getSystemLookAndFeelClassName()}. The old LAF is cached and restored once the
     * user completes the selection,
     * <hr>
     * Wrapper function for {@link #showSystemOpenDialog(Component, File)} using a <code>new File("")</code> instance.
     *
     * @param parent {@link Component} The parent component to anchor the JFileChooser to.
     * @return {@link File} The file selected by the user or <code>null</code> if they do not press the approve option.
     */
    public static File showSystemSaveDialog(Component parent) {
        return showSystemSaveDialog(parent, new File(""));
    }

    /**
     * Shows a {@link JFileChooser} using the {@link JFileChooser#showSaveDialog(Component)} styled using the systems
     * LAF supplied by {@link UIManager#getSystemLookAndFeelClassName()}. The old LAF is cached and restored once the
     * user completes the selection,
     *
     * @param parent {@link Component} The parent component to anchor the JFileChooser to.
     * @return {@link File} The file selected by the user or <code>null</code> if they do not press the approve option.
     */
    public static File showSystemSaveDialog(Component parent, File workingDirectory) {
        try {
            LookAndFeel laf = UIManager.getLookAndFeel();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JFileChooser chooser = new JFileChooser(workingDirectory);
            int option = chooser.showSaveDialog(parent);
            if (option == JFileChooser.APPROVE_OPTION) {
                UIManager.setLookAndFeel(laf);
                return chooser.getSelectedFile();
            }

            UIManager.setLookAndFeel(laf);
            return null;
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
