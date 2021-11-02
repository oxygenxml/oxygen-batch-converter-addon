package com.oxygenxml.resources.batch.converter.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.persister.ContentPersister;
import com.oxygenxml.resources.batch.converter.persister.ContentPersisterImpl;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

/**
 * Converter dialog.
 * @author Cosmin Duna
 *
 */
public class ConverterDialog extends OKCancelDialog implements BatchConverterInteractor{ //NOSONAR parent

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The input panel.
	 */
	private InputPanel inputPanel;
	
	/**
	 * The output panel.
	 */
	private	OutputPanel outputPanel;

	/**
	 * CheckBox for select to open converted files after conversion.
	 */
	private JCheckBox openFilesCBox;
	
	/**
	 * Translator.
	 */
	private transient Translator translator;

	/**
	 * The type of converter.
	 */
	private String converterType;
	
	/**
	 * Used for persistence.
	 */
	private transient ContentPersister contentPersister;

	/**
	 * Link to GitHub repository description.
	 */
	private static final String LINK_TO_GIT_HUB = "https://github.com/oxygenxml/oxygen-resources-convertor";
	
	/**
	 * The inset used between components
	 */
	private static final int INSET_BETWEEN_COMPONENTS = 6;
	
	/**
	 * The additional 
	 */
	private Map<String, JCheckBox> additionalOptions = new HashMap<String, JCheckBox>();

	/**
	 * The parent frame
	 */
  private JFrame parentFrame;

  /**
   * Constructor
   * 
   * @param converterType The type of converter.
   * @param toConvertFiles List with files to convert.
   * @param outputDir The ouput directory.
   * @param parentFrame The parent frame.
   * @param translator Translator.
   */
	public ConverterDialog(String converterType, List<File> toConvertFiles, File outputDir, JFrame parentFrame, Translator translator) {
		super(parentFrame, "" , true);
		this.converterType = converterType;
    this.parentFrame = parentFrame;
		this.translator = translator;
		contentPersister = new ContentPersisterImpl();
		
		inputPanel = new InputPanel(converterType, translator, this);
		outputPanel = new OutputPanel(translator);
		openFilesCBox = new JCheckBox(translator.getTranslation(Tags.OPEN_FILE_CHECK_BOX));
		
		initGUI();
		
		//  Load saved state of the dialog
		contentPersister.loadState(this);
		getOkButton().setEnabled(!toConvertFiles.isEmpty());
		
		setTitle(translator.getTranslation(Tags.MENU_ITEM_TEXT + converterType));
		setOkButtonText(translator.getTranslation(Tags.CONVERT_BUTTON));
		
		if(outputDir != null) {
		  setOutputFolder(outputDir.getAbsolutePath());
		} else if (!toConvertFiles.isEmpty()){
			// Set the input files and the output folder
			inputPanel.addFilesInTable(toConvertFiles);
			setOutputFolder(toConvertFiles.get(toConvertFiles.size()-1).getParent() + File.separator + "output");
		}
	}

	/**
	 * Show the converted dialog.
	 */
	public void showDialog() {
	  setResizable(true);
    pack();
    setMinimumSize(new Dimension(getSize().width , getSize().height));
    setLocationRelativeTo(parentFrame);
    setVisible(true);
	}
	
	/**
	 * Initialize the GUI.
	 * 
	 */
	private void initGUI(){
		JPanel convertorPanel = new JPanel( new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		//-----Add the input panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		Insets betweenComponentsInsets = new Insets(0, 0, INSET_BETWEEN_COMPONENTS, 0);
        gbc.insets = betweenComponentsInsets;
		convertorPanel.add(inputPanel, gbc);
	
		//-----Add the output panel
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		convertorPanel.add(outputPanel, gbc);
		
		//----Add the checkBox for select to open converted files after conversion 
		gbc.gridy++;
		convertorPanel.add(openFilesCBox, gbc);
		
		List<String> imposedAdditionalOptions = ConverterAdditionalOptionsProvider.getImposedAdditionalOptions(converterType);
		for (String imposedOption : imposedAdditionalOptions) {
		  if(imposedOption.equals(ConverterAdditionalOptionsProvider.ADDITIONAL_OPTIONS_SEPARATOR)) {
		    gbc.insets = new Insets(INSET_BETWEEN_COMPONENTS * 2, 0, INSET_BETWEEN_COMPONENTS, 0);
		  } else {
		    gbc.gridy++;
		    JCheckBox optionCombo = new JCheckBox (ConverterAdditionalOptionsProvider.getTranslationMessageFor(imposedOption));
	      additionalOptions.put(imposedOption, optionCombo);
	      convertorPanel.add(optionCombo, gbc);
		    gbc.insets = betweenComponentsInsets;
		  }
    }
		
		this.add(convertorPanel);
	}
	
	/**
	 * Convert pressed.
	 */
	@Override
	protected void doOK() {
		if (outputPanel.getOutputPath().isEmpty()) {
			//output panel is empty.
			//show a warning message.
			PluginWorkspaceProvider.getPluginWorkspace().showWarningMessage(translator.getTranslation(Tags.EMPTY_OUTPUT_MESSAGE));
		} else {
			// Save the state of dialog.
			contentPersister.saveState(this);
			super.doOK();
		}
	}

	@Override
	public List<File> getInputFiles() {
		return inputPanel.getFilesFromTable();
	}


	@Override
	public File getOutputFolder() {
	  // This is a directory used to add output files.
		return new File(outputPanel.getOutputPath()); // NOSONAR cwe, owasp-a4, wasc
	}

	@Override
	public String getOutputFolderPath() {
	  return outputPanel.getOutputPath();
	}

	@Override
	public void setOutputFolder(String text) {
		outputPanel.setOutputPath(text);
	}


	@Override
	public void setEnableConvert(boolean state) {
		getOkButton().setEnabled(state);
	}

	@Override
	public String getHelpPageID() {
		return LINK_TO_GIT_HUB;
	}

	@Override
	public boolean mustOpenConvertedFiles() {
		return openFilesCBox.isSelected();
	}
	
  @Override
  public void setOpenConvertedFiles(boolean state) {
    openFilesCBox.setSelected(state);
  }

	@Override
	public Boolean getAdditionalOptionValue(String additionalOptionId) {
	  Boolean toRet = null;
	  JCheckBox optionCombo = additionalOptions.get(additionalOptionId);
	  if (optionCombo != null) {
	    toRet = optionCombo.isSelected();
	  }
	  return toRet;
	}
	
	@Override
	public Set<String> getAdditionalOptions() {
	  return additionalOptions.keySet();
	}
	
	@Override
	public void setAdditionalOptionValue(String additionalOptionId, boolean state) {
	  JCheckBox optionCombo = additionalOptions.get(additionalOptionId);
    if (optionCombo != null) {
     optionCombo.setSelected(state);
    }
	}
}
