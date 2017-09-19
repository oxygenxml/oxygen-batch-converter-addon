package com.oxygenxml.html.convertor.reporter;

public interface ProgressDialogInteractor {
	
	public void setDialogVisible(boolean state);
	
	public void setNote(String note);
	
	public void close();
}
