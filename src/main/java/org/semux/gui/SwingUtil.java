/*
 * Copyright (c) 2017 The Semux Developers
 *
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.semux.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.NumberFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class SwingUtil {

    private static final Logger logger = LoggerFactory.getLogger(SwingUtil.class);

    public static final char DEFAULT_DOUBLE_SEPARATOR = '.';
    public static final String DEFAULT_DOUBLE_FORMAT = "0.000";
    public static final String DEFAULT_PERCENTAGE_FORMAT = "0.0";

    /**
     * Put a JFrame in the center of screen.
     * 
     * @param frame
     * @param width
     * @param height
     */
    public static void centerizeFrame(JFrame frame, int width, int height) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ((int) d.getWidth() - width) / 2;
        int y = ((int) d.getHeight() - height) / 2;
        frame.setLocation((int) x, (int) y);
        frame.setBounds(x, y, width, height);
    }

    /**
     * Load an ImageIcon from resource, and rescale it.
     * 
     * @param imageName
     *            image name
     * @return an image icon if exists, otherwise null
     */
    public static ImageIcon loadImage(String imageName, int width, int height) {
        String imgLocation = imageName + ".png";
        URL imageURL = SwingUtil.class.getResource(imgLocation);

        if (imageURL == null) {
            logger.warn("Resource not found: " + imgLocation);
            return null;
        } else {
            ImageIcon icon = new ImageIcon(imageURL);
            Image img = icon.getImage();
            Image img2 = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img2);
        }
    }

    /**
     * Generate an empty image icon.
     * 
     * @param width
     * @param height
     * @return
     */
    public static ImageIcon emptyImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(new Color(255, 255, 255));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        return new ImageIcon(image);
    }

    /**
     * Set the preferred width of table columns.
     * 
     * @param table
     * @param total
     * @param widths
     */
    public static void setColumnWidths(JTable table, int total, double... widths) {
        TableColumnModel model = table.getColumnModel();
        for (int i = 0; i < widths.length; i++) {
            model.getColumn(i).setPreferredWidth((int) (total * widths[i]));
        }
    }

    /**
     * Set the alignments of table columns.
     * 
     * @param table
     * @param right
     */
    public static void setColumnAlignments(JTable table, boolean... right) {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        TableColumnModel model = table.getColumnModel();
        for (int i = 0; i < right.length; i++) {
            if (right[i]) {
                model.getColumn(i).setCellRenderer(rightRenderer);
            }
        }
    }

    /**
     * Generate an QR image for the given text.
     * 
     * @param text
     * @param size
     * @return
     */
    public static BufferedImage generateQR(String text, int size) {
        try {
            Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintMap.put(EncodeHintType.MARGIN, 2);
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hintMap);
            int width = matrix.getWidth();
            BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, width);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (matrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            return image;
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a copy-paste-cut popup for a component.
     * 
     * @param comp
     */
    private static void addCopyPastePopup(JComponent comp) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem item = new JMenuItem(new DefaultEditorKit.CutAction());
        item.setText("Cut");
        popup.add(item);
        item = new JMenuItem(new DefaultEditorKit.CopyAction());
        item.setText("Copy");
        popup.add(item);
        item = new JMenuItem(new DefaultEditorKit.PasteAction());
        item.setText("Paste");
        popup.add(item);
        comp.setComponentPopupMenu(popup);
    }

    /**
     * Generates an editable text field.
     * 
     * @return
     */
    public static JTextField editableTextField() {
        JTextField textfield = new JTextField();
        addCopyPastePopup(textfield);
        return textfield;
    }

    /**
     * Generates a JFormattedTextfield which only allows integer as entry
     * 
     * @return
     */
    public static JFormattedTextField integerFormattedTextField() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField textField = new JFormattedTextField(formatter);
        addCopyPastePopup(textField);
        return textField;
    }

    /**
     * Generates a JFormattedTextfield which only allows DoubleValues as entry
     * 
     * @return
     */
    public static JFormattedTextField doubleFormattedTextField() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(DEFAULT_DOUBLE_SEPARATOR);
        DecimalFormat decimalFormat = new DecimalFormat(SwingUtil.DEFAULT_DOUBLE_FORMAT, dfs);
        decimalFormat.setGroupingUsed(false);
        JFormattedTextField textField = new JFormattedTextField(decimalFormat);
        addCopyPastePopup(textField);
        return textField;
    }

    /**
     * Formats a given double value correctly to String with given format
     * 
     * @param value
     * @param format
     * @return
     */
    public static String formatDouble(double value, String format) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(DEFAULT_DOUBLE_SEPARATOR);
        DecimalFormat decimalFormat = new DecimalFormat(format, dfs);
        decimalFormat.setGroupingUsed(false);
        return decimalFormat.format(value);
    }

    /**
     * Formats a given double value correctly to String with given format
     * 
     * @param value
     * @return
     */
    public static String formatDouble(double value) {
        return formatDouble(value, DEFAULT_DOUBLE_FORMAT);
    }

    /**
     * Integer number comparator.
     */
    public static final Comparator<Integer> INTEGER_COMPARATOR = (o1, o2) -> {
        return Integer.compare(o1, o2);
    };

    /**
     * Long number comparator.
     */
    public static final Comparator<Long> LONG_COMPARATOR = (o1, o2) -> {
        return Long.compare(o1, o2);
    };

    /**
     * Number string comparator based on its value.
     * 
     * @exception
     */
    public static final Comparator<String> NUMBER_COMPARATOR = (o1, o2) -> {
        try {
            return Double.compare(Double.parseDouble(o1), Double.parseDouble(o2));
        } catch (NumberFormatException e) {
            logger.error("Wrong format or value for parsing to Double", e);
            throw e;
        }
    };

    /**
     * Balance string comparator based on its value.
     * 
     * @exception
     */
    public static final Comparator<String> BALANCE_COMPARATOR = (o1, o2) -> {
        try {
            return Double.compare(Double.parseDouble(o1.substring(0, o1.length() - 4).replaceAll(",", ".")),
                    Double.parseDouble(o2.substring(0, o2.length() - 4).replaceAll(",", ".")));
        } catch (NumberFormatException e) {
            logger.error("Wrong format or value for parsing to Double", e);
            throw new NumberFormatException(e.getLocalizedMessage());
        }
    };

    /**
     * Balance string comparator based on its value.
     * 
     * @exception
     */
    public static final Comparator<String> PERCENTAGE_COMPARATOR = (o1, o2) -> {
        try {
            return Double.compare(Double.parseDouble(o1.substring(0, o1.length() - 2).replaceAll(",", ".")),
                    Double.parseDouble(o2.substring(0, o2.length() - 2).replaceAll(",", ".")));
        } catch (NumberFormatException e) {
            logger.error("Wrong format or value for parsing to Double", e);
            throw new NumberFormatException(e.getLocalizedMessage());
        }
    };

}
