package com.oxygenxml.html.convertor.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.oxygenxml.html.convertor.ConvertorInteractor;
import com.oxygenxml.html.convertor.FileType;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Panel for add input files
 * 
 * @author intern4
 *
 */
public class InputPanel extends JPanel {

	/**
	 * Table with files to check.
	 */
	private JTable tableFiles = new JTable(20, 2);

	/**
	 * Model of table
	 */
	private DefaultTableModel modelTable;
	/**
	 * ScrollPane for table.
	 */
	private JScrollPane scrollPane = new JScrollPane(tableFiles);
	/**
	 * Button for add files in table
	 */
	private JButton addFilesBtn;
	/**
	 * Button for add folder in table
	 */
	private JButton addFolderBtn;
	/**
	 * Button for remove elements from table
	 */
	private JButton remvBtn;

	/**
	 * Translator
	 */
	private Translator translator;

	/**
	 * Convertor interactor.
	 */
	private ConvertorInteractor convertorInteractor;

	/**
	 * Constructor
	 */
	public InputPanel(final Translator translator, final ConvertorInteractor convertorInteractor) {
		this.translator = translator;
		this.convertorInteractor = convertorInteractor;

		addFilesBtn = new JButton(translator.getTranslation(Tags.ADD_FILE_TABLE, ""));
		addFolderBtn = new JButton(translator.getTranslation(Tags.ADD_FOLDER_TABLE, ""));
		remvBtn = new JButton(translator.getTranslation(Tags.REMOVE_TABLE, ""));
		remvBtn.setEnabled(false);

		// initialize the panel
		initPanel();

		// add action listener on add files button
		addFilesBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// open a URL chooser
				File[] files = PluginWorkspaceProvider.getPluginWorkspace().chooseFiles(null, "", FileType.INPUT_TYPES,
						"Markdown and HTML files");

				if (files != null) {

					if (modelTable.getRowCount() == 0) {
						convertorInteractor.setOutputFolder(files[0].getParent().toString() + File.separator + "output");
					}

					// add files in table
					addFilesInTable(files);

					// set check button enable
					convertorInteractor.setEnableConvert(true);
				}
			}
		});

		// add action listener on add folder button
		addFolderBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// open a URL chooser
				File file = PluginWorkspaceProvider.getPluginWorkspace().chooseDirectory();

				if (file != null) {
					List<String> listToAdd = new ArrayList<String>();

					InputPanelUtil.getFilesFromFolder(file, listToAdd);

					if(!listToAdd.isEmpty()){
						convertorInteractor.setOutputFolder(file.toString()+ File.separator + "output");
					}
					
					// add files in table
					addFilesInTable(listToAdd);

					// set convert button enable
					convertorInteractor.setEnableConvert(true);
				}
			}
		});

		// add action listener on remove button
		remvBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int index0 = tableFiles.getSelectionModel().getMinSelectionIndex();
				int index1 = tableFiles.getSelectionModel().getMaxSelectionIndex();

				for (int i = index1; i >= index0; i--) {
					int modelRow = tableFiles.convertRowIndexToModel(i);
					modelTable.removeRow(modelRow);
				}

				if (modelTable.getRowCount() == 0) {
					convertorInteractor.setEnableConvert(false);
					convertorInteractor.setOutputFolder("");
				}

				remvBtn.setEnabled(false);
			}
		});

	}

	/**
	 * Get file from files table.
	 * 
	 * @return List with files in String format.
	 */
	public List<String> getFilesFromTable() {
		List<String> toReturn = new ArrayList<String>();

		int size = modelTable.getRowCount();
		for (int i = 0; i < size; i++) {
			toReturn.add(String.valueOf(modelTable.getValueAt(i, 0)));
		}

		return toReturn;
	}

	/**
	 * Add files in table.
	 * 
	 * @param files
	 *          Vector with files.
	 */
	public void addFilesInTable(File[] files) {
		int size = files.length;
		for (int i = 0; i < size; i++) {
			if (!tableContains(files[i].toString())) {
				modelTable.addRow(new String[] { files[i].toString() });
			}
		}
	}

	/**
	 * Add files in table.
	 * 
	 * @param files
	 *          List with files in string format.
	 */
	public void addFilesInTable(List<String> files) {
		int size = files.size();
		for (int i = 0; i < size; i++) {
			if (!tableContains(files.get(i).toString())) {
				modelTable.addRow(new String[] { files.get(i) });
			}
		}
	}

	/**
	 * Delete all rows from files table.
	 */
	public void clearTable() {
		int size = modelTable.getRowCount();
		for (int i = 0; i < size; i++) {
			modelTable.removeRow(0);
		}
	}

	/**
	 * Method for initialize the Panel.
	 */
	private void initPanel() {

		modelTable = new DefaultTableModel(new String[] { translator.getTranslation(Tags.FILES_TABLE_HEAD, "") }, 0);
		// set modal on table
		tableFiles.setModel(modelTable);

		// add list selection listener on table
		tableFiles.getSelectionModel().addListSelectionListener(listSelectionListener);

		// configure the scroll pane
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tableFiles.setPreferredScrollableViewportSize(new Dimension(scrollPane.getWidth(), scrollPane.getHeight()));
		scrollPane.setOpaque(false);

		// set layout manager
		this.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		// ------add checkCurrent radio button
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JLabel(translator.getTranslation(Tags.ADD_INPUT_FILES_LABEL, "")), gbc);

		// ------add scrollPane
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 15, 0, 5);
		gbc.fill = GridBagConstraints.BOTH;
		this.add(scrollPane, gbc);

		// ------add addBtn and removeBtn
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 0, 0, 5);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;

		// create a panel that contains add and remove buttons
		JPanel btnsPanel = new JPanel();
		btnsPanel.setLayout(new GridLayout(1, 3));
		btnsPanel.setOpaque(false);

		btnsPanel.add(addFolderBtn);
		btnsPanel.add(addFilesBtn);
		btnsPanel.add(remvBtn);

		// add btnsPanel
		this.add(btnsPanel, gbc);

	}

	/**
	 * List selection listener.
	 */
	ListSelectionListener listSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!remvBtn.isEnabled()) {
				// set remove button enable
				remvBtn.setEnabled(true);
			}
		}
	};

	/**
	 * Check if table contains the given URL.
	 * 
	 * @param url
	 *          The URL in string format.
	 * @return <code>true</code>>if URL is in table, <code>false</code>> if isn't.
	 */
	private boolean tableContains(String url) {
		boolean toReturn = false;
		int size = modelTable.getRowCount();
		for (int i = 0; i < size; i++) {
			if (url.equals(modelTable.getValueAt(i, 0))) {
				return true;
			}
		}

		return toReturn;
	}
}
