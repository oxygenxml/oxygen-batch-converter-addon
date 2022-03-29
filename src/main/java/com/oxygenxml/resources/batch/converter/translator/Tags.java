package com.oxygenxml.resources.batch.converter.translator;

/**
 * Tags used for persistence and internationalization.
 * @author Cosmin Duna
 *
 */
public class Tags {
	
	/**
	 * Private constructor.
	 */
	 private Tags() {
	    throw new IllegalStateException("Utility class");
	  }
	
	/**
	 * The text of JMenu that contains converters.
	 */
	public static final String MENU_TEXT = "Menu_Text_Conv";
	
	/**
	 * Text for menu item.
	 */
	public static final String MENU_ITEM_TEXT = "Menu_Item_Text_Conv";
	
	
	/**
	 * Label for add input files.
	 */
	public static final String ADD_INPUT_FILES_LABEL = "Add_Input_Label_Conv";
	
	 /**
   * Label for confluence information message.
   */
  public static final String ADD_INPUT_FILES_LABEL_CONFLUENCE = "Add_Input_Label_Conv_Confluence";

	/**
	 * Add file button of table.
	 */
	public static final String ADD_FILE_TABLE = "Add_File_Table_Button_Conv"; 
	
	/**
	 * Add folder button of table
	 */
	public static final String ADD_FOLDER_TABLE = "Add_Folder_Table_Button_Conv";
	
	/**
	 * Remove button of table
	 */
	public static final String REMOVE_TABLE = "Remove_Table_Button_Conv";

	/**
	 * Label for select output type.
	 */
	public static final String SELECT_OUTPUT_TYPE_LABEL = "Select_Output_Type_Label_Conv";
	
	
	/**
	 * Label for add output folder.
	 */
	public static final String ADD_OUTPUT_FOLDER_LABEL = "Add_Output_Label_Conv";

	/**
	 * Label for add output folder.
	 */
	public static final String OPEN_FILE_CHECK_BOX = "Open_File_Check_Box";
	
	
	/**
	 * Button for start the conversion.
	 */
	public static final String CONVERT_BUTTON = "Convert_Button_Conv";
	
	
	
	/**
	 * Reported progress status. 
	 */
	public static final String PROGRESS_STATUS = "Progress_Status_Conv"; 
	
	/**
	 * Reported fail status.
	 */
	public static final String FAIL_STATUS = "Fail_Status_Conv";
	
	/**
	 * Reported success status.
	 */
	public static final String SUCCESS_STATUS = "Success_Status_Conv";

	/**
	 * Message in progress dialog. 
	 */
	public static final String PROGRESS_DIALOG_MESSAGE = "Progress_Dialog_Message_Conv";

	/**
	 * Message when output folder path is empty. 
	 */
	public static final String EMPTY_OUTPUT_MESSAGE = "Empty_Output_Message_Conv";
	
  /**
   * Option used to configure if the result will be a DITA Map or a DITA Topic.
   * 
   *  en: Create DITA Maps from {0} documents containing multiple headings 
   */
  public static final String CREATE_DITA_MAP_FROM_DOCUMENT_HEADINGS = "create_dita_map_from_documents_with_multiple_headings";
  
  /**
   * Option used to configure if the result will be a DITA Map or a DITA Topic.
   * 
   *  en: Create DITA Maps from {0} documents containing multiple sections. 
   */
  public static final String CREATE_DITA_MAP_FROM_DOCUMENT_SECTIONS = "create_dita_map_from_documents_with_multiple_sections";
  
  /**
   *  en: The configuration file was not found.
   */
  public static final String CONFIG_FILE_NOT_FOUND = "Config_file_not_found";
  
  /**
   * en: Create short description from the first paragraph
   */
  public static final String CREATE_SHORT_DESCRIPTION_FROM_PARAGRAPH = "Create_short_description_from_paragraph";
  
  /**
   * en: Additional conversions
   */
  public static final String ADDITIONAL_CONVERSIONS = "Additional_conversions";
  
  /**
   * en: Import
   */
  public static final String IMPORT = "Import";
  
  /**
   * en: Word element
   */
  public static final String WORD_ELEMENT = "word_element";
  /**
   * en: Word style
   */
  public static final String WORD_STYLE = "word_style";
  /**
   * en: HTML elements
   */
  public static final String HTML_ELEMENTS = "html_elements";
  /**
   * en: Word styles mapping
   */
  public static final String WORD_STYLES_MAPPING = "word_styles_mapping";
  /**
   * en: Conversions from Word documents (.docx) can be configured by mapping Word elements and styles to the corresponding HTML element.
   */
  public static final String WORD_CONFIG_DESCRIPTION = "word_config_description";
  /**
   * en: New
   */
  public static final String NEW = "new";
  /**
   * en: Edit
   */
  public static final String EDIT = "edit";
  /**
   * en: Delete
   */
  public static final String DELETE = "delete";
  /**
   * en: Export
   */
  public static final String EXPORT = "export";
  /**
   * en: Help
   */
  public static final String HELP = "help";
  /**
   * en: Unrecognized '{0}' style for '{1}' Word element..
   */
  public static final String UNRECOGNIZE_STYLES_FOR_WORD_ELEMENT = "unrecognize_style_for_word_element";
  
  /**
   * en: Unrecognized '{0}' style ID for '{1}' Word element.
   */
  public static final String UNRECOGNIZE_STYLES_ID_FOR_WORD_ELEMENT = "unrecognize_style_id_for_word_element";
  
  /**
   * en: You can configure the mapping between styles and elements in the Batch Documents Converter preferences page.
   */
  public static final String CONFIG_WORD_MAPPING_IN_PREFERENCES_PAGE = "configure_word_mapping_in_preferences_page";
  
  /**
   * en: Convert to DITA
   */
  public static final String CONVERT_TO_DITA = "convert_to_DITA";
  /**
   * en: Maximum heading level for creating topics
   */
  public static final String MAX_HEADING_LEVEL_FOR_CREATING_TOPICS = "max_heading_level_for_creating_topics";

  /**
   * en: Conversion options.
   */
  public static final String CONVERSION_OPTIONS = "conversion_options";
  
  /**
   * Option used to configure if the result will be a DITA Map or a DITA Topic.
   * 
   *  en: Create DITA map. 
   */
  public static final String CREATE_DITA_MAP = "create_dita_map";
}