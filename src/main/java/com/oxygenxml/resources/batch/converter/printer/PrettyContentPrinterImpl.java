package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.io.StringReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;

import com.oxygenxml.resources.batch.converter.doctype.DoctypeGetter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

/**
 * Content pretty printer implementation.
 * 
 * @author intern4
 *
 */
public class PrettyContentPrinterImpl implements ContentPrinter {

	/**
	 * Prettify the given content and write in given output file.
	 * 
	 * @param contentToPrint The content to print.
	 * @param transformerCreator A transformer creator.
	 * @param converterType The type of converter.
	 * @param outputFile The output file.
	 * @param styleSource The source XSL, or <code>null</code> 
	 * @throws TransformerException
	 */
		public void print(String contentToPrint, TransformerFactoryCreator transformerCreator, String converterType,
				File outputFile,  StreamSource styleSource)
				throws TransformerException {

		// create the transformer
		Transformer transformer = transformerCreator.createTransformer(styleSource);

		// set the output properties
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		if(!(DoctypeGetter.getSystemDoctype(converterType).isEmpty() || DoctypeGetter.getPublicDoctype(converterType).isEmpty()) ){
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DoctypeGetter.getSystemDoctype(converterType));
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, DoctypeGetter.getPublicDoctype(converterType));
		}
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

		// get the input source
		InputSource inputSource = new InputSource(new StringReader(contentToPrint));

		// create a unique file path if actual exist
		outputFile = ConverterFileUtils.getFileWithCounter(outputFile);

		try {
			// prettify and print
			transformer.transform(new SAXSource(inputSource), new StreamResult(outputFile));
		} catch (TransformerException e) {
			throw new TransformerException(e.getException().getMessage(), e.getException().getCause());
		}
	}


}
