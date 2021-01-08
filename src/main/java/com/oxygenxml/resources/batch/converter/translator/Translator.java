package com.oxygenxml.resources.batch.converter.translator;

/**
 * Interface used for internationalization.
 * @author Cosmin Duna
 *
 */
public interface Translator {
	
	/**
	 * Get the translation from the given key with the given suffix.
	 * 
	 * @param key          The message key.
	 * 
	 * @return The message.
	 */
	public String getTranslation(String key);
}
