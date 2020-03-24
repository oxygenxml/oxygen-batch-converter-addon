package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.hwpf.usermodel.PictureType;
import org.junit.Test;
import org.zwobble.mammoth.images.Image;

import tests.utils.FileComparationUtil;

/**
 * Tests for {@link FileImageManager} 
 * @author cosmin_duna
 */
public class FileImageManagerTest {

	 /**
   * <p><b>Description:</b> Test the 'saveImageInternal' method from the {@link FileImageManager}.
   * We check if files with unique name are saved in a media folder from base dir.</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testSaveImageInternal() throws Exception {
		File baseDir  = new File("test-sample/wordTo");
		File image = new File(baseDir, "image.png");
	  File mediaDir = new File(baseDir, "media");

	  InputStream imageIs = new FileInputStream(image);
	  FileImageManager fileImageManager = new FileImageManager(baseDir);
	  try {
	  	assertFalse(mediaDir.exists());
	  	
	  	// Save an image 
	  	fileImageManager.saveImageInternal(imageIs, "png");
	  	
	  	assertTrue(mediaDir.exists());
	  	
	  	File[] mediaFiles = mediaDir.listFiles();
	  	assertEquals(1, mediaFiles.length);
	  	assertEquals("img.png", mediaFiles[0].getName());
	  	
	  	// Save another image
	  	fileImageManager.saveImageInternal(imageIs, "png");
	  	
	  	mediaFiles = mediaDir.listFiles();
	  	assertEquals(2, mediaFiles.length);
	  	assertTrue(Arrays.asList(mediaFiles).toString().contains("img(1).png"));
	   	assertTrue(Arrays.asList(mediaFiles).toString().contains("img.png"));
	  } finally {
	  	FileComparationUtil.deleteRecursivelly(mediaDir);
		}
	}

	 /**
   * <p><b>Description:</b> Test the 'convert' method from the {@link FileImageManager}.
   * We check if 'src' and 'alt' attributes are properly returned.</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testConvert() throws Exception {
		File baseDir  = new File("test-sample/wordTo");
		File image = new File(baseDir, "image.png");
	  File mediaDir = new File(baseDir, "media");

	  final InputStream imageIs = new FileInputStream(image);
	  FileImageManager fileImageManager = new FileImageManager(baseDir);
	  
	  try {
	  	assertFalse(mediaDir.exists());
	  	
	  	// Save an image without alt text
	  	Map<String, String> imageAttributes = fileImageManager.convert(new Image() {
				public InputStream getInputStream() throws IOException {
					return imageIs;
				}
			  public String getPath() {
			    return "";
			  }
				public String getContentType() {
					return "image/jpeg";
				}
				public Optional<String> getAltText() {
					return  Optional.empty();
				}
			});
	  	
	  	assertTrue(mediaDir.exists());

	  	String srcAttr = imageAttributes.get("src");
	  	assertEquals(1, imageAttributes.size());
			assertEquals("media/img.jpg", srcAttr);
	  	assertNull(imageAttributes.get("alt"));
	  	
	  	File srcImage = new File(baseDir, srcAttr);
	  	assertTrue(srcImage.exists());
	  	assertEquals("img.jpg", srcImage.getName());
	  	
	  	// Save an image with alt text
	  	FileComparationUtil.deleteRecursivelly(mediaDir);
	  	imageAttributes = fileImageManager.convert(new Image() {
				public InputStream getInputStream() throws IOException {
					return imageIs;
				}
			  public String getPath() {
          return "";
        }
				public String getContentType() {
					return "image/tiff";
				}
				public Optional<String> getAltText() {
					return Optional.of("altText");
				}
			});
	  	
	  	assertTrue(mediaDir.exists());
	  	
	  	assertEquals(2, imageAttributes.size());
	  	srcAttr = imageAttributes.get("src");
	  	assertEquals("media/img.tif", srcAttr);
	  	assertEquals("altText", imageAttributes.get("alt"));
	  	
	  	srcImage = new File(baseDir, srcAttr);
	  	assertTrue(srcImage.exists());
	  	assertEquals("img.tif", srcImage.getName());
	  	
	  } finally {
	  	FileComparationUtil.deleteRecursivelly(mediaDir);
		}
	}
	
	 /**
   * <p><b>Description:</b> Test the 'savePicture' method from the {@link FileImageManager}.
   * We check if the path to image file is properly returned.</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testSavePicture() throws Exception {
		File baseDir  = new File("test-sample/wordTo");
		File image = new File(baseDir, "image.png");
	  File mediaDir = new File(baseDir, "media");

	  FileImageManager fileImageManager = new FileImageManager(baseDir);
	  
	  try {
	  	assertFalse(mediaDir.exists());
	  	
	  	// Save a picture.
	  	String imagePath = fileImageManager.savePicture(
	  			Files.readAllBytes(image.toPath()), PictureType.JPEG, "bla.jgp", 0, 0);
	  	
	  	assertTrue(mediaDir.exists());
			assertEquals("media/img.jpg", imagePath);
	  	
	  	File srcImage = new File(baseDir, imagePath);
	  	assertTrue(srcImage.exists());
	  	assertEquals("img.jpg", srcImage.getName());
	  	
	  } finally {
	  	FileComparationUtil.deleteRecursivelly(mediaDir);
		}
	}
}
