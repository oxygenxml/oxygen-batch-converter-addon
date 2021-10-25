package com.oxygenxml.resources.batch.converter.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.oxygenxml.batch.converter.core.extensions.FileExtensionType;
import com.oxygenxml.batch.converter.core.word.styles.WordStyleMapLoader;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
import com.oxygenxml.resources.batch.converter.resources.Images;
import com.oxygenxml.resources.batch.converter.translator.Tags;

import ro.sync.basic.util.URLUtil;
import ro.sync.exml.workspace.api.PluginResourceBundle;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Button;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;
import ro.sync.ui.application.ApplicationTable;

/**
 * Option panel for Batc Documents Converter
 * 
 * @author cosmin_duna
 */
public class BatchConverterOptionPanel extends JPanel{

  /**
   * Logger
   */
  private static final Logger logger = Logger.getLogger(BatchConverterOptionPanel.class);
  
  /**
   * The link to documentation for word conversion configuration
   */
  private static final String WORD_CONFIG_DOCUMENTATION = "https://www.oxygenxml.com/doc/ug-editor/topics/batch-converter-addon.html";
  
  /**
   * Inset used between buttons
   */
  private static final int INSET_BETWEEN_BUTTONS = 3;
  
  /**
   * The table that contains word styles mapping
   */
  private final ApplicationTable wordMappingtable;
  
  /**
   * The model of the table that contains word styles mapping
   */
  private final DefaultTableModel wordMappingtableModel;
  
  /**
   * The new button.
   */
  private Button newRowButton;
  
  /**
   * The edit button.
   */
  private Button editRowButton;

  /**
   * The delete button.
   */
  private Button deleteRowButton;
  
  /**
   * The export button.
   */
  private Button exportButton;
  
  /**
   * The import button.
   */
  private Button importButton;
  
