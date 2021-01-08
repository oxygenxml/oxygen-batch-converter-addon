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
   *  en: Create DITA Maps for Word documents containing multiple sections 
   */
  public static final String CREATE_DITA_MAP_OPTION_WORD = "Create_Dita_Map_Option_Word";
  
  /**
   * Option used to configure if the result will be a DITA Map or a DITA Topic.
   * 
   *  en: Create DITA Maps for {0} documents containing multiple sections 
   */
  public static final String CREATE_DITA_MAP_OPTION_FOR = "Create_Dita_Map_Option_For";
  
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
  
}