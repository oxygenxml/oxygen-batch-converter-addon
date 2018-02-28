package com.oxygenxml.resources.batch.converter.doctype;

/**
 * Document types.
 * @author Cosmin Duna
 *
 */
public class Doctypes {

	/**
	 * Private constructor.
	 */
  private Doctypes() {
    throw new IllegalStateException("Utility class");
  }
	
	
	/**
	 * System doctype in DITA files.
	 */
	public static final String DOCTYPE_SYSTEM_DITA = "topic.dtd";
	/**
	 * Public doctype in DITA files.
	 */
	public static final String DOCTYPE_PUBLIC_DITA = "-//OASIS//DTD DITA Topic//EN";

	/**
	 * Complete doctype in DITA file.
	 */
	public static final String DOCTYPE_DITA = "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA "
			+ "Topic//EN\" \"topic.dtd\">";

	 /**
   * System doctype in DB4 files.
   */
	public static final String DOCTYPE_PUBLIC_DB4 = "-//OASIS//DTD DocBook XML V4.5//EN";
	
	 /**
   * Public doctype in DB4 files.
   */
	public static final String DOCTYPE_SYSTEM_DB4 = "http://docbook.org/xml/4.5/docbookx.dtd";
	
	 /**
   * Complete doctype in DB4 files.
   */
	public static final String DOCTYPE_DB4 = "<!DOCTYPE article PUBLIC \"-//OASIS//DTD "
			+ "DocBook XML V4.5//EN\"\n" + 
			"                         \"http://docbook.org/xml/4.5/docbookx.dtd\">";
	
	/**
	 * System doctype in XHTML files.
	 */
	public static final String DOCTYPE_SYSTEM_XHTML = "about:legacy-compat";

	/**
	 * Complete doctype in XHTML files.
	 */
	public static final String DOCTYPE_XHTML = "<!DOCTYPE html>";
}
