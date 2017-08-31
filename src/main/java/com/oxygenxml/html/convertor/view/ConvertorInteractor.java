package com.oxygenxml.html.convertor.view;

import java.util.List;

public interface ConvertorInteractor {

	public boolean isConvertCurrentFile();

	public void setConvertCurrentFile(boolean state);

	public void setOtherFilesToConvert(List<String> otherFiles);

	public List<String> getOtherFilesToConvert();

	public void setOutputDirectory(String outputDir);

	public String getOutputDirectory();
}
