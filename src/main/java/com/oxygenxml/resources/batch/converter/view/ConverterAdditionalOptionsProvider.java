package com.oxygenxml.resources.batch.converter.view;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;

/**
 * Provider for the additional options presented in the conversion dialog.
 *  
 * @author cosmin_duna
 */
public class ConverterAdditionalOptionsProvider {
  
  /**
   * Translator for internalisation
   */
  private static OxygenTranslator translator = new OxygenTranslator();
  
  /**
   * Map between options and options info.
   */
  private static Map<String, AdditionalOptionsInfo> addionalOptionToTranslationTag = new HashMap<>();
  static {
    addionalOptionToTranslationTag.put(OptionTags.CREATE_DITA_MAP_FROM_WORD, 
        new AdditionalOptionsInfo(MessageFormat.format(translator.getTranslation(Tags.CREATE_DITA_MAP_FROM_DOCUMENT_HEADINGS), "Word"), true));
    addionalOptionToTranslationTag.put(OptionTags.CREATE_DITA_MAP_FROM_MD,
        new AdditionalOptionsInfo(MessageFormat.format(translator.getTranslation(Tags.CREATE_DITA_MAP_FROM_DOCUMENT_HEADINGS), "Markdown"), true));
    addionalOptionToTranslationTag.put(OptionTags.CREATE_DITA_MAP_FROM_HTML,
        new AdditionalOptionsInfo(MessageFormat.format(translator.getTranslation(Tags.CREATE_DITA_MAP_FROM_DOCUMENT_HEADINGS), "HTML"), true));
    addionalOptionToTranslationTag.put(OptionTags.CREATE_DITA_MAP_FROM_DOCBOOK,
        new AdditionalOptionsInfo(MessageFormat.format(translator.getTranslation(Tags.CREATE_DITA_MAP_FROM_DOCUMENT_SECTIONS), "DocBook"), true));
    addionalOptionToTranslationTag.put(OptionTags.CREATE_SHORT_DESCRIPTION,
        new AdditionalOptionsInfo(translator.getTranslation(Tags.CREATE_SHORT_DESCRIPTION_FROM_PARAGRAPH), false));
    addionalOptionToTranslationTag.put(OptionTags.FILTER_DIV_ELEMENTS_FROM_HTML,
        new AdditionalOptionsInfo(translator.getTranslation(Tags.FILTER_DIV_ELEMENTS), false));
  }
  
  /**
   * Separator between aditional options
   */
  public static final String ADDITIONAL_OPTIONS_SEPARATOR = "separator_between_additional_options";
  
  /**
   * Private constructor.
   */
  private ConverterAdditionalOptionsProvider() {
    // Avoid instantiation.
  }
  
  /**
   * The the imposed options according to given conversion type.
   * @param convertionType The conversion type.
   * 
   * @return The additional imposed options.
   */
  public static final List<String> getImposedAdditionalOptions(String convertionType){
    List<String> options= new ArrayList<>();
    if( ConverterTypes.WORD_TO_DITA.equals(convertionType)) {
      options.add(OptionTags.CREATE_DITA_MAP_FROM_WORD);
    } else if(ConverterTypes.MD_TO_DITA.equals(convertionType)) {
      options.add(ADDITIONAL_OPTIONS_SEPARATOR);
      options.add(OptionTags.CREATE_DITA_MAP_FROM_MD);
      options.add(OptionTags.CREATE_SHORT_DESCRIPTION);
    } else if(ConverterTypes.HTML_TO_DITA.equals(convertionType)) {
      options.add(ADDITIONAL_OPTIONS_SEPARATOR);
      options.add(OptionTags.CREATE_DITA_MAP_FROM_HTML);
      options.add(OptionTags.FILTER_DIV_ELEMENTS_FROM_HTML);
    } else if(ConverterTypes.DOCBOOK_TO_DITA.equals(convertionType)) {
      options.add(OptionTags.CREATE_DITA_MAP_FROM_DOCBOOK);
    }
    return options;
  }
  
  /**
   * Get the translated message associated with the given additional option.
   * 
   * @param additionalOption The additional option.
   * 
   * @return The translation message associated with the option.
   */
  public static final String getTranslationMessageFor(String additionalOption) {
    String toRet = "";
    AdditionalOptionsInfo additionalOptionsInfo = addionalOptionToTranslationTag.get(additionalOption);
    if(additionalOptionsInfo != null) {
      toRet = additionalOptionsInfo.getMessage();
    }
    return toRet;
  }
  
  /**
   * Get the default boolean value the given additional option.
   * 
   * @param additionalOption The additional option.
   * 
   * @return The default boolean value the given additional option
   */
  public static final boolean getDefaultValueFor(String additionalOption) {
    boolean toRet = true;
    AdditionalOptionsInfo additionalOptionsInfo = addionalOptionToTranslationTag.get(additionalOption);
    if(additionalOptionsInfo != null) {
      toRet = additionalOptionsInfo.getDefaultValue();
    }
    return toRet;
  }
}
