package com.oxygenxml.html.convertor.view;

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

import com.oxygenxml.html.convertor.reporter.ProgressDialogInteractor;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;

public class ProgressDialog extends OKCancelDialog implements ProgressDialogInteractor {


	private JProgressBar progressBar;
	private JLabel noteLabel;
	
	public ProgressDialog(JFrame parentFrame, Translator translator) {
		super(parentFrame , "", true);
		
		noteLabel = new JLabel();
		
		progressBar =  new JProgressBar();
		progressBar.setStringPainted(false);
		progressBar.setIndeterminate(true);
		
		JPanel panel = new JPanel(new GridBagLayout());
	
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 15, 5, 15);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(new JLabel(translator.getTranslation(Tags.PROGRESS_DIALOG_MESSAGE,"")), gbc);
		
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 5, 0, 5);
		panel.add(progressBar, gbc);
		
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 5, 10, 5);
		panel.add(noteLabel, gbc);
	
		add(panel);
		
		setTitle(translator.getTranslation(Tags.DIALOG_TITLE, ""));
		getOkButton().setVisible(false);
		
		setResizable(false);
		setSize(new Dimension(370, 150));
		setLocationRelativeTo(parentFrame);
		
	}

	public void setDialogVisible(boolean state) {
			SwingUtilities.invokeLater(new Runnable(){	
			@Override
				public void run() {
					setVisible(true);
				}
			});
	}

	@Override
	public void setNote(final String note){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				noteLabel.setText("<html>"+ note + "</html>");
				
			}
		});
	}
	
	@Override
	public void close(){
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				doCancel();

			}
		});
	}
	
	
	public void addCancelActionListener(ActionListener actionListener){
		getCancelButton().addActionListener(actionListener);
	}
}


