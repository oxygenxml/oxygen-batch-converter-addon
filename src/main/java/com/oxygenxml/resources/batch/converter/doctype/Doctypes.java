package com.oxygenxml.resources.batch.converter.doctype;

/**
 * Document types.
 * @author intern4
 *
 */
public interface Doctypes {

	/**
	 * System doctype in DITA files.
	 */
	static final String DOCTYPE_SYSTEM_DITA = "topic.dtd";
	/**
	 * Public doctype in DITA files.
	 */
	static final String DOCTYPE_PUBLIC_DITA = "-//OASIS//DTD DITA Topic//EN";

	/**
	 * System doctype in XHTML files.
	 */
	static final String DOCTYPE_SYSTEM_XHTML = "about:legacy-compat";
}
