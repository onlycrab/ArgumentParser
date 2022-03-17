package com.github.onlycrab.argParser.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Frame only for printing html.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */

class HtmlViewerJFrame extends JFrame {
    private JPanel contentPane;
    private JTextPane textPane;

    private Clipboard clipboard;

    /**
     * Create new viewer and print html data.
     *
     * @param title frame title
     * @param html  data to printing
     */
    public HtmlViewerJFrame(String title, String html) {
        super();
        setupUI();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setContentPane(contentPane);
        textPane.setContentType("text/html");

        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            JMenuItem selectAll = new JMenuItem("Select all");
            selectAll.addActionListener(e -> textPane.selectAll());
            JMenuItem copy = new JMenuItem("Copy");
            copy.addActionListener(e -> {
                try {
                    clipboard.setContents(new StringSelection(textPane.getSelectedText()), null);
                } catch (Exception ignored) {
                }
            });
            JPopupMenu menu = new JPopupMenu();
            menu.add(selectAll);
            menu.add(new JSeparator());
            menu.add(copy);
            textPane.setComponentPopupMenu(menu);
            textPane.setInheritsPopupMenu(true);
        } catch (Exception ignored) {
        }

        printHTML(title, html);
    }

    /**
     * Print html.
     *
     * @param title frame title
     * @param html  data to printing
     */
    public void printHTML(String title, String html) {
        setTitle(title);
        textPane.setText(html);
        textPane.setCaretPosition(0);
        setVisible(true);
        requestFocus();
    }

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final JScrollPane scrollPane1 = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(scrollPane1, gbc);
        textPane = new JTextPane();
        textPane.setEditable(false);
        scrollPane1.setViewportView(textPane);
    }
}
