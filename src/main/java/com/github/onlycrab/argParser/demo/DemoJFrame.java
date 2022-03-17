package com.github.onlycrab.argParser.demo;

import com.github.onlycrab.argParser.arguments.xml.Validator;
import com.github.onlycrab.argParser.common.ExternalReader;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ItemEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Visual demo to get acquainted with the main features of the project.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
class DemoJFrame extends JFrame {
    static final String SORRY_TEXT = "Oops, an unexpected error occurred : %s. \n" +
            "It seems that someone made changes to the source codes and did not check the work of the demonstrator. " +
            "Sorry!=)";
    private static final String SORRY_TITLE = "Error";

    /**
     * Font licensed under SIL Open Font License.
     * https://www.1001fonts.com/inconsolata-font.html
     */
    private static final String OUT_FONT = "inconsolata.regular.ttf";

    private static final String PROJECT_LINK = "https://github.com/onlycrab/ArgumentParser";
    private static final String PROJECT_NAME = "ArgParser";

    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    private JTextPane textPaneInfo;
    private JComboBox comboBoxExample;
    private JButton buttonExampleSubmit;
    private JTextField textFieldExampleIn;
    private JTextArea textAreaExampleOut;
    private JButton buttonExampleViewCode;
    private JTextPane textPaneExampleDescription;
    private JButton buttonXMLValidate;
    private JTextArea textAreaXMLData;
    private JButton buttonXMLViewSchema;
    private JTextPane textPaneXMLDescription;
    private JTextArea textAreaXMLOut;
    private JButton buttonXMLSelectFile;

    private JFileChooser fileChooser;
    private Clipboard clipboard;

    private HtmlViewerJFrame htmlViewer;
    private Dimension screenSize;

