package tests.utils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public class TransformerFactoryCreatorImpl implements com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator{

	@Override
	public Transformer createTransformer(StreamSource streamSource) {
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = null;
		try {
			if(streamSource != null){
				transformer = transformerFactory.newTransformer(streamSource);
			}
			else{
				transformer = transformerFactory.newTransformer();
			}
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return transformer;
	}

}
