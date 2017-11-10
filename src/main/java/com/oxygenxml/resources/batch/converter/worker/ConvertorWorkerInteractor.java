package com.oxygenxml.resources.batch.converter.worker;

/**
 * Converter worker interactor.
 * @author Cosmin Duna
 *
 */
public interface ConvertorWorkerInteractor {

	/**
	 * Check if worker is cancelled.
	 * @return <code>true</code> if is cancelled, <code>false</code> otherwise. 
	 */
	public boolean isCancelled();
}
