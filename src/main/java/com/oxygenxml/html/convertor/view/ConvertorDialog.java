package com.oxygenxml.html.convertor.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.oxygenxml.html.convertor.persister.ContentPersister;
import com.oxygenxml.html.convertor.persister.ContentPersisterImpl;
import com.oxygenxml.html.convertor.translator.OxygenTranslator;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;
import com.oxygenxml.html.convertor.worker.ConvertorWorker;

import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

public class ConvertorDialog extends OKCancelDialog implements ConvertorInteractor {

	private Translator translator = new OxygenTranslator();
	
	private InputPanel inputPanel = new InputPanel(translator);
	
	private	OutputPanel outputPanel = new OutputPanel(translator);
	
	private ContentPersister contentPersister = new ContentPersisterImpl();

	private String currentFilePath;
	
	public ConvertorDialog(Frame parentFrame, String currentFilePath) {
		super(parentFrame, "" , true);
		this.currentFilePath = currentFilePath;

		
		initGUI();
		
		contentPersister.loadState(this);
		
		
		setTitle(translator.getTranslation(Tags.DIALOG_TITLE));
		setResizable(true);
		setMinimumSize(new Dimension(300, 260));
		setSize(new Dimension(400, 300));
		setLocationRelativeTo(parentFrame);
		setVisible(true);
	}


	private void initGUI(){
		
		JPanel convertorPanel = new JPanel( new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		convertorPanel.add(inputPanel, gbc);
	
		
		gbc.gridy++;
		gbc.weighty = 0.5;
		convertorPanel.add(outputPanel, gbc);
		
		this.add(convertorPanel);
	}
	
	@Override
	protected void doOK() {
		List<String> filesPaths = new ArrayList<String>();
		
		if(inputPanel.isSelectedCheckCurrent()){
			filesPaths.add(currentFilePath);
		}
		else{
			filesPaths.addAll(inputPanel.getTableUrls());
		}
		ConvertorWorker convertorWorker = new ConvertorWorker(filesPaths, outputPanel.getOutputPath());
		convertorWorker.execute();
		
		contentPersister.saveState(this);
		
		super.doOK();
	}

	//--- implementation of ConvertorInteractor
	@Override
	public boolean isConvertCurrentFile() {
		return inputPanel.isSelectedCheckCurrent();
	}


	@Override
	public void setConvertCurrentFile(boolean state) {
		inputPanel.setSelectedCheckCurrent(state);
	}


	@Override
	public void setOtherFilesToConvert(List<String> otherFiles) {
		inputPanel.addRowsInTable(otherFiles);
	}


	@Override
	public List<String> getOtherFilesToConvert() {
		return inputPanel.getTableUrls();
	}


	@Override
	public void setOutputDirectory(String outputDir) {
		outputPanel.setOutputPath(outputDir);
		
	}


	@Override
	public String getOutputDirectory() {
		return outputPanel.getOutputPath();
	}
}
