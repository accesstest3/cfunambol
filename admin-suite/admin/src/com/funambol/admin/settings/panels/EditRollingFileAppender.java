/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2006 - 2007 Funambol, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission 
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE 
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 * 
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite 
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 * 
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 * 
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably 
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */

package com.funambol.admin.settings.panels;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.*;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;

import com.funambol.admin.AdminException;
import com.funambol.admin.mo.ManagementObject;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.ManagementObjectPanel;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import org.apache.log4j.RollingFileAppender;

/**
 * RollingFileAppender editing panel.
 *
 * @version $Id: EditRollingFileAppender.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class EditRollingFileAppender extends ManagementObjectPanel {

    // --------------------------------------------------------------- Constants
    public static long MAX_FILE_SIZE = 8796093022208L; // 8796093022208 MB =
                                                       // 2^ 63 bytes

    // ------------------------------------------------------------ Private data

    /** label for the panel's name */
    private JLabel panelNameLabel = new JLabel();

    /** label for the name */
    private JLabel nameLabel = new JLabel();

    /** the name */
    private JLabel nameLabel2 = new JLabel();

    /** label for the file name */
    private JLabel fileNameLabel = new JLabel();

    /** the file name */
    private JTextField fileNameValue = new JTextField();

    /** label for the file size */
    private JLabel fileSizeLabel = new JLabel();

    /** the file size */
    private JTextField fileSizeValue = new JTextField();

    /** label for the file size limit MB */
    private JLabel limitMBLabel = new JLabel(Bundle.getMessage(Bundle.LABEL_MB));

    /** label for the file rotation count */
    private JLabel fileRotationCountLabel = new JLabel();

    /** the file rotation count */
    private JTextField fileRotationCountValue = new JTextField();

    /** label for the conversionPattern */
    private JLabel conversionPatternLabel = new JLabel();

    /** the conversion pattern */
    private JTextField conversionPattern = new JTextField();

   /** label for the note */
    private JLabel noteLabel = new JLabel();

    /** Confirm button*/
    private JButton confirmButton = new JButton();

    private RollingFileAppender appender = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel to edit a console appender.
     */
    public EditRollingFileAppender() {

        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Set preferredSize of the panel
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(525, 245);
    }

    // ---------------------------------------------------------- Private method
    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel.
     */
    private void init() throws Exception {

        final int h  = 18;
        final int x1 = 15;
        final int x2 = 150;
        final int w1 = 100;
        final int w2 = 310;

        int y  = 40;
        int dy = 25;

        setLayout(null);
        setName(Bundle.getMessage(Bundle.EDIT_ROLLING_FILE_APPENDER_PANEL_NAME));

        panelNameLabel.setText(Bundle.getMessage(Bundle.EDIT_ROLLING_FILE_APPENDER_PANEL_NAME));
        panelNameLabel.setBounds(new Rectangle(14, 5, 245, 28));
        panelNameLabel.setAlignmentX(SwingConstants.CENTER);
        panelNameLabel.setBorder(new TitledBorder(""));

        nameLabel.setText(Bundle.getMessage(Bundle.LABEL_APPENDER_NAME) + " :");
        nameLabel.setBounds(new Rectangle(x1, y, w1, h));
        nameLabel2.setBounds(new Rectangle(x2, y, w2, h));

        y += dy;

        fileNameLabel.setText(Bundle.getMessage(Bundle.LABEL_FILE_NAME) + " :");
        fileNameLabel.setBounds(new Rectangle(x1, y, w1, h));
        fileNameValue.setBounds(new Rectangle(x2, y, w2, h));

        y += dy;

        fileSizeLabel.setText(Bundle.getMessage(Bundle.LABEL_MAX_FILE_SIZE) + " :");
        fileSizeLabel.setBounds(new Rectangle(x1, y, w1, h));
        fileSizeValue.setBounds(new Rectangle(x2, y, w2, h));
        fileSizeValue.setHorizontalAlignment(JTextField.RIGHT);
        limitMBLabel.setBounds(new Rectangle(x2 + w2 + 10, y, 50, h));

        y += dy;

        fileRotationCountLabel.setText(Bundle.getMessage(Bundle.LABEL_MAX_BACKUP_INDEX) + " :");
        fileRotationCountLabel.setBounds(new Rectangle(x1, y, w1, h));
        fileRotationCountValue.setBounds(new Rectangle(x2, y, w2, h));
        fileRotationCountValue.setHorizontalAlignment(JTextField.RIGHT);

        y += dy;

        conversionPatternLabel.setText(Bundle.getMessage(Bundle.LABEL_PATTERN_LAYOUT) + " :");
        conversionPatternLabel.setBounds(new Rectangle(x1, y, w1, h));
        conversionPattern.setBounds(new Rectangle(x2, y, w2, h));

        y += dy;

        noteLabel.setText(Bundle.getMessage(Bundle.LABEL_FILE_APPENDER_NOTE));
        noteLabel.setBounds(new Rectangle(x1, y, 500, h));

        y += dy;
        y += dy;

        confirmButton.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE));
        confirmButton.setBounds(new Rectangle(401, y, 60, 25));

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event ) {
                try {
                    validateValues();
                    getValues();
                    EditRollingFileAppender.this.actionPerformed(
                        new ActionEvent(EditRollingFileAppender.this,
                                        ACTION_EVENT_UPDATE,
                                        event.getActionCommand())
                    );
                } catch (Exception e) {
                    notifyError(new AdminException(e.getMessage()));
                }
            }
        });

        add(panelNameLabel            , null);
        add(nameLabel                 , null);
        add(nameLabel2                , null);
        add(fileNameLabel             , null);
        add(fileNameValue             , null);
        add(fileRotationCountLabel    , null);
        add(fileRotationCountValue    , null);
        add(fileSizeLabel             , null);
        add(fileSizeValue             , null);
        add(limitMBLabel              , null);
        add(conversionPatternLabel    , null);
        add(conversionPattern         , null);
        add(noteLabel                 , null);

        add(confirmButton             , null);

        GuiFactory.setDefaultFont(this);
        panelNameLabel.setFont(GuiFactory.titlePanelFont);

    }

    /**
     * Validates the inputs and throws an Exception in case of any invalid data.
     *
     * @throws IllegalArgumentException in case of invalid input
     */
    private void validateValues()
    throws IllegalArgumentException {
        String value = null;

        value = conversionPattern.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("'Conversion pattern' cannot be empty.");
        }
        value = fileNameValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("'File name' cannot be empty.");
        }
        value = fileSizeValue.getText();
        try {
            int intValue = Integer.parseInt(value);
            if (intValue < 0 || intValue > MAX_FILE_SIZE) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            String msg = MessageFormat.format(
                             Bundle.getMessage(Bundle.ERROR_NUMERIC_INPUT),
                             new String[] {
                                 Bundle.getMessage(Bundle.LABEL_MAX_FILE_SIZE),
                                 "0",
                                 String.valueOf(MAX_FILE_SIZE)
                             }
                         );

            throw new IllegalArgumentException(msg);
        }
        value = fileRotationCountValue.getText();
        try {
            int intValue = Integer.parseInt(value);
            if (intValue < 0 || intValue > Integer.MAX_VALUE) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            String msg = MessageFormat.format(
                             Bundle.getMessage(Bundle.ERROR_NUMERIC_INPUT),
                             new String[] {
                                 Bundle.getMessage(Bundle.LABEL_MAX_BACKUP_INDEX),
                                 "0",
                                 String.valueOf(Integer.MAX_VALUE)
                             }
                         );

            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Saves the inserted value in the appender.
     */
    private void getValues() {
        Layout layout = appender.getLayout();
        PatternLayout patternLayout = null;
        String conversionPattern = this.conversionPattern.getText();

        if (!(layout instanceof PatternLayout)) {
            patternLayout = new PatternLayout(conversionPattern);
            appender.setLayout(patternLayout);
        } else {
            patternLayout = (PatternLayout)layout;
        }
        patternLayout.setConversionPattern(conversionPattern);
        appender.setFile(fileNameValue.getText());
        appender.setMaxFileSize(fileSizeValue.getText() + "MB");
        appender.setMaxBackupIndex(
            Integer.parseInt(fileRotationCountValue.getText())
        );
    }

    /**
     * Load the appender to show
     */
    public void updateForm() {
        ManagementObject mo = getManagementObject();
        Object o = mo.getObject();
        if (!(o instanceof RollingFileAppender)) {
            notifyError(new AdminException(
                "This is not a RollingFileAppender. Unable to process appender values.")
            );
            return;
        }
        appender = (RollingFileAppender)o;
        nameLabel2.setText(appender.getName());
        Layout layout = appender.getLayout();
        if (layout instanceof PatternLayout) {
            this.conversionPattern.setText(((PatternLayout)layout).getConversionPattern());
        }
        fileNameValue.setText(appender.getFile());
        fileSizeValue.setText(String.valueOf(appender.getMaximumFileSize() / 1024 /1024));
        fileRotationCountValue.setText(String.valueOf(appender.getMaxBackupIndex()));
    }

}