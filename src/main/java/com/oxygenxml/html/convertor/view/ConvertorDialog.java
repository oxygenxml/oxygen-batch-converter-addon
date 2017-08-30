package com.oxygenxml.html.convertor.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.oxygenxml.html.convertor.translator.OxygenTranslator;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

public class ConvertorDialog extends OKCancelDialog {

	Translator translator = new OxygenTranslator();
	
	InputPanel inputPanel = new InputPanel(translator);
	
	OutputPanel outputPanel = new OutputPanel(translator);
	
	public ConvertorDialog(Frame parentFrame) {
		super(parentFrame, "" , true);

		initGUI();
		
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
}
