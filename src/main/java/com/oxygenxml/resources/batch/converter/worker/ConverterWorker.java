package com.oxygenxml.resources.batch.converter.worker;

import java.io.File;

import javax.swing.SwingUtilities;
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

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.project.ProjectController;

/**
 * Worker that execute the conversion.
 * @author intern4
 *
 */
public class ConverterWorker extends SwingWorker<Void, Void> implements ConvertorWorkerInteractor {
	/**
	 * Batch converter interactor.
	 */
	private BatchConverterInteractor convertorInteractor;
	/**
	 * Status reporter.
	 */
	private OxygenStatusReporter oxygenStatusReporter;
	/**
	 * Translator
	 */
	private Translator translator;
	
	/**
	 * The converter
	 */
	private BatchConverter convertor;
	/**
	 * Progress dialog interactor.
	 */
	private ProgressDialogInteractor progressDialogInteractor;
	/**
	 * Problem reporter.
	 */
	private OxygenProblemReporter oxygenProblemReporter;
	/**
	 * Converter type
	 */
	private String converterType;

	
	
	/**
	 * Constructor
	 * @param converterType The type of converter.
	 * @param convertorInteractor A converter interactor.
	 * @param progressDialogInteractor A progress dialog interactor.
	 */
	public ConverterWorker(String converterType, BatchConverterInteractor convertorInteractor,
			ProgressDialogInteractor progressDialogInteractor) {
		this.converterType = converterType;
		this.convertorInteractor = convertorInteractor;
		this.progressDialogInteractor = progressDialogInteractor;
		oxygenStatusReporter = new OxygenStatusReporter();
		oxygenProblemReporter = new OxygenProblemReporter();
		translator = new OxygenTranslator();
	}


	/**
	 * Convert the files.
	 * Note: this method is executed in a background thread.
	 */
	@Override
	protected Void doInBackground() {
		//report the progress status
		oxygenStatusReporter.reportStatus(translator.getTranslation(Tags.PROGRESS_STATUS, ""));

		//set the progress dialog visible 
		progressDialogInteractor.setDialogVisible(true);

		//delete reported problems from other conversion
		oxygenProblemReporter.deleteReportedProblems();

		//create the converter
		convertor = new BatchConverterImpl(oxygenProblemReporter, oxygenStatusReporter, progressDialogInteractor, this,
				new OxygenTransformerFactoryCreator());

		//convert the files
		convertor.convertFiles(converterType,	convertorInteractor.getInputFiles(), convertorInteractor.getOutputFolder(),
				convertorInteractor.mustOpenConvertedFiles());

		//refresh the output folder from the project manager.
		PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();		
		final ProjectController projectManager = ((StandalonePluginWorkspace)pluginWorkspace).getProjectManager();
		
		//refresh the project manager parent directory
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				projectManager.refreshFolders(new File[]{convertorInteractor.getOutputFolder().getParentFile()});
				
			}
		});
		
		
		//close the progress dialog
		progressDialogInteractor.close();

		return null;
	}
}
