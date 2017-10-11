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
}
