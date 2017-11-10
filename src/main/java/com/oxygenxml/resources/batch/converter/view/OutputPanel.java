package com.oxygenxml.resources.batch.converter.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

/**
 * Panel for add output folder.
 * @author Cosmin Duna
 *
 */
public class OutputPanel extends JPanel{

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Filed that contains the output folder.
	 */
	private	JTextField outputField = new JTextField();
	
	/**
	 * Button for open a directory chooser.
	 */
	private ToolbarButton chooserBtn;

	/**
	 * Path to icon for button that open the directory chooser.
	 */
	private static final String CHOOSER_ICON = "images/Open16.png";
	

	public OutputPanel(Translator translator) {

		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
	
		// ------------add JLabel for add output folder
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(new JLabel(translator.getTranslation(Tags.ADD_OUTPUT_FOLDER_LABEL, "")), gbc);

		// -------------add output field
		gbc.gridy++;
		gbc.insets = new Insets(5, 15, 0, 0);
		add(outputField, gbc);

		// -------------add directoryChooser button
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		gbc.fill = GridBagConstraints.NONE;

		//action for chooserBtn
		Action chooserAction = new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
					//open a directory chooser
					File file = PluginWorkspaceProvider.getPluginWorkspace().chooseDirectory();

					if(file != null){
						//set the selected folder in the outputField
						outputField.setText(file.getPath());
					}
			}
		};
		
		chooserBtn = new ToolbarButton(chooserAction, false);
		
		// Get the image for chooserBtn
		URL imageToLoad = getClass().getClassLoader().getResource(CHOOSER_ICON);
		
		if (imageToLoad != null) {
			//set the icon
			chooserBtn.setText("");
			chooserBtn.setIcon(ro.sync.ui.Icons.getIcon(imageToLoad.toString()));
		}
		
		//add the button in panel.
		add(chooserBtn,gbc);
		
	}
	
	
	/**
	 * Get the output path from outputField
	 * @return The output path.
	 */
	public String getOutputPath(){
		return outputField.getText();
	}
	
	/**
	 * Set the output path with given path.
	 * @param text The output path to set.
	 */
	public void setOutputPath(String path){
		outputField.setText(path);
	}
	
}
