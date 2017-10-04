package com.oxygenxml.resources.batch.converter.extensions;

/**
 * The file types used in this app
 * 
 * @author intern4
 *
 */
public interface FileExtensionType {

	/**
	 * All Input type
	 */
	public final static String[] INPUT_TYPES = new String[] { "html", "xhtml", "htm", "htx", "markdown", "md" };

	/**
	 * Extensions of HTML type.
	 */
	public final String[] INPUT_HTML_EXTENSIONS = new String[] { "html", "xhtml", "htm", ".htx" };

	/**
	 * Extensions of Markdown type.
	 */
	public final String[] INPUT_MD_EXTENSIONS = new String[] { "md", "markdown" };

	/**
	 * XHtml output type
	 */
	public final String XHTML_OUTPUT_EXTENSION = "xhtml";

	/**
	 * Dita output type
	 */
	public final String DITA_OUTPUT_EXTENSION = "dita";

}
