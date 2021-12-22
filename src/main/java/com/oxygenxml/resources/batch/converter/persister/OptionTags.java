package com.oxygenxml.resources.batch.converter.persister;

/**
 * The option tags used in add-on.
 * 
 * @author cosmin_duna
 */
public class OptionTags {
  
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
    * Option used to configure if the result of the conversion from Word
    *  will be a DITA Map or a DITA Topic.
    */
   public static final String CREATE_DITA_MAP_FROM_WORD = "create.dita.map.from.word";
   
   /**
    * Option used to configure if the result of the conversion from Markdown
    *  will be a DITA Map or a DITA Topic.
    */
   public static final String CREATE_DITA_MAP_FROM_MD = "create.dita.map.from.md";
   
   /**
    * Option used to configure if the result of the conversion from HTML
    *  will be a DITA Map or a DITA Topic.
    */
   public static final String CREATE_DITA_MAP_FROM_HTML = "create.dita.map.from.html";
   
   /**
    * Option used to configure if the result of the conversion from DocBook
    *  will be a DITA Map or a DITA Topic.
    */
   public static final String CREATE_DITA_MAP_FROM_DOCBOOK = "create.dita.map.from.docbook";
   
   /**
    * Create short description from first paragraph 
    */
   public static final String CREATE_SHORT_DESCRIPTION = "create.short.description";
   
   /**
    * The word styles map configuration.
    */
   public static final String WORD_STYLES_MAP_CONFIG = "word.styles.map.config";
   
   /**
    * Max heading level for creating DITA topics.
    */
   public static final String MAX_HEADING_LEVEL_FOR_TOPICS = "max.heading.leve.for.topics";
}