  /**
   * Constructor
   */
  public BatchConverterOptionPanel() {
    super(new GridBagLayout());
    StandalonePluginWorkspace pluginWorkspace = (StandalonePluginWorkspace) PluginWorkspaceProvider
        .getPluginWorkspace();
    PluginResourceBundle messages = pluginWorkspace.getResourceBundle();

    wordMappingtable = new ApplicationTable() {
      @Override
      public String getToolTipText(MouseEvent event) {
        String toolTipText = super.getToolTipText(event);
        Point point = event.getPoint();
        int row = rowAtPoint(point);
        int column = columnAtPoint(point);
        if(row != -1 && column != -1) {
          String value = (String)wordMappingtableModel.getValueAt(row, column);
          if (value != null && !value.isEmpty()) {
            toolTipText = value;
          }
        }
        return toolTipText;
      }
    };
    
    wordMappingtableModel = new DefaultTableModel(0, 3) {
      public String getColumnName(int column) {
        String name; 
        switch (column) {
        case 0 :
          name = messages.getMessage(Tags.WORD_ELEMENT);
          break;

        case 1 :
          name = messages.getMessage(Tags.WORD_STYLE);
          break;

        case 2 :
          name = messages.getMessage(Tags.HTML_ELEMENTS);
          break;

        default:
          name = "";
          break;
        }
        return name;
      }
    };
    
    wordMappingtable.setModel(wordMappingtableModel);
    wordMappingtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // Edit on enter
    wordMappingtable.setDefaultAction(
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            editRow();
          }
        },
        // On double click
        true,
        // On enter
        true
        );
    
    TableColumn column = wordMappingtable.getColumnModel().getColumn(2);
    column.setMinWidth(200);
    column.setPreferredWidth(200);
    
    // Add the table in a scroll pane.
    JScrollPane scPane = new JScrollPane(wordMappingtable);
    scPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
   
    
    GridBagConstraints constr = new GridBagConstraints();
    constr.gridx = 0;
    constr.gridy = 0;
    constr.gridwidth = 2;
    constr.fill = GridBagConstraints.HORIZONTAL;
    constr.weightx = 1;
    add(new SectionPane(messages.getMessage(Tags.WORD_STYLES_MAPPING)), constr);
    
    
    constr.gridy++;
    constr.insets = new Insets(0, 1, 7, 1);
    constr.gridwidth = 1;
    add(new MultilineLabel(messages.getMessage(Tags.WORD_CONFIG_DESCRIPTION)), constr);
    
    constr.gridx++;
    constr.weightx = 0;
    ToolbarButton buttonToProfiling = new ToolbarButton(new AbstractAction(messages.getMessage(Tags.HELP)) {
      @Override
      public void actionPerformed(ActionEvent e) {
        pluginWorkspace.openInExternalApplication(URLUtil.convertToURL(WORD_CONFIG_DOCUMENTATION), true);
      }}, true);
    
    // Get the icon for button
    URL imageToLoad = getClass().getClassLoader().getResource(Images.SHOW_HELP_ICON);
    if (imageToLoad != null) {
      buttonToProfiling.setIcon(ro.sync.ui.Icons.getIcon(imageToLoad.toString()));
      buttonToProfiling.setText("");
    }
    add(buttonToProfiling, constr);
    
    constr.gridx = 0;
    constr.gridy++;
    constr.fill = GridBagConstraints.BOTH;
    constr.gridwidth = 2;
    constr.weightx = 1;
    constr.weighty = 1;
    constr.insets = new Insets(0, 1, 0, 1);
    add(scPane, constr);
    
    
    JPanel buttonsPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(buttonsPanel, BoxLayout.X_AXIS);
    buttonsPanel.setLayout(boxLayout);
    exportButton = new Button(new AbstractAction(messages.getMessage(Tags.EXPORT)) {
      @Override
      public void actionPerformed(ActionEvent e) {
        File outputFile = PluginWorkspaceProvider.getPluginWorkspace().chooseFile(null, 
            new String[] { FileExtensionType.XML_OUTPUT_EXTENSION },
            "", true);
        WordStylesConfigUtil.exportWordStylesMapConfiguration(wordMappingtableModel, outputFile);
      }
    });
    buttonsPanel.add(exportButton);
    buttonsPanel.add(Box.createRigidArea(new Dimension(INSET_BETWEEN_BUTTONS, 0)));

    importButton = new Button(new AbstractAction(messages.getMessage(Tags.IMPORT)) {
      @Override
      public void actionPerformed(ActionEvent e) {
        File inputFile = PluginWorkspaceProvider.getPluginWorkspace().chooseFile(null, 
            new String[] { FileExtensionType.XML_OUTPUT_EXTENSION },
            "", false);
        WordStylesConfigUtil.importWordStylesMapConfiguration(wordMappingtableModel, inputFile);
      }
    });
    buttonsPanel.add(importButton);
    
    buttonsPanel.add(Box.createHorizontalGlue());
    
    newRowButton = new Button(new AbstractAction(messages.getMessage(Tags.NEW)) {
      @Override
      public void actionPerformed(ActionEvent e) {
        newRow();
      }
    });
    buttonsPanel.add(newRowButton);
    buttonsPanel.add(Box.createRigidArea(new Dimension(INSET_BETWEEN_BUTTONS, 0)));
    
    editRowButton = new Button(new AbstractAction(messages.getMessage(Tags.EDIT)) {
      @Override
      public void actionPerformed(ActionEvent e) {
        editRow();
      }
    });
    buttonsPanel.add(editRowButton);
    buttonsPanel.add(Box.createRigidArea(new Dimension(INSET_BETWEEN_BUTTONS, 0)));
    
    deleteRowButton = new Button(new AbstractAction(messages.getMessage(Tags.DELETE)) {
      @Override
      public void actionPerformed(ActionEvent e) {
        deleteRows();
      }
    });
    buttonsPanel.add(deleteRowButton);
    
    constr.gridy++;
    constr.fill = GridBagConstraints.HORIZONTAL;
    constr.weightx = 1;
    constr.weighty = 0;
    constr.anchor = GridBagConstraints.EAST;
    constr.insets = new Insets(2, 2, 2, 2);
    add(buttonsPanel, constr);

    loadPageState();
  }
  
  
  /**
   * Edit the selected row from the table.
   */
  protected void editRow() {
    int selectedTableRow = wordMappingtable.getSelectedRow();
    if (selectedTableRow != -1) {
      // Check for the in place editing.
      int selectedColumnIndex = wordMappingtable.getSelectedColumn();
      wordMappingtable.editCellAt(selectedTableRow, selectedColumnIndex);

      wordMappingtable.requestFocus();
    }
  }
  
  /**
   * Add a new row in the table.
   */
  private void newRow() {
      // Stop editing before new
      TableCellEditor cellEditor = wordMappingtable.getCellEditor();
      if (cellEditor != null) {
        cellEditor.stopCellEditing();
      }
    
    wordMappingtableModel.addRow(new String [] {"", "", ""});
    int addedRow = wordMappingtableModel.getRowCount() - 1;
    if (addedRow >= 0 && addedRow < wordMappingtable.getModel().getRowCount()) {
      wordMappingtable.getSelectionModel().setSelectionInterval(addedRow, addedRow);
      wordMappingtable.invalidate();
      wordMappingtable.scrollRectToVisible(wordMappingtable.getCellRect(addedRow, 0, true));

      wordMappingtable.editCellAt(addedRow, 0);
      wordMappingtable.requestFocus();
    }
  }
  
  /**
   * Deletes the selected rows from the table.
   */
  protected void deleteRows() {
    int[] selectedRows = wordMappingtable.getSelectedRows();
    int rowToSelect = -1;
    for (int i = selectedRows.length - 1; i >=0 ; i--) {
      if (selectedRows[i] != -1) {
          // Cancel editing before deleting
          TableCellEditor cellEditor = wordMappingtable.getCellEditor();
          if (cellEditor != null) {
            cellEditor.cancelCellEditing();
          }
        
        wordMappingtableModel.removeRow(selectedRows[i]);
        if (i == 0) {
          // Try to select the row after the first selected row
          rowToSelect = selectedRows[0];   
        }
      }
    }
    
    if (rowToSelect > wordMappingtable.getRowCount() - 1) {
      // Select the last row.
      rowToSelect --;
    }
    
    // Select row
    wordMappingtable.getSelectionModel().setSelectionInterval(rowToSelect, rowToSelect);
    wordMappingtable.invalidate();
    wordMappingtable.scrollRectToVisible(wordMappingtable.getCellRect(rowToSelect, 0, true));
  }
  
  /**
   * Save the state of the options in WSOptionStorage.
   */
  public void savePageState() {
    WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
    if (optionsStorage != null) {
      optionsStorage.setOption(OptionTags.WORD_STYLES_MAP_CONFIG, WordStylesConfigUtil.serializeWordStylesMapping(wordMappingtableModel));
    }
  }

  /**
   * Load the state of the options from WSOptionStorage.
   */
  private void loadPageState() {
    WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
    if (optionsStorage != null) {
      String loaded = optionsStorage.getOption(OptionTags.WORD_STYLES_MAP_CONFIG, "");
      if(loaded.isEmpty()) {
        try {
          loaded = WordStyleMapLoader.readStyleMap(getClass().getClassLoader());
        } catch (JAXBException e) {
          logger.debug(e.getMessage(), e);
        }
      }
      WordStylesConfigUtil.setWordStylesMappingInTable(loaded, wordMappingtableModel);
    }
  }
  
  /**
   * Restore the default state of the options.
   */
  public void restoreDefault() {
    String defaultStylesMapping = "";
    try {
      defaultStylesMapping = WordStyleMapLoader.readStyleMap(getClass().getClassLoader());
    } catch (JAXBException e) {
      logger.debug(e.getMessage(), e);
    }
    WordStylesConfigUtil.setWordStylesMappingInTable(defaultStylesMapping, wordMappingtableModel);
  }
}
