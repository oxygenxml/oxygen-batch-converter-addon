package com.oxygenxml.resources.batch.converter.converters;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.zwobble.mammoth.images.Image;
import org.zwobble.mammoth.images.ImageConverter;

import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

/**
 * Manager for the image processing in the converters from the WORD format.
 * 
 * @author cosmin_duna
 *
 */
public class FileImageManager implements PicturesManager, ImageConverter.ImgElement{
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(FileImageManager.class);
	
	/**
	 * The related path at the base directory for the images.  
	 */
	private static final String IMAGES_RELATIVE_PATH = "media/img";
	
	/**
	 * Map between image extension and the file name counter of the last saved image.
	 */
	private Map<String, Integer> lastImageFileNameCounterMap = new HashMap<String, Integer>(); 
	
	/**
	 * A map between the Media type of images and the extensions.
	 */
	private static final Map<String, String> MIME_EXTENSION = new HashMap<String, String>();
	static	{
		MIME_EXTENSION.put("image/bmp", "bmp");
		MIME_EXTENSION.put("image/cis-cod", "cod");
    MIME_EXTENSION.put("image/gif", "gif");
    MIME_EXTENSION.put("image/png", "png");
    MIME_EXTENSION.put("image/jpeg", "jpg");
    MIME_EXTENSION.put("image/pipeg", "jfif");
  	MIME_EXTENSION.put("image/svg+xml", "svg");
		MIME_EXTENSION.put("image/tiff", "tif");
    MIME_EXTENSION.put("image/x-cmx", "cmx");
    MIME_EXTENSION.put("image/x-icon", "ico");
    MIME_EXTENSION.put("image/x-portable-anymap", "pnm");
  	MIME_EXTENSION.put("image/x-portable-bitmap", "pbm");
		MIME_EXTENSION.put("image/x-portable-graymap", "pgm");
    MIME_EXTENSION.put("image/x-portable-pixmap", "ppm");
    MIME_EXTENSION.put("image/x-rgb", "rgb");
    MIME_EXTENSION.put("image/x-xbitmap", "xbm");
    MIME_EXTENSION.put("image/x-xpixmap", "xpm");
    MIME_EXTENSION.put("image/x-xwindowdump", "xwd");
	}
	
	/**
	 * The base directory.
	 */
	private File baseDir;
	
	/**
	 * Constructor
	 * 
	 * @param baseDir The base directory. The images are linked in the document relative at this directory.
	 */
	public FileImageManager(File baseDir) {
		this.baseDir = baseDir;
	}
	
	@Override
	public String savePicture(
			byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches) {
		String extension = "png";
		if(pictureType != null) {
			extension = pictureType.getExtension();
		}
		
		String imageRelativePath = "";
		try {
			imageRelativePath = saveImageInternal(new ByteArrayInputStream(content), extension);
		} catch (IOException e) {
			logger.debug(e.getMessage(), e);
		}
		return imageRelativePath;
	}
	
	@Override
	public Map<String, String> convert(Image image) throws IOException {
			String extension = "png";
			String contentType = image.getContentType();
			if (contentType != null && MIME_EXTENSION.containsKey(contentType)) {
				extension = MIME_EXTENSION.get(contentType);
			}
			
			String relativePath = saveImageInternal(image.getInputStream(), extension);
	    Map<String, String> attributes = new HashMap();
			attributes.put("src", relativePath);

			Optional<String> altText = image.getAltText();
			if(altText.isPresent()) {
				attributes.put("alt", altText.get());
			}
	    return attributes;
	}

	/**
	 * Save the image on the disk
	 * 
	 * @param imageIs 		The input stream of the image content.
	 * @param extension 	The extension of the image.
	 * 
	 * @return The relative path of the image.
	 * @throws IOException
	 */
	 String saveImageInternal(InputStream imageIs, String extension) throws IOException {
		// Create an unique file
		File imageFile = new File(baseDir, IMAGES_RELATIVE_PATH + '.' + extension);
		Integer lastImageCounter = lastImageFileNameCounterMap.get(extension);
		if(lastImageCounter == null) {
			lastImageCounter = 0;
		}
		File uniqueImageFile = ConverterFileUtils.getFileWithCounter(imageFile, lastImageCounter + 1);

		String name = uniqueImageFile.getName();
		int openBracket = name.lastIndexOf('(');
		int closeBracket = name.lastIndexOf(')');
		if(openBracket != -1 && closeBracket != -1 && openBracket < closeBracket) {
			lastImageFileNameCounterMap.put(
					extension, new Integer(name.substring(openBracket + 1, closeBracket)));
		} else {
			lastImageFileNameCounterMap.put(extension, 0);
		}
		
		imageFile.getParentFile().mkdirs();
		Files.copy(imageIs, uniqueImageFile.toPath());

		int rootLength = baseDir.getAbsolutePath().length();
		String absFileName = uniqueImageFile.getAbsolutePath();
		String relativePath = absFileName.substring(rootLength + 1);
		return relativePath.replace('\\', '/');
	}
}
