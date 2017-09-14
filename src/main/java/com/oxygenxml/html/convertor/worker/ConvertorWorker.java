package com.oxygenxml.html.convertor.worker;

import javax.swing.SwingWorker;

import com.oxygenxml.html.convertor.Convertor;
import com.oxygenxml.html.convertor.ConvertorInteractor;
import com.oxygenxml.html.convertor.ConvertorToDita;
import com.oxygenxml.html.convertor.ConvertorToXhtml;
import com.oxygenxml.html.convertor.FileType;
import com.oxygenxml.html.convertor.reporter.OxygenProblemReporter;
import com.oxygenxml.html.convertor.reporter.OxygenStatusReporter;
import com.oxygenxml.html.convertor.translator.OxygenTranslator;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;

public class ConvertorWorker extends SwingWorker<Void, Void> {

	private ConvertorInteractor convertorInteractor;
	private OxygenStatusReporter oxygenStatusReporter;
	private Translator translator;
	private Convertor convertor;
	
	public ConvertorWorker(ConvertorInteractor convertorInteractor) {
		this.convertorInteractor = convertorInteractor;
		oxygenStatusReporter = new OxygenStatusReporter();
		translator = new OxygenTranslator();
	}
	
	@Override
	protected Void doInBackground()  {
		
		oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.PROGRESS_STATUS));
		
		if(FileType.XHTML_TYPE.equals(convertorInteractor.getOutputType())){
			//it's selected xhtml output
			convertor = new ConvertorToXhtml(new OxygenProblemReporter());
		}
		else {
			//it's selected dita output
			convertor = new ConvertorToDita(new OxygenProblemReporter());
		}
		
		boolean isSuccesfully = convertor.convertFiles(convertorInteractor.getInputFiles(), convertorInteractor.getOutputFolder());
		
		if(isSuccesfully){
			oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.SUCCESS_STATUS));
		}else{
			oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.FAIL_STATUS));
		}
		
		return null;
	}

}
