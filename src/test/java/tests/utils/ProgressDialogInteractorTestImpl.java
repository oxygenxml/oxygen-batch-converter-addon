package tests.utils;

import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;

/**
 * Implementation of ProgressDialogInteractor for JUnits.
 * @author Cosmin Duna
 *
 */
public class ProgressDialogInteractorTestImpl implements ProgressDialogInteractor {

	@Override
	public void setDialogVisible(boolean state) {
	}

	@Override
	public void setNote(String note) {
		System.out.println("Note: "+ note);
	}

	@Override
	public void close() {
	}

}
