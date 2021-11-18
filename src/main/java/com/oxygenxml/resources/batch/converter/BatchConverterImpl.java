package com.oxygenxml.resources.batch.converter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.zwobble.mammoth.internal.conversion.UnknownStylesReporter;
import org.zwobble.mammoth.internal.documents.Style;

import com.oxygenxml.batch.converter.core.ConversionFormatUtil;
import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.converters.ConversionResult;
import com.oxygenxml.batch.converter.core.converters.Converter;
import com.oxygenxml.batch.converter.core.converters.ConverterCreator;
import com.oxygenxml.batch.converter.core.extensions.ExtensionGetter;
import com.oxygenxml.batch.converter.core.transformer.TransformerFactoryCreator;
import com.oxygenxml.batch.converter.core.utils.ConverterFileUtils;
import com.oxygenxml.batch.converter.core.word.styles.WordStyleMapLoader;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinter;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinterCreater;
import com.oxygenxml.resources.batch.converter.printer.StyleSourceGetter;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.reporter.ResultsUtil;
import com.oxygenxml.resources.batch.converter.reporter.StatusReporter;
import com.oxygenxml.resources.batch.converter.transformer.OxygenTransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.worker.ConverterStatusReporter;
import com.oxygenxml.resources.batch.converter.worker.ConvertorWorkerInteractor;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.results.ResultsManager;
import ro.sync.exml.workspace.api.results.ResultsManager.ResultType;

/**
 * Batch converter implementation.
 * 
 * @author Cosmin Duna
 *
 */
public class BatchConverterImpl implements BatchConverter {
  /**
   * Logger
   */
   private static final Logger logger = Logger.getLogger(BatchConverterImpl.class);
   
   /**
    * Problem reporter.
    */
   private ProblemReporter problemReporter;

   /**
    * Status reporter.
    */
   private StatusReporter oxyStatusReporter;

   /**
    * Progress dialog interactor.
    */
   private ConverterStatusReporter converterStatusReporter;
   /**
    * Worker interactor.
    */
   private ConvertorWorkerInteractor workerInteractor;
   /**
    * Transformer creator.
    */
   private TransformerFactoryCreator transformerFactoryCreator;

   /**
    * Translator
    */
   private Translator translator = new OxygenTranslator();

   /**
    *  The flag of conversion. <code>True</code> when conversion was successfully, <code>false</code> otherwise.
    */
   private boolean isSuccessfully ;

   /**
    * Number of converted files
    */
   private int noOfConvertedFiles;

   /**
    * Number of files that cannot be converted.
    */
   private int failedFile;
	 
	  /**
	   * Constructor.
	   * 
	   * @param problemReporter
	   *          Problem reporter.
	   * @param progressDialogInteractor
	   *          Progress dialog interactor.
	   * @param workerInteractor
	   *          Worker interactor.
	   * @param transformerFactoryCreator
	   *          Transformer factory creator.
	   */
		// This constructor is used from the batch converter command line script from Oxygen. You must not change the signature.
	  public BatchConverterImpl(ProblemReporter problemReporter, StatusReporter statusReporter, final ProgressDialogInteractor progressDialogInteractor) {
	    this(problemReporter, statusReporter, new ConverterStatusReporter() {
        
	      @Override
	      public void conversionStarted() {
	        // Do nothing
	      }
        @Override
        public void conversionInProgress(File inputFile) {
          progressDialogInteractor.conversionInProgress(inputFile);
        }
        
        @Override
        public void conversionFinished(List<File> resultedDocuments, File outputDir) {
          // Do nothing
        }
      }, new ConvertorWorkerInteractor() {
        @Override
        public boolean isCancelled() {
          return false;
        }
      }, new OxygenTransformerFactoryCreator());
	    
	  }
		
	/**
	 * Constructor.
	 * 
	 * @param problemReporter
	 *          Problem reporter.
	 * @param progressDialogInteractor
	 *          Progress dialog interactor.
	 * @param workerInteractor
	 *          Worker interactor.
	 * @param transformerFactoryCreator
	 *          Transformer factory creator.
	 */
	public BatchConverterImpl(ProblemReporter problemReporter, StatusReporter statusReporter,
			ConverterStatusReporter converterStatusReporter, ConvertorWorkerInteractor workerInteractor,
			TransformerFactoryCreator transformerFactoryCreator) {
		this.problemReporter = problemReporter;
		this.oxyStatusReporter = statusReporter;
		this.converterStatusReporter = converterStatusReporter;
		this.workerInteractor = workerInteractor;
		this.transformerFactoryCreator = transformerFactoryCreator;
	}
	 
