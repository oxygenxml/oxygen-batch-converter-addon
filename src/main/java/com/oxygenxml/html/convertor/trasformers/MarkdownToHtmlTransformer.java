package com.oxygenxml.html.convertor.trasformers;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import javax.xml.transform.TransformerException;

import org.parboiled.Parboiled;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.Parser;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.MailLinkNode;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;

public class MarkdownToHtmlTransformer implements Transformer{

	/**
	 * Convert the markdown document from the given URL in HTML.
	 * 
	 * @param originalFileLocation
	 *          The URL location of document.
	 * @param contentReader
	 *          Reader of the document.
	 * @return The conversion in HTML or null if conversion process fail.
	 * @throws TransformerException
	 */
	@Override
	public String convert(URL originalFileLocation, Reader contentReader) throws TransformerException   {
		//content to return 
		String toReturn = null;
			
		//create the parser
		Parser parser = Parboiled.createParser(Parser.class,
				// EXM-37621 Nu mai generam linkuri pe headere...
				(Extensions.ALL ^ Extensions.ANCHORLINKS ^ Extensions.HARDWRAPS) | Extensions.TASKLISTITEMS, 10000L,
				Parser.DefaultParseRunnerProvider, PegDownPlugins.NONE);

			String protocol = originalFileLocation.getProtocol();
			String stringUrl = originalFileLocation.getPath();
			
			Path path = Paths.get(stringUrl.substring(stringUrl.lastIndexOf(protocol)+2));
			
			try {
				// Get the content to parse.
				String contentToParse = new String(Files.readAllBytes(path), "UTF-8");

				// Get the root node.
				RootNode root = parser.parse((contentToParse).toCharArray());
				
				// Serialize !
				toReturn = new ToHtmlSerializer(new LinkRenderer() {
					@Override
					public Rendering render(MailLinkNode node) {
						// Avoid obfuscating the email link.
						return new Rendering("mailto:" + node.getText(), node.getText());
					}
				}, Collections.<String, VerbatimSerializer>emptyMap()).toHtml(root);

			} catch (IOException e) {
				e.printStackTrace();
				throw new TransformerException(e.getMessage());
			}

		return toReturn;
	}
}
