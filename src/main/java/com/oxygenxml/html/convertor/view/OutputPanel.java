package com.oxygenxml.html.convertor.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

public class OutputPanel extends JPanel{

	JTextField outputField = new JTextField();
	
	Action chooserAction = new AbstractAction() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	};
	
	ToolbarButton chooserBtn = new ToolbarButton(chooserAction, false);
	
	public OutputPanel(Translator translator) {
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
	
		
		// ------------add JLabel for add output file
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		add(new JLabel(translator.getTranslation(Tags.ADD_OUTPUT_FILE_LABEL_KEY)), gbc);
	
		gbc.gridy++; 
		gbc.insets = new Insets(0, 10, 0, 0);
		add(outputField, gbc);
		
		gbc.gridx ++;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		
	// Get the image for toolbar button
		URL imageToLoad = getClass().getClassLoader().getResource("images/chooser.png");
		if (imageToLoad != null) {
			chooserBtn.setText("");
			chooserBtn.setIcon(ro.sync.ui.Icons.getIcon(imageToLoad.toString()));
		}
		
		add(chooserBtn,gbc);
	}
}
