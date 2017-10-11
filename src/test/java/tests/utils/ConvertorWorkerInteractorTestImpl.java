package tests.utils;

import com.oxygenxml.resources.batch.converter.worker.ConvertorWorkerInteractor;

/**
 * Implementation of ConverterWorkerInteractor  for JUnits
 * @author intern4
 *
 */
public class ConvertorWorkerInteractorTestImpl implements ConvertorWorkerInteractor {

	@Override
	public boolean isCancelled() {
		return false;
	}

}
