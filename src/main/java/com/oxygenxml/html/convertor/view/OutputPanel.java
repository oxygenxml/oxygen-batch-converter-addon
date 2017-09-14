package com.oxygenxml.html.convertor.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.oxygenxml.html.convertor.FileType;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

/**
 * Panel for add output folder.
 * @author intern4
 *
 */
public class OutputPanel extends JPanel{

	/**
	 * Radio button for select xhtml output files
	 */
	private JRadioButton xhtmlRBtn;
	/**
	 * Radio button for select dita output files
	 */
	private JRadioButton ditaRBtn;
	
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
	private static final String CHOOSER_ICON = "images/chooser.png";
	
	/**
	 * Translator.
	 */
	private Translator translator; 

	public OutputPanel(Translator translator) {
		this.translator = translator;  

		xhtmlRBtn = new JRadioButton(FileType.XHTML_TYPE);
		ditaRBtn = new JRadioButton(FileType.DITA_TYPE);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
	
		// Create a group with the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(xhtmlRBtn);
		group.add(ditaRBtn);
	
		// ------add JLabel for select input type
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		this.add(new JLabel(translator.getTranslation(Tags.SELECT_OUTPUT_TYPE_LABEL)), gbc);

		// create a panel that contains html and markdown radio buttons
		JPanel rBtnsPanel = new JPanel();
		rBtnsPanel.setLayout(new GridLayout(1, 2, 5, 0));
		rBtnsPanel.setOpaque(false);

		rBtnsPanel.add(xhtmlRBtn);
		rBtnsPanel.add(ditaRBtn);

		// ------add html and markdown radio buttons
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 10, 0, 0);
		this.add(rBtnsPanel, gbc);

		// ------------add JLabel for add output folder
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(new JLabel(translator.getTranslation(Tags.ADD_OUTPUT_FOLDER_LABEL)), gbc);

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
	 * Get the output type.
	 * @return The output type
	 */
	public String getOutputType(){
		if(xhtmlRBtn.isSelected()){
			return FileType.XHTML_TYPE;
		}
		else{
			return FileType.DITA_TYPE;
		}
	}
	
	/**
	 * Set the output type.
	 * @param type The output type
	 */
	public void setOutputType(String type){
		if(FileType.XHTML_TYPE.equals(type)){
			xhtmlRBtn.setSelected(true);
		}
		else{
			ditaRBtn.setSelected(true);
		}
	}

	
	
	//TODO add javaDoc(here and at constructor) or delete. 
	public String getOutputPath(){
		return outputField.getText();
	}
	
}
