package com.oxygenxml.resources.batch.converter.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;
/**
 * Progress dialog
 * @author Cosmin Duna
 *
 */
public class ProgressDialog extends OKCancelDialog implements ProgressDialogInteractor {

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Progress bar
	 */
	private JProgressBar progressBar;
	/**
	 * Label for notes.
	 */
	private JLabel noteLabel;
	
	/**
	 * Constructor
	 * @param parentFrame Parent frame.
	 * @param translator Translator. 
	 * @param converterType The type of converter.
	 */
	public ProgressDialog(JFrame parentFrame, Translator translator, String converterType) {
		super(parentFrame , "", true);
		
		noteLabel = new JLabel();
		
		progressBar =  new JProgressBar();
		progressBar.setStringPainted(false);
		progressBar.setIndeterminate(true);
		
		JPanel panel = new JPanel(new GridBagLayout());
	
		GridBagConstraints gbc = new GridBagConstraints();
		
		// add the message from progress dialog
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 15, 5, 15);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(new JLabel(translator.getTranslation(Tags.PROGRESS_DIALOG_MESSAGE,"")), gbc);
		
		// add the progress bar
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 5, 0, 5);
		panel.add(progressBar, gbc);
		
		// add the label for post notes
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 5, 10, 5);
		panel.add(noteLabel, gbc);
	
		add(panel);
		
		setTitle(translator.getTranslation(Tags.MENU_ITEM_TEXT, converterType));
		getOkButton().setVisible(false);
		
		setResizable(false);
		pack();
		setMinimumSize(new Dimension(getSize().width + 120 , getSize().height + 30));		
		setLocationRelativeTo(parentFrame);
		
	}

	/**
	 * Set the dialog visible.
	 */
	@Override
	public void setDialogVisible(boolean state) {
			SwingUtilities.invokeLater(new Runnable(){	
			@Override
				public void run() {
					setVisible(true);
				}
			});
	}

	/**
	 * Set the given note in label for notes.
	 * @param note The note to set.
	 */
	@Override
	public void setNote(final String note){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				noteLabel.setText("<html>"+ note + "</html>");
				
			}
		});
	}
	
	/**
	 * Close the dialog.
	 */
	@Override
	public void close(){
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				doCancel();

			}
		});
	}
	
	
	/**
	 * Add the given action listener to cancel buttons
	 * @param actionListener Action listener to add.
	 */
	public void addCancelActionListener(ActionListener actionListener){
		getCancelButton().addActionListener(actionListener);
	}
}
