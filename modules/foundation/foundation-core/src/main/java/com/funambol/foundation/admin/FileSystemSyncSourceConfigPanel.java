/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.foundation.admin;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import java.io.Serializable;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSourceInfo;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.SourceManagementPanel;

import com.funambol.foundation.engine.source.FileSystemSyncSource;

import org.apache.commons.lang.StringUtils;


/**
 * This class implements the configuration panel for a FileSystemSyncSource
 *
 * @version $Id: FileSystemSyncSourceConfigPanel.java,v 1.1.1.1 2008-03-20 21:38:30 stefano_fornari Exp $
 */
public class FileSystemSyncSourceConfigPanel
extends SourceManagementPanel
implements Serializable {

  // ----------------------------------------------------------------- Constants

  public static final String NAME_ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.";

  // -------------------------------------------------------------- Private data
  /** label for the panel's name */
  private JLabel panelName = new JLabel();

  /** border to evidence the title of the panel */
  private TitledBorder titledBorder1;

  private JLabel nameLabel = new JLabel();
  private JTextField nameValue = new JTextField();

  private JLabel sourceUriLabel = new JLabel();
  private JTextField sourceUriValue = new JTextField();

  private JLabel directoryLabel = new JLabel();
  private JTextField directoryValue = new JTextField();

  private JLabel infoTypesLabel = new JLabel();
  private JTextField infoTypesValue = new JTextField();

  private JLabel infoVersionsLabel = new JLabel();
  private JTextField infoVersionsValue = new JTextField();

  private JLabel multiUserLabel = new JLabel();
  private JCheckBox multiUserValue = new JCheckBox();

  private JButton confirmButton = new JButton();

  // ------------------------------------------------------------ Constructors


  public FileSystemSyncSourceConfigPanel() {
      init();
  }


  // ----------------------------------------------------------- Private methods
  /**
   * Create the panel
   * @throws Exception if error occures during creation of the panel
   */

  private void init() {
    // set layout
    this.setLayout(null);

    // set properties of label, position and border
    //  referred to the title of the panel
    titledBorder1 = new TitledBorder("");

    panelName.setFont(titlePanelFont);
    panelName.setText("Edit File System SyncSource");
    panelName.setBounds(new Rectangle(14, 5, 316, 28));
    panelName.setAlignmentX(SwingConstants.CENTER);
    panelName.setBorder(titledBorder1);

    int y  = 60;
    int dy = 30;
    
    sourceUriLabel.setText("Source URI: ");
    sourceUriLabel.setFont(defaultFont);
    sourceUriLabel.setBounds(new Rectangle(14, y, 150, 18));
    sourceUriValue.setFont(defaultFont);
    sourceUriValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;
    
    nameLabel.setText("Name: ");
    nameLabel.setFont(defaultFont);
    nameLabel.setBounds(new Rectangle(14, y, 150, 18));
    nameValue.setFont(defaultFont);
    nameValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;
    
    directoryLabel.setText("Source Directory: ");
    directoryLabel.setFont(defaultFont);
    directoryLabel.setBounds(new Rectangle(14, y, 150, 18));
    directoryValue.setFont(defaultFont);
    directoryValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;
    
    infoTypesLabel.setText("Supported types: ");
    infoTypesLabel.setFont(defaultFont);
    infoTypesLabel.setBounds(new Rectangle(14, y, 150, 18));
    infoTypesValue.setFont(defaultFont);
    infoTypesValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;
    
    infoVersionsLabel.setText("Supported versions: ");
    infoVersionsLabel.setFont(defaultFont);
    infoVersionsLabel.setBounds(new Rectangle(14, y, 150, 18));
    infoVersionsValue.setFont(defaultFont);
    infoVersionsValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;
    
    multiUserLabel.setText("MultiUser: ");
    multiUserLabel.setFont(defaultFont);
    multiUserLabel.setBounds(new Rectangle(14, y, 150, 18));
    multiUserValue.setSelected(false);
    multiUserValue.setBounds(167, y, 50, 25);

    y += dy;
    y += dy;
    
    confirmButton.setFont(defaultFont);
    confirmButton.setText("Add");
    confirmButton.setBounds(170, y, 70, 25);

    confirmButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event ) {
            try {
                validateValues();
                getValues();
                if (getState() == STATE_INSERT) {
                    FileSystemSyncSourceConfigPanel.this.actionPerformed(new ActionEvent(FileSystemSyncSourceConfigPanel.this, ACTION_EVENT_INSERT, event.getActionCommand()));
                } else {
                    FileSystemSyncSourceConfigPanel.this.actionPerformed(new ActionEvent(FileSystemSyncSourceConfigPanel.this, ACTION_EVENT_UPDATE, event.getActionCommand()));
                }
            } catch (Exception e) {
                notifyError(new AdminException(e.getMessage()));
            }
        }
    });


    // add all components to the panel
    this.add(panelName        , null);
    this.add(sourceUriLabel   , null);
    this.add(sourceUriValue   , null);
    this.add(nameLabel        , null);
    this.add(nameValue        , null);
    this.add(directoryLabel   , null);
    this.add(directoryValue   , null);
    this.add(infoTypesLabel   , null);
    this.add(infoTypesValue   , null);
    this.add(infoVersionsLabel, null);
    this.add(infoVersionsValue, null);
    this.add(multiUserLabel   , null);
    this.add(multiUserValue   , null);
    this.add(confirmButton    , null);

  }


  public void updateForm() {
      if (!(getSyncSource() instanceof FileSystemSyncSource)) {
          notifyError(
              new AdminException(
                  "This is not a FileSystemSyncSource! Unable to process SyncSource values."
              )
          );
          return;
      }

      FileSystemSyncSource syncSource = (FileSystemSyncSource)getSyncSource();

      if (getState() == STATE_INSERT) {
          confirmButton.setText("Add");
      } else if (getState() == STATE_UPDATE) {
          confirmButton.setText("Save");
      }

      directoryValue.setText(syncSource.getSourceDirectory());
      sourceUriValue.setText(syncSource.getSourceURI()      );
      nameValue.setText     (syncSource.getName()           );

      multiUserValue.setSelected(syncSource.isMultiUser());

      SyncSourceInfo info = syncSource.getInfo();
      if (info != null) {
          ContentType[] types = info.getSupportedTypes();

          StringBuffer typesList = new StringBuffer(), versionsList = new StringBuffer();
          for (int i=0; ((types != null) && (i<types.length)); ++i) {
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

      if (syncSource.getSourceURI() != null) {
          sourceUriValue.setEditable(false);
      }
  }

  /**
   * Set preferredSize of the panel
   * @return preferredSize of the panel
   */
  public Dimension getPreferredSize() {
      return new Dimension(525, 330);
  }

  // ----------------------------------------------------------- Private methods

  /**
   * Checks if the values provided by the user are all valid. In caso of errors,
   * a IllegalArgumentException is thrown.
   *
   * @throws IllegalArgumentException if:
   *         <ul>
   *         <li>name, uri, type or directory are empty (null or zero-length)
   *         <li>the types list length does not match the versions list length
   *         </ul>
   */
  private void validateValues() throws IllegalArgumentException {
      String value = null;

      value = nameValue.getText();
      if (StringUtils.isEmpty(value)) {
          throw new IllegalArgumentException("Field 'Name' cannot be empty. Please provide a SyncSource name.");
      }

      if (!StringUtils.containsOnly(value, NAME_ALLOWED_CHARS.toCharArray())) {
          throw new IllegalArgumentException("Only the following characters are allowed for field 'Name': \n" + NAME_ALLOWED_CHARS);
      }

      value = sourceUriValue.getText();
      if (StringUtils.isEmpty(value)) {
          throw new IllegalArgumentException("Field 'Source URI' cannot be empty. Please provide a SyncSource URI.");
      }

      value = directoryValue.getText();
      if (StringUtils.isEmpty(value)) {
          throw new IllegalArgumentException("Field 'Source directory' cannot be empty. Please provide a SyncSource db directory.");
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
   * Set syncSource properties with the values provided by the user.
   */
  private void getValues() {
      FileSystemSyncSource syncSource = (FileSystemSyncSource)getSyncSource();

      StringTokenizer types    = new StringTokenizer(infoTypesValue.getText(),    ",");
      StringTokenizer versions = new StringTokenizer(infoVersionsValue.getText(), ",");

      syncSource.setSourceURI      (sourceUriValue.getText().trim());
      syncSource.setName           (nameValue.getText().trim()     );
      syncSource.setSourceDirectory(directoryValue.getText().trim());

      syncSource.setEncode(false);

      syncSource.setMultiUser(multiUserValue.isSelected());

      ContentType[] contentTypes= new ContentType[types.countTokens()];

      for(int i = 0; i<contentTypes.length; ++i) {
          contentTypes[i] = new ContentType(types.nextToken().trim(), versions.nextToken().trim());
      }

      syncSource.setInfo(new SyncSourceInfo(contentTypes, 0));
    }

}
