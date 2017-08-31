package com.oxygenxml.html.convertor.persister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import com.oxygenxml.html.convertor.view.ConvertorInteractor;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * Use WSOptionStorage for save content from GUI and load saved content
 * 
 * 
 * @author intern4
 *
 */
public class ContentPersisterImpl implements ContentPersister {

	@Override
	public void saveState(ConvertorInteractor interactor) {
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();

		// save state of CurrentFile radioButton
		optionsStorage.setOption(OptionKeys.CONVERT_CURRENT_FILE, String.valueOf(interactor.isConvertCurrentFile()));
		
		// save the list with files to convert
		optionsStorage.setOption(OptionKeys.OTHER_FILES_TO_CONVERT, join(";", interactor.getOtherFilesToConvert()));
	
		optionsStorage.setOption(OptionKeys.OUTPUT_DIRECTORY, interactor.getOutputDirectory());
	}
	
	@Override
	public void loadState(ConvertorInteractor interactor) {
		
		WSOptionsStorage optionsStorage = PluginWorkspaceProvider.getPluginWorkspace().getOptionsStorage();
		String value;

		// set rows in input table
		value = optionsStorage.getOption(OptionKeys.OTHER_FILES_TO_CONVERT, "");
		if (!value.isEmpty()) {
			// split value in list with Strings
			List<String> rowList = new ArrayList<String>(Arrays.asList(value.split(";")));
			interactor.setOtherFilesToConvert(rowList);
		}

		// set convertCurrent radioButton or convertOther radioButton
		value = optionsStorage.getOption(OptionKeys.CONVERT_CURRENT_FILE, "true");
		interactor.setConvertCurrentFile(Boolean.valueOf(value));
		
		//set the output directory
		value = optionsStorage.getOption(OptionKeys.OUTPUT_DIRECTORY, "");
		interactor.setOutputDirectory(value);
	}
	
	
	private String join(String delimiter, Collection<String> otherResourcesToCheck) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence cs : otherResourcesToCheck) {
			joiner.add(cs);
		}
		return joiner.toString();
	}
}