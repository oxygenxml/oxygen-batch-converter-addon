package com.oxygenxml.html.convertor.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;


public class InputPanel extends JPanel {

	JRadioButton checkCurrent = new JRadioButton();
	JRadioButton checkOtherFiles = new JRadioButton();
	JTable inputTable = new JTable();
	DefaultTableModel inputTableModel ;

	JButton addBtn = new JButton();
	JButton rmvBtn = new JButton();
	
	public InputPanel(Translator translator) {

		JScrollPane scrollPane = new JScrollPane(inputTable);

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

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

	}
}
