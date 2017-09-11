package com.oxygenxml.html.convertor.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.oxygenxml.html.convertor.ConvertorInteractor;
import com.oxygenxml.html.convertor.persister.ContentPersister;
import com.oxygenxml.html.convertor.persister.ContentPersisterImpl;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

public class ConvertorDialog extends OKCancelDialog implements ConvertorInteractor{

	private InputPanel inputPanel;
	
	private	OutputPanel outputPanel;

	private ContentPersister contentPersister = new ContentPersisterImpl();

	
	public ConvertorDialog(Frame parentFrame, Translator translator) {
		super(parentFrame, "" , true);

		inputPanel = new InputPanel(translator, this.getOkButton());
		outputPanel = new OutputPanel(translator);
		
		initGUI(translator);
		
		contentPersister.loadState(this);
		
		
		setTitle(translator.getTranslation(Tags.DIALOG_TITLE));
		setOkButtonText(translator.getTranslation(Tags.CONVERT_BUTTON));
		setResizable(true);
		setMinimumSize(new Dimension(350, 400));
		setSize(new Dimension(420, 450));
		setLocationRelativeTo(parentFrame);
		setVisible(true);
	}


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
		List<String> filesPaths = new ArrayList<String>();
		
		contentPersister.saveState(this);

		//ConvertorWorker convertorWorker = new ConvertorWorker(inputPanel.getPath(), outputPanel.getPath());
		//convertorWorker.execute();
		
		
		super.doOK();
	}



	@Override
	public String getOutputType() {
		return outputPanel.getOutputType();
	}


	@Override
	public void setOutputType(String type) {
		outputPanel.setOutputType(type);
	}

}
