package com.oxygenxml.resources.batch.converter.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.worker.ConverterWorker;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

/**
 * Converter dialog.
 * @author intern4
 *
 */
public class ConverterDialog extends OKCancelDialog implements BatchConverterInteractor{

	/**
	 * The input panel.
	 */
	private InputPanel inputPanel;
	
	/**
	 * The output panel.
	 */
	private	OutputPanel outputPanel;

	/**
	 * Converter worker.
	 */
	private ConverterWorker converterWorker;
	
	/**
	 * The parent frame.
	 */
	private JFrame parentFrame;

	/**
	 * Traslator.
	 */
	private Translator translator;

	/**
	 * The type of converter.
	 */
	private String converterType;

	
	/**
	 * Constructor.
	 * @param converterType The type of converter.
	 * @param toConvertFiles List with files to convert.
	 * @param parentFrame The parent frame.
	 * @param translator Translator.
	 */
	public ConverterDialog(String converterType, List<File> toConvertFiles, JFrame parentFrame, Translator translator) {
		super(parentFrame, "" , true);
		this.converterType = converterType;
		this.parentFrame = parentFrame;
		this.translator = translator;

		inputPanel = new InputPanel(converterType, translator, this);
		outputPanel = new OutputPanel(translator);
		
		initGUI(translator);
		
		//if the given list with files isn't empty.
		if(!toConvertFiles.isEmpty()){
			getOkButton().setEnabled(true);
			inputPanel.addFilesInTable(toConvertFiles);

			//set the output folder according to given files to be converted
			setOutputFolder(toConvertFiles.get(toConvertFiles.size()-1).getParent().toString() + File.separator + "output");
		}
		else{
			getOkButton().setEnabled(false);
		}
		
		setTitle(translator.getTranslation(Tags.DIALOG_TITLE, converterType));
		setOkButtonText(translator.getTranslation(Tags.CONVERT_BUTTON, ""));
		setResizable(true);
		setMinimumSize(new Dimension(350, 300));
		setSize(new Dimension(420, 350));
		setLocationRelativeTo(parentFrame);
		setVisible(true);
	}

	/**
	 * Initialize the GUI.
	 * 
	 * @param translator
	 */
	private void initGUI(Translator translator){
		
		JPanel convertorPanel = new JPanel( new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		//-----Add the input panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 20, 0);
		convertorPanel.add(inputPanel, gbc);
	
		//-----Add the output panel
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 10, 0);
		convertorPanel.add(outputPanel, gbc);
		
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
			PluginWorkspaceProvider.getPluginWorkspace().showWarningMessage(translator.getTranslation(Tags.EMPTY_OUTPUT_MESSAGE,""));
		} else {

			//create a progress dialog
			final ProgressDialog progressDialog = new ProgressDialog(parentFrame, translator, converterType);

			//create a converter worker.
			converterWorker = new ConverterWorker(converterType, this, progressDialog);

			//add a action listener on cancel button for progress dialog
			progressDialog.addCancelActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					converterWorker.cancel(true);
					progressDialog.dispose();
				}
			});

			//start the worker.
			converterWorker.execute();

			super.doOK();
		}
	}


	@Override
	public List<File> getInputFiles() {
		return inputPanel.getFilesFromTable();
	}


	@Override
	public File getOutputFolder() {
		return new File(outputPanel.getOutputPath());
	}


	@Override
	public void setOutputFolder(String text) {
		outputPanel.setOutputPath(text);
	}


	@Override
	public void setEnableConvert(boolean state) {
		getOkButton().setEnabled(state);
	}

	
}
