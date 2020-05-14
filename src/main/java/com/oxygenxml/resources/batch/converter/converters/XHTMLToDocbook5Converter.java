package com.oxygenxml.resources.batch.converter.converters;

import javax.xml.transform.Transformer;

import com.oxygenxml.resources.batch.converter.doctype.Doctypes;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Converter implementation for XHTML to Docbook5
 * @author Cosmin Duna
 *
 */
public class XHTMLToDocbook5Converter extends StylesheetConverter {
	/**
   *  Attributes and values from the article root element.
   */
  private static final String[] ROOT_ATTRIBUTES = new String[] {"xmlns=\"http://docbook.org/ns/docbook\"",
  		"xmlns:xlink=\"http://www.w3.org/1999/xlink\"",
  		"version=\"5.0\""};
  /**
   * The local name of root element.
   */
  private static final String ROOT_ELEMENT = "article";
  
  private static final String MATHML_MODELS = "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\r\n" + 
  		"<?xml-model href=\"http://www.oxygenxml.com/docbook/xml/5.0/rng/dbmathml.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n";
  
  /**
   * @see StylesheetConverter#getStylesheetPath()
   */
  @Override
  public String getStylesheetPath() {
    // get the XSL path from oxygen
    String xslPath = PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess().expandEditorVariables("${frameworks}",
        null);
    return xslPath + "/docbook/resources/xhtml2db5Driver.xsl";
  }
  
  /**
   * @see StylesheetConverter#setTransformationParam(Transformer)
   */
  @Override
  public void setTransformationParam(Transformer transformer) {
    // set the parameter of transformer
    transformer.setParameter("context.path.names", ROOT_ELEMENT);
    transformer.setParameter("context.path.uris", "http://docbook.org/ns/docbook");
    transformer.setParameter("replace.entire.root.contents", Boolean.TRUE);
    transformer.setParameter("wrapMultipleSectionsInARoot", Boolean.TRUE);
  }
  
  /**
   * @see StylesheetConverter#processConversionResult(String)
   */
  @Override
  public ConversionResult processConversionResult(String docbookContent) {
    final ConversionResult conversionResult;
    docbookContent = updateArticleRootAttributes(docbookContent);
    
    if(docbookContent.contains("mml:math")) {
      docbookContent = addMathmlModels(docbookContent);
      conversionResult = new ConversionResult(
          docbookContent, Doctypes.DOCTYPE_PUBLIC_DB5_MAHTML, Doctypes.DOCTYPE_SYSTEM_DB5_MATHML);
    } else {
      conversionResult = new ConversionResult(docbookContent);
    }
    return conversionResult;
  }
  
	/**
	 * Add mathml models to given document content.
	 * 
	 * @param documentContent The document content.
	 * 
	 * @return A document that contains mathml models.
	 */
	private String addMathmlModels(String documentContent) {
		int indexOfRootTag = documentContent.indexOf(ROOT_ELEMENT);
		if(indexOfRootTag != -1){
			documentContent = documentContent.substring(0, indexOfRootTag - 1) 
					+ MATHML_MODELS + documentContent.substring(indexOfRootTag - 1);
 		}
		return documentContent;
	}
	
	/**
	 * Add the root attributes if those don't exist.
	 * @param documentContent The document content to be updated.
	 * @return A document that contains all root attributes.
	 */
	private String updateArticleRootAttributes(String documentContent) {
		int indexOfRootTag = documentContent.indexOf(ROOT_ELEMENT);
		if(indexOfRootTag != -1){
			int rootCloseTag = documentContent.indexOf('>');
			String rootContent = documentContent.substring(0, rootCloseTag);
			int nuOfAttributes = ROOT_ATTRIBUTES.length;
			StringBuilder attributesBuilder = new StringBuilder();
			for (int i = 0; i < nuOfAttributes; i++) {
				String currentAttr = ROOT_ATTRIBUTES[i];
				if(!rootContent.contains(currentAttr)) {
					attributesBuilder.append(" ").append(currentAttr);
				}
			}
			if(attributesBuilder.length() > 0) {
				attributesBuilder.insert(0, documentContent.substring(0, rootCloseTag));
				attributesBuilder.append(documentContent.substring(rootCloseTag));
				documentContent = attributesBuilder.toString();
			}
		}
		return documentContent;
	}
}
