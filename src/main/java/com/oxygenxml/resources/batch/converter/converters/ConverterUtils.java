package com.oxygenxml.resources.batch.converter.converters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ConverterUtils {

	public static String getUrlContents(URL theUrl) throws IOException
  {
    StringBuilder content = new StringBuilder();

    System.out.println("functie de getCOntent1");
      // create a urlconnection object
      URLConnection urlConnection = theUrl.openConnection();
      System.out.println("functie de getCOntent2");
      // wrap the urlconnection in a bufferedreader
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      String line;

      // read from the urlconnection via the bufferedreader
      while ((line = bufferedReader.readLine()) != null)
      {
        content.append(line + "\n");
      }
      bufferedReader.close();
   
    return content.toString();
  }
}
