package com.oxygenxml.resources.batch.converter.translator;

/**
 * Interface used for internationalization.
 * @author intern4
 *
 */
public interface Translator {
	
	/**
	 * Get the translation from the given key;
	 * @param key the key.
	 * @return the translation.
	 */
	public String getTranslation(String key, String convertorType);
}
