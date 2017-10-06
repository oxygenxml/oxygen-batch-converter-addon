package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.net.URL;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public interface ContentPrinter {

	public void print(String contentToPrint, TransformerFactoryCreator transformerCreator,
			File originalDocument, File outputFolder, String converterType) throws TransformerException;
}
