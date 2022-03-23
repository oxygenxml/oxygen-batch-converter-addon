package com.oxygenxml.resources.batch.converter.persister;

import com.oxygenxml.batch.converter.core.ConversionOptionTags;

/**
 * The option tags used in add-on.
 * 
 * @author cosmin_duna
 */
public class OptionTags extends ConversionOptionTags {
  
  /**
   * Private constructor.
   */
  private OptionTags() {
    // Avoid instantiation.
  }
  
  /**
   * Key for openConvertedDocument checkBox.
   */
   public static final String OPEN_CONVERTED_DOCUMENT = "open.converted.document.batch.converter";
   
   /**
    * The word styles map configuration.
    */
   public static final String WORD_STYLES_MAP_CONFIG = "word.styles.map.config";
   
   /**
    * Max heading level for creating DITA topics.
    */
   public static final String MAX_HEADING_LEVEL_FOR_TOPICS = "max.heading.leve.for.topics";
}