  /**
  * Convert the given input files and write them in given output folder according to given convertorType.
  *
  * @param inputFormat      The input format to check.
  * @param outputFormat      The output format to check.
  * @param inputsProvider    Provider for the user inputs like input files, output directory and another options.
  * 
  * @return <code>true</code> if the process of conversion was finished successfully, <code>false</code> otherwise.
  */
	@Override
	public List<File> convertFiles(String inputFormat, String outputFormat, UserInputsProvider inputsProvider) {
	  String converterType = ConversionFormatUtil.getConverterType(inputFormat, outputFormat);
	  List<File> convertedFiles = new ArrayList<>();
	  if(converterType != null) {
	    convertedFiles = convertFiles(converterType, inputsProvider);
	  } else {
	     problemReporter.reportProblem(new Exception(
	         "The " + inputFormat + " to " + outputFormat + " conversion format is not supported."), null);
	  }
	  return convertedFiles;
	}
	
	/**
	 * Convert the given input files and write them in given output folder
	 * according to given convertorType.
	 * 
	 * @param convertorType        The converter type.
   * @param inputsProvider       Provider for the user inputs like input files, output directory and another options.

	 * @return <code>true</code> if the process of conversion was finished
	 *         successfully, <code>false</code> otherwise.
	 */
	@Override
	public List<File> convertFiles(String converterType, UserInputsProvider inputsProvider) {
		List<File> convertedFiles = new ArrayList<File>();
		isSuccessfully = true;
		noOfConvertedFiles = 0;
		failedFile = 0;
				
		if(logger.isDebugEnabled()) {
		  logger.debug("Converter type: " + converterType);
		}
		
		Converter converter = ConverterCreator.create(converterType);
		ContentPrinter contentPrinter = ContentPrinterCreater.create(converterType);
		
		if(ConverterTypes.WORD_TO_XHTML.equals(converterType) || ConverterTypes.WORD_TO_DITA.equals(converterType)){
		  PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
		  if(pluginWorkspace != null) {
		    WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
		    String wordStylesMapConfig = optionsStorage.getOption(OptionTags.WORD_STYLES_MAP_CONFIG, "");
		    if (!wordStylesMapConfig.isEmpty()) {
		      WordStyleMapLoader.imposeStyleMap(wordStylesMapConfig);
		    }
		    
        ResultsManager resultsManager = pluginWorkspace.getResultsManager();
        if (resultsManager != null) {
          WordStyleMapLoader.setUnknownStylesReporter(new UnknownStylesReporter() {
            @Override
            public void reportUnknownStyle(String element, Style style, Optional<Path> documentPath) {
              StringBuilder message = new StringBuilder();
              if (style.getName().isPresent()) {
                message.append(MessageFormat.format(translator.getTranslation(Tags.UNRECOGNIZE_STYLES_FOR_WORD_ELEMENT), style.getName().get(), element));
              } else {
                message.append(MessageFormat.format(translator.getTranslation(Tags.UNRECOGNIZE_STYLES_ID_FOR_WORD_ELEMENT), style.getStyleId(), element));
              }
              message.append(" ").append(translator.getTranslation(Tags.CONFIG_WORD_MAPPING_IN_PREFERENCES_PAGE));
              
              String location = null;
              if(documentPath.isPresent()) {
                try {
                  location = documentPath.get().toUri().toURL().toExternalForm();
                } catch (MalformedURLException e) {
                  logger.debug(e.getMessage(), e);
                }
              }
              resultsManager.addResult(ResultsUtil.BATCH_CONVERTER_RESULTS_TAB_KEY,
                  new DocumentPositionedInfo(DocumentPositionedInfo.SEVERITY_WARN, message.toString(), location),
                  ResultType.PROBLEM, true, true);
            }
          });
        }
      }
    } 
		
		//make the output directory if it doesn't exist
		File outputFolder = inputsProvider.getOutputFolder();
    if(!outputFolder.exists()){
		  inputsProvider.getOutputFolder().mkdirs();
		}
		
		if (converter != null) {
			// iterate over files
		  List<File> inputFiles = inputsProvider.getInputFiles();
			int size = inputFiles.size();
			for (int i = 0; i < size; i++) {

				// check if worker was interrupted
				if (workerInteractor.isCancelled()) {
					isSuccessfully = false;
					failedFile += size-i;
					// break the loop
					break;
				}

				// get the current file.
				File currentFile = inputFiles.get(i);
				if(logger.isDebugEnabled()) {
					logger.debug("File to convert: " + currentFile);
				}
				
				converterStatusReporter.conversionInProgress(currentFile);

				//generate the output file.
				File outputFile = ConverterFileUtils.getUniqueOutputFile(currentFile, 
            ExtensionGetter.getOutputExtension(converterType), outputFolder);
				
				//convert and print the current file.
				convertedFiles.add(
				    convertAndPrintFile(currentFile, outputFile, converter, contentPrinter, converterType, inputsProvider));
			}

		} else {
			isSuccessfully = false;
			failedFile = inputsProvider.getInputFiles().size();
		}

		//report the finish status
		oxyStatusReporter.conversionFinished(noOfConvertedFiles, failedFile);
		return convertedFiles;
	}
	
	
	/**
	 * Convert the given file using the given converter and print the converted result using the given contentPrinter. 
	 * @param file            The file.
	 * @param outputFile      The outputFile
	 * @param converter       The converter. 
	 * @param contentPrinter  The contentPrinter.
	 * @param converterType   The converterType.
   * @param inputsProvider  Provider for the user inputs like input files, output directory and another options.
   * 
   * @return The converted file, or <code>null</code> if conversion failed
 	 */
	private File convertAndPrintFile(File file, File outputFile, Converter converter, ContentPrinter contentPrinter,
			String converterType, UserInputsProvider inputsProvider) {
	  File convertedFile = null;
		try {
			ConversionResult conversionResult = converter.convert(file, null, transformerFactoryCreator, inputsProvider);
			String convertedContent = conversionResult.getConvertedContent();
			
			List<String> conversionProblems = conversionResult.getConversionProblems();
			reportEncounteredProblems(conversionProblems);
			
			if (convertedContent != null) {
				if(logger.isDebugEnabled()) {
					logger.debug("Print converted content in: " + outputFile);
				}
				
				if (conversionResult.getImposedOutputFileExtension() != null) {
			    outputFile = ConverterFileUtils.getUniqueOutputFile(
			        file, 
			        conversionResult.getImposedOutputFileExtension(),
	            inputsProvider.getOutputFolder());
				}
				
				// print the converted content.
				contentPrinter.print(conversionResult, transformerFactoryCreator, converterType, outputFile,
						StyleSourceGetter.getStyleSource(converterType));

				noOfConvertedFiles++;
				convertedFile = outputFile;
				if (inputsProvider.mustOpenConvertedFiles()) {
					// open the converted file
					URL convertedFileUrl;
					convertedFileUrl = outputFile.toURI().toURL();
					PluginWorkspaceProvider.getPluginWorkspace().open(convertedFileUrl);
				}

			}

		} catch (MalformedURLException e) {
		  if(logger.isDebugEnabled()) {
		    logger.debug(e.getMessage(), e);
		  }
		} catch (Exception e) {
		  if(logger.isDebugEnabled()) {
        logger.debug(e.getMessage(), e);
      }
			problemReporter.reportProblem(e, file);
			isSuccessfully = false;
			failedFile++;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Conversion " + (isSuccessfully ? "successful" : "fail"));
		}
		return convertedFile;
	}

	/**
	 * Report problems encountered in the conversion process.
	 * 
	 * @param conversionProblems The problem to report.
	 */
  private void reportEncounteredProblems(List<String> conversionProblems) {
    if(!conversionProblems.isEmpty()) {
      PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
      if(pluginWorkspace != null) {
        ResultsManager resultsManager = pluginWorkspace.getResultsManager();
        if (resultsManager != null) {
          for (String problem : conversionProblems) {
            resultsManager.addResult(ResultsUtil.BATCH_CONVERTER_RESULTS_TAB_KEY,
                new DocumentPositionedInfo(DocumentPositionedInfo.SEVERITY_WARN, problem),
                ResultType.PROBLEM, true, true);
          }
        }
      }
    }
  }
}