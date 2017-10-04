package com.oxygenxml.resources.batch.converter.worker;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.reporter.OxygenProblemReporter;
import com.oxygenxml.resources.batch.converter.reporter.OxygenStatusReporter;
import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.trasformer.OxygenTransformerFactoryCreator;

import ro.sync.util.URLUtil;

public class ConvertorWorker extends SwingWorker<Void, Void> implements ConvertorWorkerInteractor {

	private BatchConverterInteractor convertorInteractor;
	private OxygenStatusReporter oxygenStatusReporter;
	private Translator translator;
	private BatchConverter convertor;
	private ProgressDialogInteractor progressDialogInteractor;
	private OxygenProblemReporter oxygenProblemReporter;
	private String convertorType;
	
	public ConvertorWorker(String convertorType, BatchConverterInteractor convertorInteractor, ProgressDialogInteractor progressDialogInteractor) {
		this.convertorType = convertorType;
		this.convertorInteractor = convertorInteractor;
		this.progressDialogInteractor = progressDialogInteractor;
		oxygenStatusReporter = new OxygenStatusReporter();
		oxygenProblemReporter = new OxygenProblemReporter();
		translator = new OxygenTranslator();
	}
	
	@Override
	protected Void doInBackground()  {
		
		oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.PROGRESS_STATUS,""));

		progressDialogInteractor.setDialogVisible(true);
		
		convertor = new BatchConverterImpl(oxygenProblemReporter, progressDialogInteractor, 
				this, new OxygenTransformerFactoryCreator());
		
		boolean isSuccesfully = convertor.convertFiles(convertorType, convertAndCorrectToURL(convertorInteractor.getInputFiles()), 
				convertorInteractor.getOutputFolder());
		
		if(isSuccesfully){
			oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.SUCCESS_STATUS,""));
		}else{
			oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.FAIL_STATUS, ""));
		}
		
		progressDialogInteractor.close();
		
		return null;
	}

	
	private List<URL> convertAndCorrectToURL(List<String> list){
		List<URL> toReturn = new ArrayList<URL>();
		String currentElement;
		URL currentUrlElement;
		
		int size = list.size();
		for (int i = 0; i < size; i++) {
			 currentElement = list.get(i);
			try {
				currentUrlElement = URLUtil.correct(new File(currentElement));
				toReturn.add(currentUrlElement);
			}
			catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//TODO poate raportez
			}
		}
		
		return toReturn;
	}
	
}
