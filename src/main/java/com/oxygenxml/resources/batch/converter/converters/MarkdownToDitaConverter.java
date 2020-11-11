package com.oxygenxml.resources.batch.converter.converters;

import java.util.ArrayList;
import java.util.List;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;


/**
 * Converter implementation for Markdown to DITA.
 * 
 * @author Cosmin Duna
 *
 */
public class MarkdownToDitaConverter extends PipelineConverter {

  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    List<Converter> converters = new ArrayList<Converter>();
    converters.add(new MarkdownToDitaInternalConverter());
    Boolean shoultCreateDitaMap = userInputsProvider.getAdditionalOptionValue(
        OptionTags.CREATE_DITA_MAP_FROM_MD);
    if(shoultCreateDitaMap != null && shoultCreateDitaMap) {
      converters.add(new MapWithTopicsConverter());
    }
    return  converters.toArray(new Converter[0]);
  }
}
