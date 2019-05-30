package tests.utils;

import java.io.File;

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
	public void setNote(File note) {
		System.out.println("Note: "+ note.getAbsolutePath());
	}

	@Override
	public void close() {
	}

}
