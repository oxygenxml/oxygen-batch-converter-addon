package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.DataHolder;

/**
 * Converter implementation for Markdown to HTML.
 * 
 * @author intern4
 *
 */
public class MarkdownToHtmlConverter implements Converter {

	/**
	 * Convert Markdown to HTML.
	 * 
	 * @param originalFile
	 *          The markdown file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, the converter will process this reader and will ignore the given file.
	 * @return The converted HTML content in String format or null if conversion
	 *         process failed.
	 * @throws TransformerException
	 */
	@Override
	public String convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		// content to return
		String toReturn = null;

		// create the parser
		final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(Extensions.ALL);

		Parser parser = Parser.builder(OPTIONS).build();
		HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();

		String contentToParse = "";

		try {
			// Get the content to parse.
			if (contentReader == null) {
				contentToParse = ConverterFileUtils.readFile(originalFile);
			} else {
				int intValueOfChar;
				while ((intValueOfChar = contentReader.read()) != -1) {
					contentToParse += (char) intValueOfChar;
				}
			}

			// Parse the content.
			Node document = parser.parse(contentToParse);

			toReturn = renderer.render(document);
		} catch (IOException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		}

		return toReturn;
	}
}
