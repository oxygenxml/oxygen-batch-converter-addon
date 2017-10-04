package com.oxygenxml.resources.batch.converter.printer;

import java.net.URL;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public interface ContentPrinter {

	public void prettifyAndPrint(String contentToPrint, TransformerFactoryCreator transformerCreator,
			URL currentDocument, String outputFolder, String converterType) throws TransformerException;
}
