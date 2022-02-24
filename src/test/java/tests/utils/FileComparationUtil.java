package tests.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileComparationUtil {

	/**
	 * Compare line to line the given files.
	 * @param file1 
	 * @param file2
	 * @return
	 * @throws IOException
	 */
	public static boolean compareLineToLine(File file1, File file2) throws IOException{
		 boolean areEqual = true;
		 BufferedReader reader1 = null;
		 BufferedReader reader2 = null; 
		try {
		reader1  = new BufferedReader(new FileReader(file1));
     
     reader2 = new BufferedReader(new FileReader(file2));
      
     String line1 = reader1.readLine();
     String line2 = reader2.readLine();
     while (line1 != null || line2 != null)
     {
         if(line1 == null || line2 == null)
         {
             areEqual = false;
             break;
         }
         else if(! line1.equalsIgnoreCase(line2))
         {
             areEqual = false;
             break;
         }
          
         line1 = reader1.readLine();
         line2 = reader2.readLine();
     }
     
		}finally{
			try {
				if(reader1 != null){
					reader1.close();
				}
				if(reader2 != null){
					reader2.close();
				}
			} catch (IOException e) {
			}
		
		}
	return areEqual;

	}
	
	/**
	 * Read the given file.
	 * @param filePath The path of the file to read.
	 * @return The content of the file in String format.
	 * @throws IOException 
	 */
	public static String readFile(String filePath) throws IOException{
		FileReader fileReader = new FileReader(filePath);
		String fileContents = "";
		int i ;
		try {
			while((i =  fileReader.read())!=-1){
				char ch = (char)i;
				fileContents = fileContents + ch; 
			}
		} finally {
			fileReader.close();
		}
		fileContents = fileContents.replace("\r", "");
		return fileContents;
	}
	
	 /**
   *  Deletes recursively the specified directory.
   * 
   *@param  file   The file or directory to be deleted.
   */
  public static void deleteRecursivelly(File file) {
    if (!file.exists()) {
      // Nothing to do.
      return;
    }
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (int i = 0; i < files.length; i++) {
          deleteRecursivelly(files[i]);
        }
      }
      file.delete();
    } else {
      file.delete();
    }
  }
}
