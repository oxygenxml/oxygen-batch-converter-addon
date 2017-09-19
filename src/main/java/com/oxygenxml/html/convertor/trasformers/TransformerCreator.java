package com.oxygenxml.html.convertor.trasformers;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

public interface TransformerCreator {

	public Transformer createTransformer(StreamSource streamSource);

}