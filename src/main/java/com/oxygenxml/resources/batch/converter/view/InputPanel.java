package com.oxygenxml.resources.batch.converter.view;

import java.awt.Component;
import java.awt.Dimension;
import java.text.MessageFormat;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.extensions.ExtensionGetter;
import com.oxygenxml.batch.converter.core.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.InputFilesManager;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.ui.Table;

/**
 * Panel for add input files
 *
 * @author Cosmin Duna
 *
 */
public class InputPanel extends JPanel {

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Table with files to check.
	 */
	private Table tableFiles = new Table();
	/**
	 * Model of table
	 */
	private DefaultTableModel modelTable;
	/**
	 * ScrollPane for table.
	 */
	private JScrollPane scrollPane;
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
	 * The converter type
	 */
	private String converterType;

	/**
	 * Translator
	 */
	private transient Translator translator;

	/**
	 * Manages the input files list and their root directory associations.
	 */
	private InputFilesManager inputFilesManager;

	/**
	 * Shown when at least one file was added via "Add Folder", to inform the user
	 * that the subfolder structure will be preserved in the output.
	 */
	private JLabel infoLabel;

	 /**
   * The inset used between components
   */
  private static final int INSET_BETWEEN_COMPONENTS = 6;


	/**
	 * Constructor
	 */
	public InputPanel(final String converterType, final Translator translator,
			final BatchConverterInteractor convertorInteractor, final InputFilesManager inputFilesManager) {
		this.translator = translator;
		this.converterType = converterType;
		this.inputFilesManager = inputFilesManager;

		scrollPane = new JScrollPane((JTable)tableFiles);
		scrollPane.setPreferredSize(new Dimension(450, 100));

		addFilesBtn = new JButton(translator.getTranslation(Tags.ADD_FILE_TABLE) + "...");
		addFilesBtn.setToolTipText(buildTooltipWithExtensions(Tags.ADD_FILES_TOOLTIP, converterType, translator));
		addFolderBtn = new JButton(translator.getTranslation(Tags.ADD_FOLDER_TABLE) + "...");
		addFolderBtn.setToolTipText(buildTooltipWithExtensions(Tags.ADD_FOLDER_TOOLTIP, converterType, translator));
		remvBtn = new JButton(translator.getTranslation(Tags.REMOVE_TABLE));
		remvBtn.setEnabled(false);

		infoLabel = new JLabel(translator.getTranslation(Tags.PRESERVE_FOLDER_STRUCTURE_INFO));
		infoLabel.setIcon(IconsLoader.loadIcon(Icons.LICENSE_INFO));
		infoLabel.setVisible(false);

		// initialize the panel
		initPanel();

		// add action listener on add files button
		addFilesBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// open a file chooser
				File[] files = PluginWorkspaceProvider.getPluginWorkspace().chooseFiles(null, "",
						ExtensionGetter.getInputExtension(converterType),
						"");

				if (files != null) {
					if (modelTable.getRowCount() == 0 && convertorInteractor.getOutputFolderPath().isEmpty()) {
						convertorInteractor.setOutputFolder(files[0].getParent() + File.separator + "output");
					}

					inputFilesManager.addFiles(Arrays.asList(files));
					addFilesInTable(files);
					tableFiles.repaint();
					updateInfoLabel();

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
					List<File> listToAdd = ConverterFileUtils.getAllFiles(file,
						Arrays.asList(ExtensionGetter.getInputExtension(converterType)));

					if (!listToAdd.isEmpty() && convertorInteractor.getOutputFolderPath().isEmpty()) {
						convertorInteractor.setOutputFolder(file.toString() + File.separator + "output");
					}

					inputFilesManager.addFilesFromFolder(listToAdd, file);
					addFilesInTable(listToAdd);
					updateInfoLabel();

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
					File removedFile = (File) modelTable.getValueAt(modelRow, 0);
					inputFilesManager.removeFile(removedFile);
					modelTable.removeRow(modelRow);
				}

				if (modelTable.getRowCount() == 0) {
					convertorInteractor.setEnableConvert(false);
				}

				updateInfoLabel();
				remvBtn.setEnabled(false);
			}
		});

	}

	/**
	 * Get file from files table.
	 *
	 * @return List with files.
	 */
	public List<File> getFilesFromTable() {
		List<File> toReturn = new ArrayList<File>();
		int size = modelTable.getRowCount();

		for (int i = 0; i < size; i++) {
			toReturn.add( (File)modelTable.getValueAt(i, 0) );
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
			if (!tableContains(files[i])) {
				modelTable.addRow(new File[] { files[i] });
			}
		}
	}

	/**
	 * Add files in table.
	 *
	 * @param files
	 *          List with files in string format.
	 */
	public void addFilesInTable(List<File> files) {
		int size = files.size();
		for (int i = 0; i < size; i++) {
			if (!tableContains(files.get(i))) {
				modelTable.addRow(new File[] { files.get(i) });
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
		inputFilesManager.clear();
		updateInfoLabel();
	}

	/**
	 * Build a localized tooltip string for the given tag, substituting the accepted
	 * input file extensions for the given converter type as the <code>{0}</code> placeholder.
	 *
	 * @param tag           The translation tag whose value contains a <code>{0}</code> placeholder.
	 * @param converterType The type of converter.
	 * @param translator    The translator.
	 * @return The localized tooltip string with the extensions substituted.
	 */
	private static String buildTooltipWithExtensions(String tag, String converterType, Translator translator) {
		String[] extensions = ExtensionGetter.getInputExtension(converterType);
		StringBuilder extList = new StringBuilder();
		for (int i = 0; i < extensions.length; i++) {
			if (i > 0) {
				extList.append(", ");
			}
			extList.append('.').append(extensions[i]);
		}
		return MessageFormat.format(translator.getTranslation(tag), extList.toString());
	}

	/**
	 * Show or hide the info label depending on whether any folder mappings exist.
	 */
	private void updateInfoLabel() {
		infoLabel.setVisible(inputFilesManager.hasFolderMappings());
	}

	/**
	 * Method for initialize the Panel.
	 */
	private void initPanel() {

		modelTable = new DefaultTableModel(0, 1) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		// set modal on table
		tableFiles.setModel(modelTable);
		tableFiles.setTableHeader(null);
		tableFiles.setDefaultRenderer(Object.class, new InputFileCellRenderer(inputFilesManager));

		// add list selection listener on table
		tableFiles.getSelectionModel().addListSelectionListener(listSelectionListener);

		// configure the scroll pane
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		tableFiles.setPreferredScrollableViewportSize(new Dimension(scrollPane.getWidth(), scrollPane.getHeight()));
		scrollPane.setOpaque(false);

		// set layout manager
		this.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		// ------add label for input file
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		if (ConverterTypes.CONFLUENCE_TO_DITAMAP.equals(converterType)) {
		  gbc.insets = new Insets(0, 0, INSET_BETWEEN_COMPONENTS, 0);
		  this.add(new MultilineLabel(translator.getTranslation(Tags.ADD_INPUT_FILES_LABEL_CONFLUENCE)), gbc);
		}

		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);
		this.add(new JLabel(translator.getTranslation(Tags.ADD_INPUT_FILES_LABEL)), gbc);

		// ------add scrollPane
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 0, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		this.add(scrollPane, gbc);

		// ------add addBtn and removeBtn
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;

		// create a panel that contains add and remove buttons
		JPanel btnsPanel = new JPanel();
		btnsPanel.setLayout(new GridLayout(1, 3));
		btnsPanel.setOpaque(false);

		if (!ConverterTypes.CONFLUENCE_TO_DITAMAP.equals(converterType)) {
		  btnsPanel.add(addFolderBtn);
    }
		btnsPanel.add(addFilesBtn);
		btnsPanel.add(remvBtn);

		// add btnsPanel
		this.add(btnsPanel, gbc);

		// ------add info label for folder structure preservation
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 0, 0, 0);
		this.add(infoLabel, gbc);

	}

	/**
	 * List selection listener.
	 */
	transient ListSelectionListener listSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!remvBtn.isEnabled()) {
				// set remove button enable
				remvBtn.setEnabled(true);
			}
		}
	};

	/**
	 * Check if table contains the given file.
	 *
	 * @param file
	 *          The file.
	 * @return <code>true</code>>if file is in table, <code>false</code>> if isn't.
	 */
	private boolean tableContains(File file) {
		boolean toReturn = false;
		int size = modelTable.getRowCount();
		for (int i = 0; i < size; i++) {
			if (file.equals(modelTable.getValueAt(i, 0))) {
				return true;
			}
		}

		return toReturn;
	}

	/**
	 * Cell renderer for the input files table.
	 * Files added via "Add Folder" are displayed with their path relative to the
	 * root directory; the full root path is shown in the tooltip.
	 * Individually added files show their absolute path in both the cell and tooltip.
	 */
	private static class InputFileCellRenderer extends DefaultTableCellRenderer {

		/**
		 * Default serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Used to resolve the root directory for each file.
		 */
		private final InputFilesManager inputFilesManager;

		/**
		 * Constructor.
		 *
		 * @param inputFilesManager The manager that tracks root directories.
		 */
		InputFileCellRenderer(InputFilesManager inputFilesManager) {
			this.inputFilesManager = inputFilesManager;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof File) {
				File file = (File) value;
				File rootDir = inputFilesManager.getRootDirectoryForFile(file);
				if (rootDir != null) {
					Path relativePath = rootDir.toPath().relativize(file.toPath());
					setText(relativePath.toString());
					setToolTipText("<html><b>Root folder:</b> " + rootDir.getAbsolutePath()
							+ "<br><b>File:</b> " + file.getAbsolutePath() + "</html>");
				} else {
					setText(file.getName());
					setToolTipText(file.getAbsolutePath());
				}
			}
			return this;
		}
	}
}
