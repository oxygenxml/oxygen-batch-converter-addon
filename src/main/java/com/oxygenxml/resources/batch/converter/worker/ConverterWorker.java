package com.oxygenxml.resources.batch.converter.worker;

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

public class ConverterWorker extends SwingWorker<Void, Void> implements ConvertorWorkerInteractor {

	private BatchConverterInteractor convertorInteractor;
	private OxygenStatusReporter oxygenStatusReporter;
	private Translator translator;
	private BatchConverter convertor;
	private ProgressDialogInteractor progressDialogInteractor;
	private OxygenProblemReporter oxygenProblemReporter;
	private String convertorType;

	public ConverterWorker(String convertorType, BatchConverterInteractor convertorInteractor,
			ProgressDialogInteractor progressDialogInteractor) {
		this.convertorType = convertorType;
		this.convertorInteractor = convertorInteractor;
		this.progressDialogInteractor = progressDialogInteractor;
		oxygenStatusReporter = new OxygenStatusReporter();
		oxygenProblemReporter = new OxygenProblemReporter();
		translator = new OxygenTranslator();
	}

	@Override
	protected Void doInBackground() {

		oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.PROGRESS_STATUS, ""));

		progressDialogInteractor.setDialogVisible(true);

		oxygenProblemReporter.deleteReportedProblems();
		
		convertor = new BatchConverterImpl(oxygenProblemReporter, progressDialogInteractor, this,
				new OxygenTransformerFactoryCreator());

		boolean isSuccesfully = convertor.convertFiles(convertorType,
				convertorInteractor.getInputFiles(), convertorInteractor.getOutputFolder());

		if (isSuccesfully) {
			oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.SUCCESS_STATUS, ""));
		} else {
			oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.FAIL_STATUS, ""));
		}

		progressDialogInteractor.close();

		return null;
	}

}
