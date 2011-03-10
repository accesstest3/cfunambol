/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

import java.io.Serializable;
import java.util.Map;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.tools.StringTools;

import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSourceInfo;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.SourceManagementPanel;

import com.funambol.foundation.engine.source.FileDataObjectSyncSource;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JComboBox;

/**
 * This class implements the configuration panel for a FileSystemSyncSource
 *
 * @version $Id: FileSystemSyncSourceConfigPanel.java,v 1.1.1.1 2008-03-20 21:38:30 stefano_fornari Exp $
 */
public class FileDataObjectSyncSourceConfigPanel
extends SourceManagementPanel
implements Serializable {

  // ----------------------------------------------------------------- Constants

  public static final String NAME_ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.";
  public static final String DEFAULT_MAX_SIZE = "50M";

  private static final String PROVIDER_S3 = "Amazon Simple Storage Service (S3)";
  private static final String PROVIDER_FILESYSTEM = "Local storage";

  // -------------------------------------------------------------- Private data
  /** label for the panel's name */
  private JLabel panelName = new JLabel();

  /** border to evidence the title of the panel */
  private TitledBorder titledBorder1;

  private JLabel nameLabel = new JLabel();
  private JTextField nameValue = new JTextField();

  private JLabel sourceUriLabel = new JLabel();
  private JTextField sourceUriValue = new JTextField();

  private JLabel localRootPathLabel = new JLabel();
  private JTextField localRootPathValue = new JTextField();

  private JLabel storageContainerNameLabel = new JLabel();
  private JTextField storageContainerNameValue = new JTextField();

  private JLabel storageProviderLabel = new JLabel();
  private JComboBox storageProviderValue;

  private JLabel storageIdentityLabel = new JLabel();
  private JTextField storageIdentityValue = new JTextField();

  private JLabel storageCredentialLabel = new JLabel();
  private JTextField storageCredentialValue = new JTextField();

  private JLabel storageFilesystemRootPathLabel = new JLabel();
  private JTextField storageFilesystemRootPathValue = new JTextField();

  private JLabel quotaLabel = new JLabel();
  private JTable quotaTable;
  /** model for the search result table */
  private RolesTableModel model;

  private JLabel notaLabel = new JLabel();
  
  private JButton confirmButton = new JButton();

  //configuration data for storage type field
  //SortedMap is useful in order to match the position of the element in the
  //provider type combobox
  SortedMap<String, StorageProviderWrapper> storagesConfigurations;

  // ------------------------------------------------------------ Constructors


  public FileDataObjectSyncSourceConfigPanel() {
      init();

      //configure the different storage type values
      storagesConfigurations = new TreeMap<String, StorageProviderWrapper>();

      List <StorageFieldConfiguration> fieldsConfigs;
      StorageProviderWrapper providerConfig;

      //create configuration for Filesystem provider
      fieldsConfigs = new ArrayList<StorageFieldConfiguration>();
      fieldsConfigs.add(new StorageFieldConfiguration
              (storageIdentityLabel, storageIdentityValue, "", false));
      fieldsConfigs.add(new StorageFieldConfiguration
              (storageCredentialLabel, storageCredentialValue, "", false));
      fieldsConfigs.add(new StorageFieldConfiguration
              (storageContainerNameLabel, storageContainerNameValue, "Media directory name", true));
      fieldsConfigs.add(new StorageFieldConfiguration
              (storageFilesystemRootPathLabel, storageFilesystemRootPathValue, "Media directory base path", true));
      providerConfig = new StorageProviderWrapper("filesystem", fieldsConfigs);
      storagesConfigurations.put(PROVIDER_FILESYSTEM, providerConfig);

      //create configuration for S3 provider
      fieldsConfigs = new ArrayList<StorageFieldConfiguration>();
      fieldsConfigs.add(new StorageFieldConfiguration
              (storageIdentityLabel, storageIdentityValue, "Amazon S3 access key", true));
      fieldsConfigs.add(new StorageFieldConfiguration
              (storageCredentialLabel, storageCredentialValue, "Amazon S3 secret key", true));
      fieldsConfigs.add(new StorageFieldConfiguration
              (storageContainerNameLabel, storageContainerNameValue, "Bucket name", true));
      fieldsConfigs.add(new StorageFieldConfiguration
              (storageFilesystemRootPathLabel, storageFilesystemRootPathValue, "", false));
      providerConfig = new StorageProviderWrapper("s3", fieldsConfigs);
      storagesConfigurations.put(PROVIDER_S3, providerConfig);

      //set filesystem provider as default one
      setStorageProviderComboValue("filesystem");
  }


  // ------------------------------------------------------------ Public methods
  /**
   * Create the panel
   */

  private void init() {
    // set layout
    this.setLayout(null);

    // set properties of label, position and border
    //  referred to the title of the panel
    titledBorder1 = new TitledBorder("");

    panelName.setFont(titlePanelFont);
    panelName.setText("Edit File Data Object SyncSource");
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
    
    storageProviderLabel.setText("Storage type:");
    storageProviderLabel.setFont(defaultFont);
    storageProviderLabel.setBounds(new Rectangle(14, y, 150, 18));
    //list of supported storage providers
    String[] providers = new String[]{PROVIDER_FILESYSTEM, PROVIDER_S3};
    storageProviderValue = new JComboBox(providers);
    storageProviderValue.addActionListener(jcloudsProviderValueActionListener);
    storageProviderValue.setFont(defaultFont);
    storageProviderValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;

//    storageContainerNameLabel.setText("Provider container name:");
    storageContainerNameLabel.setFont(defaultFont);
    storageContainerNameLabel.setBounds(new Rectangle(14, y, 150, 18));
    storageContainerNameValue.setFont(defaultFont);
    storageContainerNameValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;

//    storageIdentityLabel.setText("Provider identity:");
    storageIdentityLabel.setFont(defaultFont);
    storageIdentityLabel.setBounds(new Rectangle(14, y, 150, 18));
    storageIdentityValue.setFont(defaultFont);
    storageIdentityValue.setBounds(new Rectangle(170, y, 350, 18));

    //storage provider identity and storageFilesystemRootPathLabel aren't
    //showed together, so leaving the y coordinate equals the panel has a better
    //aspect for different storage types
    //y += dy;

//    storageFilesystemRootPathLabel.setText("Local path for filesystem provider:");
    storageFilesystemRootPathLabel.setFont(defaultFont);
    storageFilesystemRootPathLabel.setBounds(new Rectangle(14, y, 150, 18));
    storageFilesystemRootPathValue.setFont(defaultFont);
    storageFilesystemRootPathValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;

//    storageCredentialLabel.setText("Provider credential:");
    storageCredentialLabel.setFont(defaultFont);
    storageCredentialLabel.setBounds(new Rectangle(14, y, 150, 18));
    storageCredentialValue.setFont(defaultFont);
    storageCredentialValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;

    localRootPathLabel.setText("Root local path for temp files: ");
    localRootPathLabel.setFont(defaultFont);
    localRootPathLabel.setBounds(new Rectangle(14, y, 150, 18));
    localRootPathValue.setFont(defaultFont);
    localRootPathValue.setBounds(new Rectangle(170, y, 350, 18));

    y += dy;

    quotaLabel.setText("Quota: ");
    quotaLabel.setFont(defaultFont);
    quotaLabel.setBounds(new Rectangle(14, y, 150, 18));
    model = new RolesTableModel();
    quotaTable = new JTable(model);
    quotaTable.setFont(defaultFont);
    quotaTable.setShowGrid(true);
    quotaTable.setAutoscrolls(true);
    quotaTable.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
    //add the table into a JScrollPanel, otherwise no columns headers and border
    //will be show
    JScrollPane scrollpane = new JScrollPane(quotaTable);
    scrollpane.setBounds(new Rectangle(170, y, 350, 110));
    quotaTable.setPreferredScrollableViewportSize(new Dimension(350, 110));

    y += dy + 75;
    notaLabel.setText("<html>Please enter the number followed by 'M' if it is 'MegaBytes', or 'G' if it is 'GigaBytes'<BR>Example: '100 M' stands for 100 MegaBytes, '2 G' stands for 2 GigaBytes</html>");
    notaLabel.setFont(defaultFont);
    notaLabel.setBounds(new Rectangle(170, y, 350, 54));

    //button separed from other controls, and the height of the table
    y += dy+36;
    
    confirmButton.setFont(defaultFont);
    confirmButton.setText("Add");
    confirmButton.setBounds(170, y, 70, 25);

    confirmButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event ) {
            try {
                validateValues();
                getValues();
                if (getState() == STATE_INSERT) {
                    FileDataObjectSyncSourceConfigPanel.this.actionPerformed(new ActionEvent(FileDataObjectSyncSourceConfigPanel.this, ACTION_EVENT_INSERT, event.getActionCommand()));
                } else {
                    FileDataObjectSyncSourceConfigPanel.this.actionPerformed(new ActionEvent(FileDataObjectSyncSourceConfigPanel.this, ACTION_EVENT_UPDATE, event.getActionCommand()));
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
    this.add(storageProviderLabel, null);
    this.add(storageProviderValue, null);
    this.add(storageContainerNameLabel, null);
    this.add(storageContainerNameValue, null);
    this.add(storageIdentityLabel, null);
    this.add(storageIdentityValue, null);
    this.add(storageCredentialLabel, null);
    this.add(storageCredentialValue, null);
    this.add(storageFilesystemRootPathLabel, null);
    this.add(storageFilesystemRootPathValue, null);
    this.add(localRootPathLabel, null);
    this.add(localRootPathValue, null);
    this.add(quotaLabel, null);
    this.add(scrollpane, null);
    this.add(notaLabel, null);
    this.add(confirmButton, null);
  }


  public void updateForm() {
      if (!(getSyncSource() instanceof FileDataObjectSyncSource)) {
          notifyError(
              new AdminException(
                  "This is not a FileDataObjectSyncSource! Unable to process SyncSource values."
              )
          );
          return;
      }

      FileDataObjectSyncSource syncSource = (FileDataObjectSyncSource)getSyncSource();

      if (getState() == STATE_INSERT) {
          confirmButton.setText("Add");
      } else if (getState() == STATE_UPDATE) {
          confirmButton.setText("Save");
      }

      localRootPathValue.setText(syncSource.getLocalRootPath());
      sourceUriValue.setText(syncSource.getSourceURI());
      nameValue.setText(syncSource.getName());
      setStorageProviderComboValue(syncSource.getStorageProvider());
      storageContainerNameValue.setText(syncSource.getStorageContainerName());
      storageIdentityValue.setText(syncSource.getStorageIdentity());
      storageCredentialValue.setText(syncSource.getStorageCredential());
      storageFilesystemRootPathValue.setText(syncSource.getStorageFilesystemRootPath());

      //set the table user quota data
      model.setData(syncSource.getRoleQuota());

      if (syncSource.getSourceURI() != null) {
          sourceUriValue.setEditable(false);
      }
  }

  /**
   * Set preferredSize of the panel
   * @return preferredSize of the panel
   */
    @Override
  public Dimension getPreferredSize() {
      return new Dimension(525, 530);
  }

  // ----------------------------------------------------------- Private methods


  /**
   * Manage labels and text values and visibility based on storage providers
   */
  private ActionListener jcloudsProviderValueActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox)e.getSource();
            String provider = (String)cb.getSelectedItem();

            //get the configuration for selected providers
            StorageProviderWrapper configs = storagesConfigurations.get(provider);
            //and configures elements
            for(StorageFieldConfiguration fieldConfig:configs.getFieldsConfiguration()) {
                fieldConfig.configure();
            }
        }
  };

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

      value = nameValue.getText().trim();
      if (StringUtils.isEmpty(value)) {
          throw new IllegalArgumentException("Field 'Name' cannot be empty. Please provide a SyncSource name.");
      }

      if (!StringUtils.containsOnly(value, NAME_ALLOWED_CHARS.toCharArray())) {
          throw new IllegalArgumentException("Only the following characters are allowed for field 'Name': \n" + NAME_ALLOWED_CHARS);
      }

      value = sourceUriValue.getText().trim();
      if (StringUtils.isEmpty(value)) {
          throw new IllegalArgumentException("Field 'Source URI' cannot be empty. Please provide a SyncSource URI.");
      }

      //checks value for storage provider
      StorageProviderWrapper currentStorageProvider = getStorageProviderWrapperForSelectedComboboxItem();
      String errors = currentStorageProvider.checkValidity();
      if (!StringUtils.isEmpty(errors)) {
          throw new IllegalArgumentException(errors + " cannot be empty. Please provide a valid value.");
      }

      value = localRootPathValue.getText().trim();
      if (StringUtils.isEmpty(value)) {
          throw new IllegalArgumentException("Field 'Root local path for temp files' cannot be empty. Please provide a SyncSource db directory.");
      }

      //checks role quota values
      for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex ++) {
          String quotaValueString = (String) model.getValueAt(rowIndex, 1);
          try {
            long quotaValue = StringTools.converterStringSizeInBytes(quotaValueString);
          } catch (NumberFormatException e) {
              String quotaDesc = (String) model.getValueAt(rowIndex, 0);
              throw new IllegalArgumentException("Quota value for role '" + quotaDesc + "' is wrong. Please provide a valid value.");
          }
      }
  }

  /**
   * Set syncSource properties with the values provided by the user.
   */
  private void getValues() {

      FileDataObjectSyncSource syncSource = (FileDataObjectSyncSource)getSyncSource();

      syncSource.setSourceURI(sourceUriValue.getText().trim());
      syncSource.setName(nameValue.getText().trim());
      syncSource.setMaxSize(DEFAULT_MAX_SIZE);
      syncSource.setLocalRootPath(localRootPathValue.getText().trim());

      //get the data from the jtable
      for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex ++) {
          String quotaDesc = (String) model.getValueAt(rowIndex, 0);
          String quotaValue = (String)model.getValueAt(rowIndex, 1);
          syncSource.getRoleQuota().put(quotaDesc, quotaValue);
      }

      //get current selected storage provider wrapper
      StorageProviderWrapper currentStorageProvider = getStorageProviderWrapperForSelectedComboboxItem();
      //assign the corresponding name for jclouds provider
      syncSource.setStorageProvider(currentStorageProvider.getJcloudsProviderName());
      //clean not visible values for selected storage provider
      currentStorageProvider.cleanNotVisibleFieldsValue();
      //and assign other storage provider parameters
      syncSource.setStorageContainerName(storageContainerNameValue.getText().trim());
      syncSource.setStorageIdentity(storageIdentityValue.getText().trim());
      syncSource.setStorageCredential(storageCredentialValue.getText().trim());
      syncSource.setStorageFilesystemRootPath(storageFilesystemRootPathValue.getText().trim());

      ContentType[] contentTypes= new ContentType[1];
      contentTypes[0] = new ContentType(FileDataObjectSyncSource.FDO_MIME_TYPE,
                                        "1.0"                                 );
      syncSource.setInfo(new SyncSourceInfo(contentTypes, 0));
    }

    /**
     * Based on jclouds provider value, set the combobox of storage providers
     * @param jcloudsProvidersType
     */
    private void setStorageProviderComboValue(String jcloudsProvidersType) {
        //collection is ordered, so the position of the element in the combo
        //matches the one in the collection
        //find the name corresponding to the jclouds provider type
        String providerDisplayName = null;
        for (Map.Entry<String, StorageProviderWrapper> entry : storagesConfigurations.entrySet()) {
           if (jcloudsProvidersType.equalsIgnoreCase(entry.getValue().getJcloudsProviderName())) {
               providerDisplayName = entry.getKey();
               break;
           };
        }
        storageProviderValue.setSelectedItem(providerDisplayName);
    }

    /**
     * Returns the {@link StorageProviderConfigurations} associated with the
     * selected item of the storage type Combobox
     * @return
     */
    private StorageProviderWrapper getStorageProviderWrapperForSelectedComboboxItem() {
        String providerDisplayName = (String) storageProviderValue.getSelectedItem();
        StorageProviderWrapper wrapper = storagesConfigurations.get(providerDisplayName);
        return wrapper;
    }


    // ----------------------------------------------------------- Inner classes


    /**
     * Model that is bound to the role table.
     */
    private class RolesTableModel extends AbstractTableModel {
            private final String[] columnNames = { "Role", "Quota"};
            //rows of dataVector have to be String[2]
            private String[][] dataVector = new String[0][columnNames.length];

            public int getRowCount() {
                return null != dataVector ? dataVector.length : 0;
            }

            public int getColumnCount() {
                return columnNames.length;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                if (null == dataVector) return null;
                return dataVector[rowIndex][columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (null == dataVector) return;
                dataVector[rowIndex][columnIndex] = (String) aValue;
                fireTableCellUpdated(rowIndex, columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                //first cell isn't editable
                return column > 0;
            }

            public void setData(Map<String, String> newData) {
                if (null == newData) {
                    dataVector = new String[0][columnNames.length];
                } else {
                    //transform map data with quotas in array data for jtable
                    dataVector = new String[newData.size()][2];
                    int rowIndex = 0;
                    for (Map.Entry<String, String> role : newData.entrySet()) {
                        dataVector[rowIndex][0] = role.getKey();
                        dataVector[rowIndex][1] = role.getValue();
                        rowIndex++;
                    }

                    //sort the array based on quota values, increasing order
                    for (int i = 0; i < dataVector.length - 1; i++) {
                        for(int j = i + 1; j < dataVector.length; j++) {
                            if (fromStringToLong(dataVector[i][1]) > fromStringToLong(dataVector[j][1])) {
                                //swap data (element 0 and element 1)
                                for (int index = 0; index < 2; index ++){
                                    String swap = dataVector[i][index];
                                    dataVector[i][index] = dataVector[j][index];
                                    dataVector[j][index] = swap;
                                }
                            }
                        }
                    }
                }
                fireTableDataChanged();
            }


            /**
             * Use this method for converting quota string values (with Mb, Gb, Kb) to long values
             * @param value
             * @return
             */
            private long fromStringToLong(String value){
                return StringTools.converterStringSizeInBytes(value);
            }
    }


    /**
     * Used to configure textboxes labels and visibility
     */
    private class StorageFieldConfiguration {
        /** JLabel associated to Textbox */
        private final JLabel associatedLabel;

        /** JTextField with parameter value */
        private final JTextField associatedTextField;

        /** Label value */
        public String getLabel()
        { return associatedLabelValue; }
        private final String associatedLabelValue;

        /** Returns the value of the associated Textbox */
        public String getValue()
        { return associatedTextField.getText(); }
        /** Set the value of the associated Textbox */
        public void setValue(String neValue)
        { associatedTextField.setText(neValue); }

        /** visibility of JLabel and JTextField */
        public boolean getVisible()
        { return visible; }
        private final boolean visible;

        /**
         *
         * @param associatedLabel JLabel associated with JTextField
         * @param associatedTextField the JTextField to set visibility
         * @param associatedLabelValue the value of the label
         * @param visible the visibility of the JLabel and of JTextField
         */
        public StorageFieldConfiguration(
                JLabel associatedLabel,
                JTextField associatedTextField,
                String associatedLabelValue,
                boolean visible)
        {
            this.associatedLabel = associatedLabel;
            this.associatedTextField = associatedTextField;
            this.associatedLabelValue = associatedLabelValue;
            this.visible = visible;
        }

        /**
         * Configure visibility and labels of associated controls
         */
        public void configure()
        {
            if (visible) {
                associatedLabel.setText(associatedLabelValue);
            }

            associatedLabel.setVisible(visible);
            associatedTextField.setVisible(visible);
        }
    }


    /**
     * Contains all the configuration needed for a storage provider
     */
    private class StorageProviderWrapper {
        private final String jcloudsName;
        public String getJcloudsProviderName()
        { return jcloudsName; }

        private final List<StorageFieldConfiguration> fieldsConfiguration;
        public List<StorageFieldConfiguration> getFieldsConfiguration()
        { return fieldsConfiguration; }

        /**
         *
         * @param jcloudsName the name of the jclouds provider
         * @param fieldsConfiguration a list of configurations to apply to panel
         *                            fields related to jclouds parameter value
         */
        public StorageProviderWrapper(
                String jcloudsName,
                List<StorageFieldConfiguration> fieldsConfiguration)
        {
            this.jcloudsName = jcloudsName;
            this.fieldsConfiguration = fieldsConfiguration;
        }

        /**
         * Returns an error message some fields have empty values, otherwise
         * return an empty string
         */
        public String checkValidity()  {
            boolean errorsDetected = false;
            StringBuilder fieldsInError = new StringBuilder();

            for (StorageFieldConfiguration fieldConfig : fieldsConfiguration) {
                if (fieldConfig.getVisible()) {
                    if (StringTools.isEmpty(fieldConfig.getValue())) {
                        fieldsInError.append("\"").append(fieldConfig.getLabel()).append("\"");
                        errorsDetected = true;
                    }
                }
            }

            if (!errorsDetected) return null;

            return fieldsInError.toString();
        }

        /**
         * Cleans all the values of non visible fields
         */
        public void cleanNotVisibleFieldsValue() {
            for (StorageFieldConfiguration fieldConfig : fieldsConfiguration) {
                if (!fieldConfig.getVisible()) {
                    fieldConfig.setValue("");
                }
            }
        }
    }

}
