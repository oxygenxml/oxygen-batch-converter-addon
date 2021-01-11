package tests.utils;

import java.io.File;
import java.util.List;

import com.oxygenxml.resources.batch.converter.worker.ConverterStatusReporter;

/**
 * Implementation of ConverterStatusReporter for JUnits.
 * 
 * @author cosmin_duna
 */
public class ConverterStatusReporterTestImpl implements ConverterStatusReporter {

  @Override
  public void conversionStarted() {
    // Do nothing
  }

  @Override
  public void conversionInProgress(File inputFile) {
    System.out.println("Note: "+ inputFile.getAbsolutePath());
  }

  @Override
  public void conversionFinished(List<File> resultedDocuments, File outputDirectory) {
    // Do nothing
  }
}
