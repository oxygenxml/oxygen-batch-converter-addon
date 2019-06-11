package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.elovirta.dita.markdown.MarkdownReader;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterReaderUtils;


/**
 * Converter implementation for Markdown to DITA.
 * 
 * @author Cosmin Duna
 *
 */
public class MarkdownToDitaTransformer implements com.oxygenxml.resources.batch.converter.converters.Converter {

	/**
	 * The key for system property of transformer factory.
	 */
	private static final String KEY_TRANSFORMER_FACTORY = "javax.xml.transform.TransformerFactory";
	
	/**
	 * Property value of transformer factory. 
	 */
	private static final String VALUE_TRANSFORMER_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
	
	
	/**
	 * Convert Markdown to DITA.
	 * 
	 * @param originalFile
	 *          The Markdowm file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The converted DITA content in String format or <code>null</code> if conversion process failed.
	 * @throws TransformerException
	 */
	@Override
	public ConversionResult convert(File originalFileLocation, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator) throws TransformerException {
		// content to return
		String convertedContent = null;

		//create the transformer
		Transformer transformer = transformerCreator.createTransformer(null);

		// get the trasformFactory property
		String property = System.getProperty(KEY_TRANSFORMER_FACTORY);

		// set the trasformFactory property to
		// "net.sf.saxon.TransformerFactoryImpl"
		System.setProperty(KEY_TRANSFORMER_FACTORY, VALUE_TRANSFORMER_FACTORY);
		
		// reader for markdown document
		final MarkdownReader r = new MarkdownReader();

		try {
			// input source of document to convert
			InputSource inputSource;
			if (contentReader == null) {
				contentReader = ConverterReaderUtils.createReader(originalFileLocation);
			}
			
			inputSource = new InputSource(contentReader);
			
			StringWriter sw = new StringWriter();
			StreamResult res = new StreamResult(sw);

			// convert the document
			transformer.transform(new SAXSource(createXMLFilter(r), inputSource), res);

			// get the converted content
			convertedContent = sw.toString();

		}catch (TransformerException e) {
				throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		} catch (IOException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		}finally {
			// return the initial property of trasformerFactory
			if (property == null) {
				System.getProperties().remove(KEY_TRANSFORMER_FACTORY);
			} else {
				System.setProperty(KEY_TRANSFORMER_FACTORY, property);
			}
			
			if (contentReader != null) {
				try {
					contentReader.close();
				} catch (IOException e) {
					// Do nothing.
				}
			}
		}

		return new ConversionResult(convertedContent);
	}
	
	/**
	 * Create an XML filter for remove "class", "domains" and "DITAArchVersion" attributes.
	 * 
	 * @param reader The document reader.
	 * 
	 * @return	The xml filter.
	 */
	private XMLFilter createXMLFilter(XMLReader reader) {
		return  new XMLFilterImpl(reader) {
      /**
       * @see org.xml.sax.helpers.XMLFilterImpl#startPrefixMapping(java.lang.String, java.lang.String)
       */
      @Override
      public void startPrefixMapping(String prefix, String uri) throws SAXException {
        // Do not declare the "ditaarch" namespace prefix.
        if (!"ditaarch".equals(prefix)) {
          super.startPrefixMapping(prefix, uri);
        }
      }

      /**
       * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
       */
      @Override
      public void startElement(String uri, String localName, String qName,
          Attributes atts) throws SAXException {
        AttributesImpl attributes = new AttributesImpl();
        if (atts != null) {
          attributes.setAttributes(atts);
          for (int i = attributes.getLength() - 1; i >= 0; i--) {
            String attrName = attributes.getLocalName(i);
            // Remove "class", "domains" and "DITAArchVersion" attributes.
            if ("class".equals(attrName) || "domains".equals(attrName) 
                || "DITAArchVersion".equals(attrName)) {
              attributes.removeAttribute(i);
            }
          }
        }
        super.startElement(uri, localName, qName, attributes);
      }
    };
	}
}
