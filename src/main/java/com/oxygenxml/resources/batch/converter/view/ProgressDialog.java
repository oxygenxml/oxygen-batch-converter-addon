package com.oxygenxml.resources.batch.converter.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

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
	 * The fileNote to be set in note label.
	 */
	private File fileNote;
	
	/**
   * Coalescing timer for the change note.
	 */
  private Timer changeNotesCoalescingTimer;
	
	/**
	 * Constructor
	 * @param parentFrame Parent frame.
	 * @param translator Translator. 
	 * @param converterType The type of converter.
	 */
	public ProgressDialog(JFrame parentFrame, Translator translator, String converterType) {
		super(parentFrame , "", true);
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
	  //Add 'Operation in progress' label.
    JLabel taskNameLabel = new JLabel(translator.getTranslation(Tags.PROGRESS_DIALOG_MESSAGE,""));
    Icon icon = null;
    try {
      icon = (Icon) UIManager.get("OptionPane.informationIcon");
    } catch (ClassCastException e) {
      // Negglected.
    }
    
    if(icon != null) {
    	taskNameLabel.setIcon(icon);
    	taskNameLabel.setIconTextGap(7);
    }
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 7, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(taskNameLabel, gbc);
		
		// Add the progress bar
		progressBar =  new JProgressBar();
		progressBar.setStringPainted(false);
		progressBar.setIndeterminate(true);
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(7, 0, 0, 0);
	  progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 15));
		panel.add(progressBar, gbc);
		
		noteLabel = new JLabel();
	  Font font = noteLabel.getFont();
    if (font != null) {
      FontMetrics fontMetrics = noteLabel.getFontMetrics(font);
      if (fontMetrics != null) {
        int height = fontMetrics.getHeight();
        Dimension preferredSize = new Dimension(noteLabel.getPreferredSize().width, height);
        noteLabel.setPreferredSize(preferredSize);
      }
    }
    
    changeNotesCoalescingTimer = new Timer(200, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileNote != null) {
					noteLabel.setText(extractNote(fileNote));
					noteLabel.setToolTipText(fileNote.getAbsolutePath());
				}
			}
		});
		
		// Add the label for the informal notes
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		panel.add(noteLabel, gbc);
	
		add(panel);
		
		setTitle(translator.getTranslation(Tags.MENU_ITEM_TEXT, converterType));
		getOkButton().setVisible(false);
		
		setResizable(false);
		pack();
		setMaximumSize(new Dimension(800, 800 / 3));
		setMinimumSize(new Dimension(400, 400 / 3));
		
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
	public void setNote(final File note){
		fileNote = note;
		changeNotesCoalescingTimer.restart();
	}

	/**
	 * Extract a note from the given file that will fit in the note label.
	 * 
	 * @param file The file to be processed.
	 * 
	 * @return The note.
	 */
	private String extractNote(final File file) {
		String noteToSet = file.getAbsolutePath(); 

		Graphics graphics = getGraphics();
		if(graphics != null) {
			FontMetrics fontMetrics = graphics.getFontMetrics();
			int pathWidth = fontMetrics.stringWidth(noteToSet);
			int labelWidth = noteLabel.getWidth();

			if(pathWidth > labelWidth) {
				String fileName = file.getName();
				String rightSideNote = "..." + File.separator + fileName;

				int rightSideWidth = fontMetrics.stringWidth(rightSideNote);
				if(rightSideWidth < labelWidth) {
					int leftSideWidth = labelWidth - rightSideWidth;
					String leftText = file.getParentFile().getAbsolutePath();
					int auxWidth = fontMetrics.stringWidth(leftText);
					while (auxWidth > leftSideWidth) {
						int length = leftText.length();
						leftText = leftText.substring(0, length - 1);
						auxWidth  = fontMetrics.stringWidth(leftText);
					}
					noteToSet = leftText + rightSideNote;

				} else if (rightSideWidth == labelWidth) {
					noteToSet = rightSideNote;
				}
			}
		}
		
		return noteToSet; 
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
