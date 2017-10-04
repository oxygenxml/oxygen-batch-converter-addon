package tests.utils;

import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;

public class ProgressDialogInteractorTestImpl implements ProgressDialogInteractor {

	@Override
	public void setDialogVisible(boolean state) {

	}

	@Override
	public void setNote(String note) {
		System.out.println("Node: "+ note);
	}

	@Override
	public void close() {
	}

}
