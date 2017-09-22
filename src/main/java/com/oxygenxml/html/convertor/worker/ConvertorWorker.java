package com.oxygenxml.html.convertor.worker;


import javax.swing.SwingWorker;

import com.oxygenxml.html.convertor.Convertor;
import com.oxygenxml.html.convertor.ConvertorInteractor;
import com.oxygenxml.html.convertor.ConvertorToDita;
import com.oxygenxml.html.convertor.ConvertorToXhtml;
import com.oxygenxml.html.convertor.FileType;
import com.oxygenxml.html.convertor.reporter.OxygenProblemReporter;
import com.oxygenxml.html.convertor.reporter.OxygenStatusReporter;
import com.oxygenxml.html.convertor.reporter.ProblemReporter;
import com.oxygenxml.html.convertor.reporter.ProgressDialogInteractor;
import com.oxygenxml.html.convertor.translator.OxygenTranslator;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

public class ConvertorWorker extends SwingWorker<Void, Void> implements ConvertorWorkerInteractor {

	private ConvertorInteractor convertorInteractor;
	private OxygenStatusReporter oxygenStatusReporter;
	private Translator translator;
	private Convertor convertor;
	private ProgressDialogInteractor progressDialogInteractor;
	
	public ConvertorWorker( ConvertorInteractor convertorInteractor, ProgressDialogInteractor progressDialogInteractor) {
		this.convertorInteractor = convertorInteractor;
		this.progressDialogInteractor = progressDialogInteractor;
		oxygenStatusReporter = new OxygenStatusReporter();
		translator = new OxygenTranslator();
	}
	
	@Override
	protected Void doInBackground()  {
		
		System.out.println("worker1");
		
		oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.PROGRESS_STATUS,""));
		
		progressDialogInteractor.setDialogVisible(true);
		
		System.out.println("worker2");
		
			System.out.println("worker2.2");
			try {
				convertor = new ConvertorToXhtml(new OxygenProblemReporter(), progressDialogInteractor, this);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		System.out.println("worker3");
		
		boolean isSuccesfully = convertor.convertFiles(convertorInteractor.getInputFiles(), convertorInteractor.getOutputFolder());
		
		System.out.println("worker4");
		
		if(isSuccesfully){
			oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.SUCCESS_STATUS,""));
		}else{
			oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.FAIL_STATUS, ""));
		}
		
		progressDialogInteractor.close();
		
		return null;
	}

	
}
