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
	 * Complete doctype in DITA files.
	 */
	public static final String DOCTYPE_DITA = "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA "
			+ "Topic//EN\" \"topic.dtd\">";

	/**
	 * System doctype in DITA composite files.
	 */
	public static final String DOCTYPE_SYSTEM_DITA_COMPOSITE = "ditabase.dtd";
	/**
	 * Public doctype in DITA composite files.
	 */
	public static final String DOCTYPE_PUBLIC_DITA_COMPOSITE = "-//OASIS//DTD DITA Composite//EN";

	/**
	 * Completed doctype in DITA composite files.
	 */
	public static final String DOCTYPE_DITA_COMPOSITE = "<!DOCTYPE dita PUBLIC \"-//OASIS//DTD DITA Composite//EN\" \"ditabase.dtd\">";

	
	 /**
   * System doctype in DB4 files.
   */
	public static final String DOCTYPE_PUBLIC_DB4 = "-//OASIS//DTD DocBook XML V4.5//EN";
	
	/**
	 * System doctype in DB4 files.
	 */
	public static final String DOCTYPE_SYSTEM_DB4 = "http://docbook.org/xml/4.5/docbookx.dtd";
	
	/**
   * Public doctype in DB4 with MATHML files.
   */
	public static final String DOCTYPE_PUBLIC_DB4_MAHTML = "-//OASIS//DTD DocBook EBNF Module V1.1CR1//EN";
	
	/**
	 * System doctype in DB4 with Mathml files.
	 */
	public static final String DOCTYPE_SYSTEM_DB4_MATHML = "http://www.oasis-open.org/docbook/xml/mathml/1.1CR1/dbmathml.dtd";
	
	/**
   * Public doctype in DB5 with MATHML files.
   */
	public static final String DOCTYPE_PUBLIC_DB5_MAHTML = "-//W3C//DTD MathML 2.0//EN";
	
	/**
	 * System doctype in DB5 with MATHML files.
	 */
	public static final String DOCTYPE_SYSTEM_DB5_MATHML = "http://www.w3.org/Math/DTD/mathml2/mathml2.dtd";
	
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
