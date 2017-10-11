package com.oxygenxml.resources.batch.converter.worker;

/**
 * Converter worker interactor.
 * @author intern4
 *
 */
public interface ConvertorWorkerInteractor {

	/**
	 * Check if worker is cancelled.
	 * @return <code>true</code> if is cancelled, <code>false</code> otherwise. 
	 */
	public boolean isCancelled();
}
