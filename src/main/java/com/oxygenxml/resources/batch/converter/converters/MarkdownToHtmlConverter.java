package com.oxygenxml.resources.batch.converter.converters;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.DataHolder;

public class MarkdownToHtmlConverter implements Converter{

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
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator) throws TransformerException   {
		//content to return 
		String toReturn = null;
			
		
		System.out.println(originalFileLocation.toString());
		
		//create the parser
		
		final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
        Extensions.ALL
				);
		
	  Parser parser = Parser.builder(OPTIONS).build();
    HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();
		
    //create file path
    String protocol = originalFileLocation.getProtocol();
		String stringUrl = originalFileLocation.getFile();
		
		Path path = Paths.get(stringUrl.substring(stringUrl.lastIndexOf(protocol)+2));
		
		try {
		// Get the content to parse.
		String contentToParse = new String(Files.readAllBytes(path), "UTF-8");
		
		 Node document = parser.parse(contentToParse);
    
		 toReturn = renderer.render(document); 
		 
		} catch (IOException e) {
			e.printStackTrace();
			//TODO Add a message
			throw new TransformerException(e.getMessage(), e.getCause());
		}
		
	/*	Parser parser = Parboiled.createParser(Parser.class,
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
				throw new TransformerException(e);
			}
	*/
		return toReturn;
	}
}