    private DemoJFrame() {
        super(PROJECT_NAME);
        setupUI();

        //Get project version
        final InputStream mfStream = getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
        if (mfStream != null) {
            String implTitle = null;
            String implVersion = null;
            Manifest mf = new Manifest();
            try {
                mf.read(mfStream);
                implTitle = mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE);
                implVersion = mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            } catch (IOException ignore) {
            }
            if (implTitle != null && implVersion != null) {
                setTitle(String.format("%s (%s-%s)", PROJECT_NAME, implTitle, implVersion));
            }
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        try {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = (int) screenSize.getWidth();
            int height = (int) screenSize.getHeight();
            setSize(width / 2, height / 2);
        } catch (Exception e) {
            setSize(500, 400);
        }

        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (Exception ignored) {
        }

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                getRootPane().setDefaultButton(buttonExampleSubmit);
            } else if (tabbedPane.getSelectedIndex() == 2) {
                getRootPane().setDefaultButton(buttonXMLValidate);
            }
        });

        initInfoTab();
        initExamplesTab();
        initXmlTab();

        setFont();

        getRootPane().setDefaultButton(buttonExampleSubmit);

        setVisible(true);
    }

    public static void main(String[] args) {
        //Start GUI
        try {
            new DemoJFrame();
        } catch (HeadlessException e) {
            System.out.println("This is <" + PROJECT_NAME + ">. For information about the project visit " + PROJECT_LINK + ".");
            System.out.println("Sorry, but demo gui could not been started : " + e.getMessage());
        }
    }

    /**
     * Initialize tag {@code InfoTab}.
     */
    private void initInfoTab() {
        createCopyMenu(textPaneInfo);
        textPaneInfo.setContentType("text/html");
        textPaneInfo.addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(e.getURL().toURI());
                } catch (Exception ignore) {
                }
            }
        });
        try {
            textPaneInfo.setText(read(DemoExamples.INFO.DESCRIPTION));
        } catch (IOException e) {
            textPaneInfo.setText(String.format(SORRY_TEXT, e.getMessage()));
        }
        textPaneInfo.setCaretPosition(0);
    }

    /**
     * Initialize tab {@code Examples}.
     */
    private void initExamplesTab() {
        createCopyMenu(textPaneExampleDescription);
        createCopyMenu(textAreaExampleOut);
        createMenu(textFieldExampleIn);
        textPaneExampleDescription.setContentType("text/html");

        //noinspection unchecked
        comboBoxExample.addItem(new DemoJFrame.ComboBoxItem(DemoExamples.BASIC.NAME));
        //noinspection unchecked
        comboBoxExample.addItem(new DemoJFrame.ComboBoxItem(DemoExamples.CONVERTER.NAME));
        //noinspection unchecked
        comboBoxExample.addItem(new DemoJFrame.ComboBoxItem(DemoExamples.DEPENDENCIES.NAME));
        //noinspection unchecked
        comboBoxExample.addItem(new DemoJFrame.ComboBoxItem(DemoExamples.RULES.NAME));
        //noinspection unchecked
        comboBoxExample.addItem(new DemoJFrame.ComboBoxItem(DemoExamples.XML.NAME));

        //Print description
        comboBoxExample.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                textFieldExampleIn.setText("");
                textAreaExampleOut.setText("");
                Object item = e.getItem();
                try {
                    if (item.equals(DemoExamples.BASIC.NAME)) {
                        textPaneExampleDescription.setText(read(DemoExamples.BASIC.DESCRIPTION));
                    } else if (item.equals(DemoExamples.CONVERTER.NAME)) {
                        textPaneExampleDescription.setText(read(DemoExamples.CONVERTER.DESCRIPTION));
                    } else if (item.equals(DemoExamples.DEPENDENCIES.NAME)) {
                        textPaneExampleDescription.setText(read(DemoExamples.DEPENDENCIES.DESCRIPTION));
                    } else if (item.equals(DemoExamples.RULES.NAME)) {
                        textPaneExampleDescription.setText(read(DemoExamples.RULES.DESCRIPTION));
                    } else if (item.equals(DemoExamples.XML.NAME)) {
                        textPaneExampleDescription.setText(read(DemoExamples.XML.DESCRIPTION));
                    }
                } catch (Exception ex) {
                    textPaneExampleDescription.setText(String.format(SORRY_TEXT, ex.getMessage()));
                }
                textPaneExampleDescription.setCaretPosition(0);
            }
        });

        //Change element to call event handler
        comboBoxExample.setSelectedIndex(1);
        comboBoxExample.setSelectedIndex(0);

        //Execute command
        buttonExampleSubmit.addActionListener(e -> {
            if (comboBoxExample.getSelectedItem() == null) {
                return;
            }
            if (comboBoxExample.getSelectedItem().equals(DemoExamples.BASIC.NAME)) {
                textAreaExampleOut.setText(SimplyExample.main(splitArgs(textFieldExampleIn.getText())));
            } else if (comboBoxExample.getSelectedItem().equals(DemoExamples.CONVERTER.NAME)) {
                textAreaExampleOut.setText(ConverterExample.main(splitArgs(textFieldExampleIn.getText())));
            } else if (comboBoxExample.getSelectedItem().equals(DemoExamples.DEPENDENCIES.NAME)) {
                textAreaExampleOut.setText(DependenciesExample.main(splitArgs(textFieldExampleIn.getText())));
            } else if (comboBoxExample.getSelectedItem().equals(DemoExamples.RULES.NAME)) {
                textAreaExampleOut.setText(CustomRulesExample.main(splitArgs(textFieldExampleIn.getText())));
            } else if (comboBoxExample.getSelectedItem().equals(DemoExamples.XML.NAME)) {
                textAreaExampleOut.setText(XMLExample.main(splitArgs(textFieldExampleIn.getText())));
            }
            textAreaExampleOut.setCaretPosition(0);
        });

        //Print code example at new frame
        buttonExampleViewCode.addActionListener(e -> {
            if (comboBoxExample.getSelectedItem() == null) {
                return;
            }
            try {
                if (comboBoxExample.getSelectedItem().equals(DemoExamples.BASIC.NAME)) {
                    viewHtml(DemoExamples.BASIC.SOURCE_NAME, read(DemoExamples.BASIC.SOURCE));
                } else if (comboBoxExample.getSelectedItem().equals(DemoExamples.CONVERTER.NAME)) {
                    viewHtml(DemoExamples.CONVERTER.SOURCE_NAME, read(DemoExamples.CONVERTER.SOURCE));
                } else if (comboBoxExample.getSelectedItem().equals(DemoExamples.DEPENDENCIES.NAME)) {
                    viewHtml(DemoExamples.DEPENDENCIES.SOURCE_NAME, read(DemoExamples.DEPENDENCIES.SOURCE));
                } else if (comboBoxExample.getSelectedItem().equals(DemoExamples.RULES.NAME)) {
                    viewHtml(DemoExamples.RULES.SOURCE_NAME, read(DemoExamples.RULES.SOURCE));
                } else if (comboBoxExample.getSelectedItem().equals(DemoExamples.XML.NAME)) {
                    viewHtml(DemoExamples.XML.SOURCE_NAME, read(DemoExamples.XML.SOURCE));
                }
            } catch (Exception ex) {
                viewHtml(SORRY_TITLE, String.format(SORRY_TEXT, ex.getMessage()));
                ex.printStackTrace();
            }
        });
    }

    /**
     * Initialize tab {@code XML}.
     */
    private void initXmlTab() {
        createCopyMenu(textPaneXMLDescription);
        createCopyMenu(textAreaXMLOut);
        createMenu(textAreaXMLData);
        textPaneXMLDescription.setContentType("text/html");

        //Print description
        try {
            textPaneXMLDescription.setText(read(DemoExamples.VALIDATOR.DESCRIPTION));
        } catch (Exception ex) {
            textPaneXMLDescription.setText(String.format(SORRY_TEXT, ex.getMessage()));
        }
        textPaneXMLDescription.setCaretPosition(0);

        //Validate XML data
        buttonXMLValidate.addActionListener(e -> {
            Validator validator = new Validator();
            validator.validate(DemoJFrame.class.getResourceAsStream(DemoExamples.VALIDATOR.ORIGINAL), new ByteArrayInputStream(textAreaXMLData.getText().getBytes()));
            textAreaXMLOut.setText(validator.getMessage());
            textAreaXMLOut.setCaretPosition(0);
        });

        fileChooser = new JFileChooser();

        //Load data from file
        buttonXMLSelectFile.addActionListener(e -> {
            int choose = fileChooser.showOpenDialog(contentPane);
            if (choose == JFileChooser.APPROVE_OPTION) {
                String data;
                try {
                    data = ExternalReader.readFile(fileChooser.getSelectedFile().getAbsolutePath());
                } catch (IOException ex) {
                    textAreaXMLData.setText(ex.getMessage());
                    textAreaXMLData.setCaretPosition(0);
                    textAreaXMLOut.setText("");
                    return;
                }
                if (textAreaXMLData.getText().trim().length() != 0) {
                    if (!textAreaXMLData.getText().equals(data)) {
                        int answer = JOptionPane.showConfirmDialog(
                                contentPane,
                                "Your previously entered data will be overwritten. Continue?",
                                "Warning",
                                JOptionPane.OK_CANCEL_OPTION
                        );
                        if (answer != JOptionPane.OK_OPTION) {
                            return;
                        }
                    }
                }
                textAreaXMLData.setText(data);
                textAreaXMLData.setCaretPosition(0);
                textAreaXMLOut.setText("");
            }
        });

        //Print schema at new frame
        buttonXMLViewSchema.addActionListener(e -> {
            try {
                viewHtml(DemoExamples.VALIDATOR.SOURCE_NAME, read(DemoExamples.VALIDATOR.SOURCE));
            } catch (Exception ex) {
                viewHtml(SORRY_TITLE, String.format(SORRY_TEXT, ex.getMessage()));
            }
        });
    }

    /**
     * Set external font for input and output components.
     */
    private void setFont() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(OUT_FONT);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            font = font.deriveFont(14f);
            textFieldExampleIn.setFont(font);
            textAreaXMLData.setFont(font);
            textAreaExampleOut.setFont(font);
            textAreaXMLOut.setFont(font);
        } catch (Exception ignore) {
        }
    }

    /**
     * Print HTML data to additional window.
     *
     * @param title window title
     * @param html  data to print
     */
    private void viewHtml(String title, String html) {
        if (htmlViewer == null) {
            htmlViewer = new HtmlViewerJFrame(title, html);
            htmlViewer.pack();
            //Resize if viewer size is to large
            if (screenSize.getWidth() * 0.9 < htmlViewer.getWidth() || screenSize.getHeight() * 0.9 < htmlViewer.getHeight()) {
                htmlViewer.setSize((int) Math.min(screenSize.getWidth() * 0.9, htmlViewer.getWidth()),
                        (int) Math.min(screenSize.getHeight() * 0.9, htmlViewer.getHeight()));
            }
        } else {
            htmlViewer.printHTML(title, html);
        }
    }

    /**
     * Create copy popup menu.
     *
     * @param component to which the popup menu will be added
     */
    private void createCopyMenu(JTextComponent component) {
        if (clipboard == null || component == null) {
            return;
        }
        JMenuItem selectAll = new JMenuItem("Select all");
        selectAll.addActionListener(e -> component.selectAll());
        JMenuItem copy = new JMenuItem("Copy");
        copy.addActionListener(e -> {
            try {
                clipboard.setContents(new StringSelection(component.getSelectedText()), null);
            } catch (Exception ignored) {
                component.setInheritsPopupMenu(false);
            }
        });
        JPopupMenu menu = new JPopupMenu();
        menu.add(selectAll);
        menu.add(new JSeparator());
        menu.add(copy);
        component.setComponentPopupMenu(menu);
        component.setInheritsPopupMenu(true);
    }

    /**
     * Create full popup menu.
     *
     * @param component to which the popup menu will be added
     */
    private void createMenu(JTextComponent component) {
        if (clipboard == null || component == null) {
            return;
        }
        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        JMenuItem remove = new JMenuItem("Remove");

        copy.addActionListener(e -> {
            try {
                clipboard.setContents(new StringSelection(component.getSelectedText()), null);
            } catch (Exception ignored) {
                component.setInheritsPopupMenu(false);
            }
        });

        paste.addActionListener(e -> {
            try {
                Transferable transferred = clipboard.getContents(this);
                if (transferred == null) {
                    return;
                }
                String newText = (String) transferred.getTransferData(DataFlavor.stringFlavor);
                String text = component.getSelectedText();
                if (text != null) {
                    component.replaceSelection(newText);
                } else {
                    int caretPos = component.getCaretPosition();
                    text = component.getText();
                    text = text.substring(0, caretPos) + newText + text.substring(caretPos);
                    caretPos = caretPos + newText.length();
                    component.setText(text);
                    component.setCaretPosition(caretPos);
                }
            } catch (Exception ignored) {
            }
        });

        cut.addActionListener(e -> {
            try {
                clipboard.setContents(new StringSelection(component.getSelectedText()), null);
                component.replaceSelection("");
            } catch (Exception ignored) {
                component.setInheritsPopupMenu(false);
            }
        });

        remove.addActionListener(e -> component.replaceSelection(""));

        JPopupMenu menu = new JPopupMenu();
        menu.add(cut);
        menu.add(copy);
        menu.add(paste);
        menu.add(remove);
        component.setComponentPopupMenu(menu);
        component.setInheritsPopupMenu(true);
    }

    /**
     * Read resource data to {@code String}.
     *
     * @param resource resource to read data from
     * @return resource as {@code String}
     * @throws IOException if an I/O error occurs
     */
    private String read(String resource) throws IOException {
        return ExternalReader.readStream(getClass().getClassLoader().getResourceAsStream(resource));
    }

    /**
     * Split input line to array.
     *
     * @param argsLine input console string
     * @return input line like array
     */
    private String[] splitArgs(String argsLine) {
        if (argsLine == null) {
            return new String[0];
        } else if (argsLine.length() == 0) {
            return new String[0];
        } else if (argsLine.length() < 2) {
            return new String[]{argsLine};
        } else if (argsLine.indexOf('"') < 0) {
            return argsLine.split(" ");
        }
        List<String> list = new ArrayList<>();
        char[] argsChars = argsLine.toCharArray();
        int quotesCount = 0;
        for (char c : argsChars) {
            if (c == '"') {
                quotesCount++;
            }
        }
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < argsChars.length; i++) {
            if (argsChars[i] == '"') {
                quotesCount--;
                if (i != argsChars.length - 1) {
                    if (inQuotes) {
                        if (argsChars[i + 1] == ' ') {
                            list.add(sb.toString());
                            sb = new StringBuilder();
                            inQuotes = false;
                        } else {
                            sb.append(argsChars[i]);
                        }
                    } else {
                        if (quotesCount > 0) {
                            inQuotes = true;
                        } else {
                            sb.append(argsChars[i]);
                        }
                    }
                } else {
                    //END
                    if (inQuotes) {
                        list.add(sb.toString());
                        inQuotes = false;
                    } else {
                        list.add("\"");
                    }
                }
            } else {
                if (i != argsChars.length - 1) {
                    if (argsChars[i] == ' ') {
                        if (inQuotes) {
                            sb.append(argsChars[i]);
                        } else {
                            if (sb.length() > 0) {
                                list.add(sb.toString());
                                sb = new StringBuilder();
                            }
                        }
                    } else {
                        sb.append(argsChars[i]);
                    }
                } else {
                    //END
                    sb.append(argsChars[i]);
                    list.add(sb.toString());
                }
            }
        }

        String[] res = new String[list.size()];
        list.toArray(res);
        return res;
    }

    private static final class ComboBoxItem {
        private final String text;

        private ComboBoxItem(String text) {
            if (text != null) {
                this.text = text;
            } else {
                this.text = "";
            }
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (this == obj) {
                return true;
            }
            if (obj instanceof DemoJFrame.ComboBoxItem) {
                return text.equals(((DemoJFrame.ComboBoxItem) obj).text);
            } else if (obj instanceof String) {
                return text.equals(obj);
            } else {
                return false;
            }
        }
    }

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        tabbedPane = new JTabbedPane();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(tabbedPane, gbc);
        setupUiTabInfo();
        setupUiTabExamples();
        setupUiTabXml();
    }

    private void setupUiTabInfo(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        tabbedPane.addTab("Info", panel);
        JScrollPane scrollPane1 = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane1, gbc);
        textPaneInfo = new JTextPane();
        textPaneInfo.setEditable(false);
        scrollPane1.setViewportView(textPaneInfo);

    }

    private void setupUiTabExamples(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        tabbedPane.addTab("Examples", panel);
        comboBoxExample = new JComboBox();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comboBoxExample, gbc);
        JScrollPane scrollPaneDescription = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPaneDescription, gbc);
        textPaneExampleDescription = new JTextPane();
        textPaneExampleDescription.setEditable(false);
        scrollPaneDescription.setViewportView(textPaneExampleDescription);
        buttonExampleSubmit = new JButton();
        buttonExampleSubmit.setText("Submit");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(buttonExampleSubmit, gbc);
        textFieldExampleIn = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.ipady = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(textFieldExampleIn, gbc);
        buttonExampleViewCode = new JButton();
        buttonExampleViewCode.setText("View code");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(buttonExampleViewCode, gbc);
        JScrollPane scrollPaneOut = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPaneOut, gbc);
        textAreaExampleOut = new JTextArea();
        textAreaExampleOut.setBackground(new Color(-16777216));
        textAreaExampleOut.setEditable(false);
        textAreaExampleOut.setForeground(new Color(-1));
        scrollPaneOut.setViewportView(textAreaExampleOut);
    }

    private void setupUiTabXml(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        tabbedPane.addTab("XML", panel);
        JScrollPane scrollPaneDescription = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPaneDescription, gbc);
        textPaneXMLDescription = new JTextPane();
        textPaneXMLDescription.setEditable(false);
        scrollPaneDescription.setViewportView(textPaneXMLDescription);
        buttonXMLValidate = new JButton();
        buttonXMLValidate.setText("Validate");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(buttonXMLValidate, gbc);
        buttonXMLSelectFile = new JButton();
        buttonXMLSelectFile.setText("Load from file ...");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(buttonXMLSelectFile, gbc);
        buttonXMLViewSchema = new JButton();
        buttonXMLViewSchema.setText("View schema");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel.add(buttonXMLViewSchema, gbc);
        JScrollPane scrollPaneXmlData = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPaneXmlData, gbc);
        textAreaXMLData = new JTextArea();
        scrollPaneXmlData.setViewportView(textAreaXMLData);
        JScrollPane scrollPaneXmlOut = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPaneXmlOut, gbc);
        textAreaXMLOut = new JTextArea();
        textAreaXMLOut.setBackground(new Color(-16777216));
        textAreaXMLOut.setForeground(new Color(-1));
        scrollPaneXmlOut.setViewportView(textAreaXMLOut);
        JPanel spacer = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(spacer, gbc);
    }
}