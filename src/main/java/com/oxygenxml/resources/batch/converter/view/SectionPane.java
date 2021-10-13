package com.oxygenxml.resources.batch.converter.view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Section pane(JLabel + JSeparator).
 *
 */
public class SectionPane extends JPanel {

	/**
	 * Default serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param title
	 *          The title of the section pane.
	 */
	public SectionPane(String title) {
		super(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		// Add the label.
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 0, 5, 5);
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		add(titleLabel, c);

		// Add the separator.
		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.insets.right = 0;
		add(new JSeparator(), c);
	}
}
