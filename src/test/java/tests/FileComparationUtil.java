package tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class FileComparationUtil {

	public static boolean compareLineToLine(String file1, String file2) throws IOException{
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
          
         System.out.println(line1);
         System.out.println(line2);
         
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
				e.printStackTrace();
			}
		
		}
	
	return areEqual;

	}
}
