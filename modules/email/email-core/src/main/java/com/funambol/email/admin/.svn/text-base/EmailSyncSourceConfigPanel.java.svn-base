/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.email.admin ;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSourceInfo;
import com.funambol.admin.ui.SourceManagementPanel;
import com.funambol.admin.AdminException;
import com.funambol.admin.mo.SyncSourceManagementObject;


import com.funambol.email.engine.source.EmailSyncSource;


/**
 * This class implements the configuration panel for an EmailSyncSource
 *
 * @version $Id: EmailSyncSourceConfigPanel.java,v 1.4 2008-07-24 13:17:24 testa Exp $
 */
public class EmailSyncSourceConfigPanel
        extends SourceManagementPanel
        implements Serializable {

    // --------------------------------------------------------------- Constants

    public static final String NAME_ALLOWED_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.";
    public static final String PORT_ALLOWED_CHARS = "0123456789";

    // ------------------------------------------------------------- Public Data

    /**
     * label for the panel's name
     */
    public JLabel panelName = new JLabel();

    /**
     * border to evidence the title of the panel
     */
    public TitledBorder titledBorder1;

    public JLabel nameLabel             = new JLabel();
    public JTextField nameValue         = new JTextField();

    public JLabel sourceUriLabel        = new JLabel();
    public JTextField sourceUriValue    = new JTextField();

    public JLabel infoTypesLabel        = new JLabel();
    public JTextField infoTypesValue    = new JTextField();

    public JLabel infoVersionsLabel     = new JLabel();
    public JTextField infoVersionsValue = new JTextField();

    public JLabel     dtEncryptLabel    = new JLabel() ;
    public JCheckBox  dtEncryptValue    = new JCheckBox() ;

    public JLabel contentProviderURLLabel    = new JLabel();
    public JTextField contentProviderURLValue= new JTextField();

    public JLabel signatureLabel             = new JLabel();
    public JTextField signatureValue         = new JTextField();

    public JButton confirmButton = new JButton();

    // ------------------------------------------------------------ Constructors

    public EmailSyncSourceConfigPanel() {
        init();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Create the panel
     */
    public void init() {

        // set layout
        this.setLayout(null);

        // set properties of label, position and border
        //  referred to the title of the panel
        titledBorder1 = new TitledBorder("");

        panelName.setFont(titlePanelFont);
        panelName.setText("Edit Email Connector SyncSource");
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        // --------

        sourceUriLabel.setText("Source URI: ");
        sourceUriLabel.setFont(defaultFont);
        sourceUriLabel.setBounds(new Rectangle(14, 70, 150, 19));
        sourceUriValue.setFont(defaultFont);
        sourceUriValue.setBounds(new Rectangle(170, 70, 350, 19));

        nameLabel.setText("Name: ");
        nameLabel.setFont(defaultFont);
        nameLabel.setBounds(new Rectangle(14, 100, 150, 19));
        nameValue.setFont(defaultFont);
        nameValue.setBounds(new Rectangle(170, 100, 350, 19));

        infoTypesLabel.setText("Supported Types: ");
        infoTypesLabel.setFont(defaultFont);
        infoTypesLabel.setBounds(new Rectangle(14, 130, 150, 19));
        infoTypesValue.setFont(defaultFont);
        infoTypesValue.setBounds(new Rectangle(170, 130, 350, 19));
        infoTypesValue.setEnabled(false);

        infoVersionsLabel.setText("Supported Versions: ");
        infoVersionsLabel.setFont(defaultFont);
        infoVersionsLabel.setBounds(new Rectangle(14, 160, 150, 19));
        infoVersionsValue.setFont(defaultFont);
        infoVersionsValue.setBounds(new Rectangle(170, 160, 350, 19));
        infoVersionsValue.setEnabled(false);

        dtEncryptLabel.setText("Encryption: ");
        dtEncryptLabel.setFont(defaultFont);
        dtEncryptLabel.setBounds(new Rectangle(14, 190, 150, 18));
        dtEncryptValue.setSelected(false);
        dtEncryptValue.setBounds(new Rectangle(166, 190, 25, 19));

        contentProviderURLLabel.setText("Content Provider URL: ");
        contentProviderURLLabel.setFont(defaultFont);
        contentProviderURLLabel.setBounds(new Rectangle(14, 250, 150, 19));
        contentProviderURLValue.setFont(defaultFont);
        contentProviderURLValue.setBounds(new Rectangle(170, 250, 350, 19));

        signatureLabel.setText("Funambol Signature: ");
        signatureLabel.setFont(defaultFont);
        signatureLabel.setBounds(new Rectangle(14, 220, 150, 19));
        signatureValue.setFont(defaultFont);
        signatureValue.setBounds(new Rectangle(170, 220, 350, 19));


        confirmButton.setFont(defaultFont);
        confirmButton.setText("Add");
        confirmButton.setBounds(170, 280, 70, 25);

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    validateValues();
                    getValues();
                    if (getState() == STATE_INSERT) {
                        EmailSyncSourceConfigPanel.this.actionPerformed(
                                new ActionEvent(EmailSyncSourceConfigPanel.this,
                                                ACTION_EVENT_INSERT,
                                                event.getActionCommand()));
                    } else {
                        EmailSyncSourceConfigPanel.this.actionPerformed(
                                new ActionEvent(EmailSyncSourceConfigPanel.this,
                                                ACTION_EVENT_UPDATE,
                                                event.getActionCommand()));
                    }
                } catch (Exception e) {
                    notifyError(new AdminException(e.getMessage()));
                }
            }
        });

        // add all components to the panel
        this.add(panelName, null);
        this.add(sourceUriLabel, null);
        this.add(sourceUriValue, null);
        this.add(nameLabel, null);
        this.add(nameValue, null);
        this.add(infoTypesLabel, null);
        this.add(infoTypesValue, null);
        this.add(infoVersionsLabel, null);
        this.add(infoVersionsValue, null);
        this.add(dtEncryptLabel, null ) ;
        this.add(dtEncryptValue, null ) ;
        // Uncomment the next two lines if you want to show the Content Provider URL
        // input field
        //this.add(contentProviderURLLabel, null);
        //this.add(contentProviderURLValue, null);
        this.add(signatureLabel, null);
        this.add(signatureValue, null);

        this.add(confirmButton, null);

    }


    /**
     * Set preferredSize of the panel
     *
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(525, 320);
    }

    /**
     *
     */
    public void updateForm() {

        if (!(getSyncSource() instanceof EmailSyncSource)) {
            error("This is not a EmailSyncSource! Unable to process SyncSource values.");
            return;
        }

        if (getState() == STATE_INSERT) {
            confirmButton.setText("Add");
        } else if (getState() == STATE_UPDATE) {
            confirmButton.setText("Save");
        }

        EmailSyncSource syncSource = (EmailSyncSource) getSyncSource();

        sourceUriValue.setText(syncSource.getSourceURI());

        nameValue.setText(syncSource.getName());
        
        contentProviderURLValue.setText(syncSource.getContentProviderURL());
        
        signatureValue.setText(syncSource.getFunambolSignature());

        SyncSourceInfo info = syncSource.getInfo();
        if (info != null) {
            ContentType[] types = info.getSupportedTypes();

            StringBuffer typesList = new StringBuffer();
            StringBuffer versionsList = new StringBuffer();

            for (int i = 0; ((types != null) && (i < types.length)); ++i) {

                if (typesList.length() > 0) {
                    typesList.append(',');
                    versionsList.append(',');
                }

                typesList.append(types[i].getType());
                versionsList.append(types[i].getVersion());
            }

            infoTypesValue.setText(typesList.toString());
            infoVersionsValue.setText(versionsList.toString());
        }

        SyncSourceManagementObject mo = (SyncSourceManagementObject)getManagementObject();
        String transformationsRequired = mo.getTransformationsRequired();
        if (transformationsRequired == null){
            dtEncryptValue.setSelected(false);
        } else {
          if (transformationsRequired.equals("des;b64")){
              dtEncryptValue.setSelected(true);
          } else {
              dtEncryptValue.setSelected(false);
          }
        }

        if (syncSource.getSourceURI() != null) {
            sourceUriValue.setEditable(false);
        }

    }


    /**
     * Set syncSource properties with the
     * values provided by the user.
     */
    public void getValues() {

        EmailSyncSource syncSource = (EmailSyncSource) getSyncSource();

        syncSource.setSourceURI(sourceUriValue.getText().trim());

        syncSource.setName(nameValue.getText().trim());

        syncSource.setContentProviderURL(contentProviderURLValue.getText().trim());

        syncSource.setFunambolSignature(signatureValue.getText().trim());

        boolean dtencr = dtEncryptValue.isSelected();
        String transformationsRequired = null;
        if(dtencr){
           transformationsRequired = "des;b64";
        }  else {
           transformationsRequired = null;
        }
        SyncSourceManagementObject mo = (SyncSourceManagementObject)getManagementObject();
        mo.setTransformationsRequired(transformationsRequired);

        StringTokenizer types    = new StringTokenizer(infoTypesValue.getText()   , ",");
        StringTokenizer versions = new StringTokenizer(infoVersionsValue.getText(), ",");

        ContentType[] contentTypes = new ContentType[types.countTokens()];

        for (int i = 0; i < contentTypes.length; ++i) {
            contentTypes[i] = new ContentType(types.nextToken().trim(),
                    versions.nextToken().trim());
        }

        SyncSourceInfo info = new SyncSourceInfo(contentTypes, 0);

        info.setSupportHierarchicalSync(true);

        syncSource.setInfo(info);

    }


    // ----------------------------------------------------------- Private methods

    /**
     * Checks if the values provided by the user are all valid. In caso of errors,
     * a IllegalArgumentException is thrown.
     *
     * @throws IllegalArgumentException if:
     *   <ul>
     *   <li>name, uri, type or directory are empty (null or zero-length)
     *   <li>the types list length does not match the versions list length
     *   <ul>
     */
    public void validateValues() throws IllegalArgumentException {

        String value;

        value = sourceUriValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(
                    "Field 'Source URI' cannot be empty. " +
                            "Please provide a SyncSource URI.");
        }

        value = nameValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(
                    "Field 'Name' cannot be empty. " +
                            "Please provide a SyncSource name.");
        }
        if (!StringUtils.containsOnly(value, NAME_ALLOWED_CHARS.toCharArray())) {
            throw new IllegalArgumentException(
                    "Only the following characters are " +
                            "allowed for field 'Name': \n" +
                            NAME_ALLOWED_CHARS);
        }

        int typesCount = new StringTokenizer(infoTypesValue.getText(), ",").countTokens();
        if (typesCount == 0) {
            throw new IllegalArgumentException("Field 'Supported types' cannot be empty. Please provide one or more supported types.");
        }
        if (typesCount != new StringTokenizer(infoVersionsValue.getText(), ",").countTokens()) {
            throw new IllegalArgumentException("Number of supported types does not match number of versions");
        }

    }

    /**
     *
     *
     * @param msg String
     */
    protected void error(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
  }
}
