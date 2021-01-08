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
  public void conversionStarts() {
    // Do nothing
  }

  @Override
  public void conversionStartsFor(File inputFile) {
    System.out.println("Note: "+ inputFile.getAbsolutePath());
  }

  @Override
  public void conversionHasFinished(List<File> resultedDocuments, File outputDirectory) {
    // Do nothing
  }
}
