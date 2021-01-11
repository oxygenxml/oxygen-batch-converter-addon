package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.transformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.utils.ConverterReaderUtils;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.PegdownExtensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;

/**
 * Converter implementation for Markdown to HTML.
 * 
 * @author Cosmin Duna
 *
 */
public class MarkdownToHtmlConverter implements Converter {

	/**
	 * Convert Markdown to HTML.
	 * 
	 * @param originalFile
	 *          The markdown file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The converted HTML content in String format or <code>null</code> if conversion
	 *         process failed.
	 * @throws TransformerException
	 */
	@Override
	public ConversionResult convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator, UserInputsProvider userInputsProvider)
			throws TransformerException {

		// content to return
		String convertedContent = null;

		// create the parser
		final DataHolder options = PegdownOptionsAdapter.flexmarkOptions(true, PegdownExtensions.ALL - PegdownExtensions.EXTANCHORLINKS);

		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();

		String contentToParse = "";

		try {
			// Get the content to parse.
			if (contentReader == null) {
				contentToParse = ConverterFileUtils.readFile(originalFile);
			} else {
				contentToParse = ConverterReaderUtils.getString(contentReader);
			}

			// Parse the content.
			Node document = parser.parse(contentToParse);

			convertedContent = renderer.render(document);
		} catch (IOException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		}

		return new ConversionResult(convertedContent);
	}
}
