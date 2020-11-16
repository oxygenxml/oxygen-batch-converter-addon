package com.oxygenxml.resources.batch.converter.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;

/**
 * Implementation of Converter for HTML to DITA.
 * 
 * @author cosmin_duna
 *
 */
public class HtmlToDitaConverter extends PipelineConverter{

  /**
   * Get the converters used in HTML to DITA conversion.
   */
  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    List<Converter> converters = new ArrayList<Converter>();
    converters.add( new HtmlToXhtmlConverter());
    converters.add(new HTML5Cleaner());
    converters.add(new XHTMLToDITAConverter());
    
    Boolean shouldCreateDitaMap = userInputsProvider.getAdditionalOptionValue(
        OptionTags.CREATE_DITA_MAP_FROM_HTML);
    if(shouldCreateDitaMap != null && shouldCreateDitaMap) {
      converters.add(new MapWithTopicsConverter());
    }
    return  converters.toArray(new Converter[0]);
  }
}
