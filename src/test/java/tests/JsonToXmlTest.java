package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

import tests.utils.ConvertorWorkerInteractorTestImpl;
import tests.utils.FileComparationUtil;
import tests.utils.ProblemReporterTestImpl;
import tests.utils.ProgressDialogInteractorTestImpl;
import tests.utils.TransformerFactoryCreatorImpl;

public class JsonToXmlTest {

	@Test
	public void test() throws TransformerException, IOException {
	
		File sample  = new File("test-sample/jsonTest.json");		
		File goodSample = new File("test-sample/xmlTest.xml");
		File outputFolder = sample.getParentFile();
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new ProgressDialogInteractorTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File fileToRead = ConverterFileUtils.generateOutputFile(sample, FileExtensionType.XML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.JSON_TO_XML, inputFiles, outputFolder);

			assertTrue(FileComparationUtil.compareLineToLine(goodSample, fileToRead));

		} finally {
			try {
				Files.delete(Paths.get(fileToRead.getPath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
		
}
