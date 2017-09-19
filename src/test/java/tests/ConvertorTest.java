/*package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

public class ConvertorTest {

	@Test
	public void test() {
		try {
			String path = "D:/HTMLConvertor/test-sample/sample.html";
			String directoryPath = "D:/HTMLConvertor/test-sample";
			String goodSample = "D:/HTMLConvertor/test-sample/goodSample.html";
			
			//String newFilePath = PathGenerator.generate(path, directoryPath);
			
			
			//html purifier
			HtmlPurifier htmlPurifier = new HtmlPurifier();
			
			//purified content
			byte data[] = htmlPurifier.createWellFormedContent(path);

			//save the purified content at the newFilePath
			//ContentPrinter.print(data, newFilePath);
			
				
			//get the byteArray of goodSample
			Path goodFilepath = Paths.get(goodSample);
			byte[] wellData = Files.readAllBytes(goodFilepath);
			
			
			assertTrue(Arrays.equals(data, wellData));
			
			assertEquals(3, new File(directoryPath).list().length);
			
			//delete the file
	//		Files.delete(Paths.get(newFilePath));
			
		} catch (IOException e) {
			
		}
	}

}*/