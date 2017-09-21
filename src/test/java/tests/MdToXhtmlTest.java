package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.oxygenxml.html.convertor.trasformers.ContentPrinter;
import com.oxygenxml.html.convertor.trasformers.FilePathGenerator;
import com.oxygenxml.html.convertor.trasformers.HtmlToXhtmlTransformer;
import com.oxygenxml.html.convertor.trasformers.MarkdownToHtmlTransformer;
import com.oxygenxml.html.convertor.trasformers.TransformerFactoryCreator;
import com.oxygenxml.html.convertor.trasformers.TransformerFactoryCreatorImpl;

public class MdToXhtmlTest {

	@Test
	public void test() throws TransformerException, IOException {
		String sample = "file:" + File.separator + "D:" + File.separator + "HTMLConvertor" + File.separator + "test-sample"
				+ File.separator + "markdownTest.md";
		String goodSample = "D:" + File.separator + "HTMLConvertor" + File.separator + "test-sample" + File.separator
				+ "goodMdToXhtml.xhtml";

		String folder = "D:" + File.separator + "HTMLConvertor" + File.separator + "test-sample";

		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();

		MarkdownToHtmlTransformer markdownToHtmlTransformer = new MarkdownToHtmlTransformer();

		String htmlContent = markdownToHtmlTransformer.convert(new URL(sample), null, transformerCreator);

		String xhtmlContent = new HtmlToXhtmlTransformer().convert(null, new StringReader(htmlContent), transformerCreator);

		System.out.println(xhtmlContent);

		File fileToRead = FilePathGenerator.generate(sample, "xhtml", folder);

		try {

			ContentPrinter.prettifyAndPrint(new StringReader(xhtmlContent), fileToRead,
					"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd", "-//W3C//DTD XHTML 1.0 Strict//EN",
					new TransformerFactoryCreatorImpl());

			System.out.println(goodSample);
			System.out.println(fileToRead.toString());

			assertTrue(FileComparationUtil.compareLineToLine(goodSample, fileToRead.toString()));

		} finally {
			try {
				Files.delete(Paths.get(fileToRead.getPath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
