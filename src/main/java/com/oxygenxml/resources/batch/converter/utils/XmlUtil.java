package com.oxygenxml.resources.batch.converter.utils;

import java.util.regex.Pattern;

import ro.sync.basic.util.URLUtil;

/**
 * Util class for working with XML
 * 
 * @author cosmin_duna
 */
public final class XmlUtil {

  /**
   * Illegal chars used in an XML ID: anything else than 
   * alpha-numeric characters, dot, colon, underscore and minus.
   */
  private static final Pattern ILLEGAL_ID_CHARS_REGEX = Pattern.compile("[^A-Za-z0-9._-]");
  
  /**
   * Underscore and minus regex.
   */
  private static final Pattern MINUS_UNDERSCORE_REGEX = Pattern.compile("(_-_|-_|_-)");
  
  /**
   * Underscore and dot regex.
   */
  private static final Pattern DOT_UNDERSCORE_REGEX = Pattern.compile("(_\\._|\\._|_\\.)");
  
  /**
   * Consecutive minuses.
   */
  private static final Pattern CONSECUTIVE_MINUSES_REGEX = Pattern.compile("-+");
  
  /**
   * Consecutive dots.
   */
  private static final Pattern CONSECUTIVE_DOTS_REGEX = Pattern.compile("\\.+");
  
  /**
   * Consecutive underscores.
   */
  private static final Pattern CONSECUTIVE_UNDERSCORES_REGEX = Pattern.compile("_+");
  
  /**
   * Private constructor
   */
  private XmlUtil() {
    // Avoid instantiation
  }
  
  /**
   * Get the valid ID based on the given filename.
   * 
   * @param fileName The file name.
   * 
   * @return The ID.
   */
  public static String getValidIDFromName(String fileName) {
    String toReturn = URLUtil.uncorrect(fileName.trim());
    toReturn = ILLEGAL_ID_CHARS_REGEX.matcher(toReturn).replaceAll("_");
    toReturn = MINUS_UNDERSCORE_REGEX.matcher(toReturn).replaceAll("-");
    toReturn = DOT_UNDERSCORE_REGEX.matcher(toReturn).replaceAll(".");
    toReturn = CONSECUTIVE_UNDERSCORES_REGEX.matcher(toReturn).replaceAll("_");
    toReturn = CONSECUTIVE_MINUSES_REGEX.matcher(toReturn).replaceAll("-");
    toReturn = CONSECUTIVE_DOTS_REGEX.matcher(toReturn).replaceAll(".");
    char firstChar = toReturn.charAt(0);
    if ((firstChar >= '0' && firstChar <= '9') 
        || firstChar == '.' 
        || firstChar == '-') {
      // Avoid generating IDs that start with illegal characters
      toReturn = "_" + toReturn;
    }
    return toReturn;
  }
}
