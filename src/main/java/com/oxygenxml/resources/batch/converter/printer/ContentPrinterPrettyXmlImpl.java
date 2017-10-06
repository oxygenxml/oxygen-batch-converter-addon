package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import com.oxygenxml.resources.batch.converter.doctype.DoctypeGetter;
import com.oxygenxml.resources.batch.converter.extensions.ExtensionGetter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Content printer
 * 
 * @author intern4
 *
 */
public class ContentPrinterPrettyXmlImpl implements ContentPrinter{

	/**
	 * Prettify the given content and write in given output folder.
	 * @param contentToPrint
	 * @param transformerCreator
	 * @param currentDocument
	 * @param outputFolder
	 * @param converterType
	 * @throws TransformerException
	 */
	public void print(String contentToPrint, TransformerFactoryCreator transformerCreator,
			URL currentDocument, String outputFolder, String converterType) throws TransformerException  {

			//create the trasformer
			Transformer transformer = transformerCreator.createTransformer(null);

			File outFile = FilePathGenerator.generate(currentDocument, ExtensionGetter.getOutputExtension(converterType),
					outputFolder);
			
			
			//set the output properties
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DoctypeGetter.getSystemDoctype(converterType));
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, DoctypeGetter.getPublicDoctype(converterType));
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			//get the input source
			InputSource inputSource = new InputSource(new StringReader(contentToPrint));

			//create a unique file path if actual exist 
			outFile = FilePathGenerator.getFileWithCounter(outFile);

			System.out.println("counterFile : "+  outFile.toString());
			
			try {
				//prettify and print 
				transformer.transform(new SAXSource(inputSource), new StreamResult(outFile));
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
			}
	}

}
