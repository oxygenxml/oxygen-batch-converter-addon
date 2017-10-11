package com.oxygenxml.resources.batch.converter.extensions;

/**
 * The files extensions.
 * 
 * @author intern4
 *
 */
public interface FileExtensionType {

	/**
	 * Extensions of HTML type.
	 */
	public final String[] INPUT_HTML_EXTENSIONS = new String[] { "html", "xhtml", "htm", ".htx" };

	/**
	 * Extensions of Markdown type.
	 */
	public final String[] INPUT_MD_EXTENSIONS = new String[] { "md", "markdown" };

	/**
	 * Extensions of XML type.
	 */
	public final String[] INPUT_XML_EXTENSIONS = new String[] { "xml" };
	/**
	 * Extensions of JSON type.
	 */
	public final String[] INPUT_JSON_EXTENSIONS = new String[] { "json" };
	
	/**
	 * XHtml output type
	 */
	public final String XHTML_OUTPUT_EXTENSION = "xhtml";

	/**
	 * DITA output type
	 */
	public final String DITA_OUTPUT_EXTENSION = "dita";

	/**
	 * XML output type
	 */
	public final String XML_OUTPUT_EXTENSION = "xml";
	/**
	 * JSON output type
	 */
	public final String JSON_OUTPUT_EXTENSION = "json";
}
