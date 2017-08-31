package com.oxygenxml.html.convertor.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;


public class InputPanel extends JPanel {

	JRadioButton checkCurrent = new JRadioButton();
	JRadioButton checkOtherFiles = new JRadioButton();
	JTable inputTable = new JTable();
	DefaultTableModel inputTableModel ;

	JButton addBtn = new JButton();
	JButton rmvBtn = new JButton();
	
	public InputPanel(final Translator translator) {

		JScrollPane scrollPane = new JScrollPane(inputTable);

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(checkCurrent);
		buttonGroup.add(checkOtherFiles);
		
		
		//------------add JLabel for select file
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		add(new JLabel(translator.getTranslation(Tags.SELECT_FILES_LABEL_KEY)), gbc);

		//------------add checkCurrent radio button
		gbc.gridy++;
		gbc.insets = new Insets(0, 10, 0, 0);
		checkCurrent.setText(translator.getTranslation(Tags.CHECK_CURRENT_FILE_KEY));
		add(checkCurrent, gbc);

		//------------add checkOtherFiles radio button
		gbc.gridy++;
		checkOtherFiles.setText(translator.getTranslation(Tags.CHECK_OTHER_FILES_KEY));
		add(checkOtherFiles, gbc);

		
		inputTableModel =  new DefaultTableModel(new String[]{translator.getTranslation(Tags.INPUT_TABLE_HEAD)}, 0);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		inputTable.setPreferredScrollableViewportSize(new Dimension(scrollPane.getWidth(), scrollPane.getHeight()));
		inputTable.setModel(inputTableModel);

		//add list selection listener on table
		inputTable.getSelectionModel().addListSelectionListener(listSelectionListener);
		
		scrollPane.setOpaque(false);

		//------------add scrollPane
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 25, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		add(scrollPane, gbc);

		//------------add addBtn and removeBtn
		gbc.gridy ++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		
		// panel that contains add and remove buttons
		JPanel btnsPanel = new JPanel();
		btnsPanel.setLayout(new GridLayout(1, 2));

		btnsPanel.add(addBtn);
		addBtn.setEnabled(false);
		// addBtn.addActionListener(createAddActionListener());
		addBtn.setText(translator.getTranslation(Tags.ADD_TABLE));

		btnsPanel.add(rmvBtn);
		rmvBtn.setEnabled(false);
		// rmvBtn.addActionListener(createRemoveActionListener());
		rmvBtn.setText(translator.getTranslation(Tags.REMOVE_TABLE));
		btnsPanel.setOpaque(false);
		
		// add btnsPanel
		add(btnsPanel, gbc);

		
		
	// add action listener on check current radio button
			checkCurrent.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					//checkButton.setEnabled(true);
					inputTable.clearSelection();
					addBtn.setEnabled(false);
					rmvBtn.setEnabled(false);
				}
			});
			
			
			// add action listener on check other files radio button
			checkOtherFiles.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					if (getTableUrls().isEmpty()) {
						// disable the check button if is not other filesToCheck
					//	checkButton.setEnabled(false);
					}

					addBtn.setEnabled(true);
				}
			});
			
		
		// add action listener on add button
		addBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//open a URL chooser
				String file = PluginWorkspaceProvider.getPluginWorkspace()
						.chooseURLPath(translator.getTranslation(Tags.FILE_CHOOSER_TITLE), new String[] { "html" }, "html files", "");

				if(file != null){
					if (!tableContains(file)) {
						inputTableModel.addRow(new String[] { file });
					}
				}
			}
		});
		
		// add action listener on remove button
		rmvBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int index0 = inputTable.getSelectionModel().getMinSelectionIndex();
				int index1 = inputTable.getSelectionModel().getMaxSelectionIndex();

				for (int i = index1; i >= index0; i--) {
					int modelRow = inputTable.convertRowIndexToModel(i);
					inputTableModel.removeRow(modelRow);
				}

				if (inputTableModel.getRowCount() == 0) {
				//	checkButton.setEnabled(false);
				}

				rmvBtn.setEnabled(false);
			}
		});		
		
	}
	
	
	/**
	 * List selection listener.
	 */
	ListSelectionListener listSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!rmvBtn.isEnabled()){
				// set remove button enable
				rmvBtn.setEnabled(true);
			}
		}
	};
	
	
	
	public boolean isSelectedCheckCurrent() {
		return checkCurrent.isSelected();
	}

	public void setSelectedCheckCurrent(boolean state) {
		if(state){
			this.checkCurrent.doClick();
		}
		else{
			this.checkOtherFiles.doClick();
		}
		
	}

	public void setEnableCheckCurrent(boolean state) {
		this.checkCurrent.setEnabled(true);
	}
	



	/**
	 * Check if table contains the given URL.
	 * @param url The URL in string format.
	 * @return <code>true</code>>if URL is in table, <code>false</code>> if isn't.
	 */
	private boolean tableContains(String url){
		boolean toReturn = false;
		for(int i = 0; i < inputTableModel.getRowCount(); i++){
			if(url.equals(inputTableModel.getValueAt(i, 0)) ){
				return true;
			}
		}

		return toReturn;
	}
	
	
	/**
	 * Get a list with URLs, in string format, from files table.
	 * @return
	 */
	public List<String> getTableUrls() {
		List<String> toReturn = new ArrayList<String>();

		// add rows in a list
		for (int i = 0; i < inputTableModel.getRowCount(); i++) {
			toReturn.add(inputTableModel.getValueAt(i, 0).toString());
		}
		return toReturn;
	}
	
	/**
	 * Add rows in table.
	 * @param URLs List with URLs in string format.
	 */
	public void addRowsInTable(List<String> URLs){
		int size = URLs.size();
		for (int i = 0; i < size; i++) {
			if(!tableContains(URLs.get(i))){
				inputTableModel.addRow(new String[] { URLs.get(i) });
			}
		}
	}
	
	
	
}
