package tests.utils;

import com.oxygenxml.resources.batch.converter.worker.ConvertorWorkerInteractor;

public class ConvertorWorkerInteractorTestImpl implements ConvertorWorkerInteractor {

	@Override
	public boolean isCancelled() {
		return false;
	}

}
