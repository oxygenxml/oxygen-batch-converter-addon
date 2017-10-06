package com.oxygenxml.resources.batch.converter.printer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.extensions.ExtensionGetter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public class ContentPrinterImpl implements ContentPrinter {

	@Override
	public void print(String contentToPrint, TransformerFactoryCreator transformerCreator, URL currentDocument,
			String outputFolder, String converterType) throws TransformerException {

		File outFile = FilePathGenerator.generate(currentDocument, ExtensionGetter.getOutputExtension(converterType),
				outputFolder);

		// create a unique file path if actual exist
		outFile = FilePathGenerator.getFileWithCounter(outFile);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			out.write(contentToPrint);

			try {
				out.close();
			} catch (Exception e) {
			}
		} catch (IOException e) {
			throw new TransformerException(e.getMessage());
		}

	}
}
