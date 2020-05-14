package com.oxygenxml.resources.batch.converter.converters;

import java.util.ArrayList;
import java.util.List;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;

/**
 * Converter implementation for Word to DITA.
 * 
 * @author cosmin_duna
 */
public class WordToDITAConverter extends PipelineConverter {
	
  /**
   * Get the converters used in Word to DITA conversion.
   */
  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    List<Converter> converters = new ArrayList<Converter>();
    converters.add(new WordToXHTMLConverter());
    converters.add(new HtmlToDitaConverter());

    Boolean shoultCreateDitaMap = userInputsProvider.getAdditionalOptionValue(
        OptionTags.CREATE_DITA_MAP_FROM_WORD);
    if(shoultCreateDitaMap != null && shoultCreateDitaMap) {
      converters.add(new MapWithTopicsConverter());
    }

    return converters.toArray(new Converter[0]);
  }
}