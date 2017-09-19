package com.oxygenxml.html.convertor;

public interface FileType {

public final static String[] INPUT_TYPES = new String[] {"html", "xhtml", "htm","htx", "markdown", "md"}; 
	
public final String[] INPUT_HTML_EXTENSIONS = new String []{"html","xhtml", "htm", ".htx"};

public final String[] INPUT_MD_EXTENSIONS = new String []{"md","markdown"};	
	
	/**
	 * XHtml output type
	 */
	public final String XHTML_TYPE_AND_EXTENSION = "xhtml";
	
	/**
	 * Dita output type
	 */
	public final String DITA_TYPE_AND_EXTENSION = "dita";
	
	
}
