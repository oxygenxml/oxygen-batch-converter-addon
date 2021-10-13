package com.oxygenxml.resources.batch.converter.view;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.junit.Test;

import ro.sync.basic.io.IOUtil;

/**
 * Tests for {@link WordStylesConfigUtil}
 * 
 * @author cosmin_duna
 */
public class WordStylesConfigUtilTest {

  /**
   * <p><b>Description:</b> Test export of Word styles mapping</p>
   * <p><b>Bug ID:</b> EXM-45729</p>
   *
   * @author cosmin_duna
   * @throws Exception 
   *
   * @throws Exception
   */
  @Test
  public void testExportWordStylesMapConfiguration() throws Exception {
    File outputFile  = new File("test-export/exportedFile.html");   
    outputFile.getParentFile().mkdir();
    try {
      DefaultTableModel tableModel = new DefaultTableModel(0, 3);
      tableModel.addRow(new String [] {"p", "Title", "h1:fresh"});
      tableModel.addRow(new String [] {"p", "Subtitle", "h1"});
      tableModel.addRow(new String [] {"p.Heading1", "", "h1"});
      tableModel.addRow(new String [] {"u", "", "u"});
      tableModel.addRow(new String [] {"p:unordered-list(1)", "", "ul > li:fresh"});
      
      WordStylesConfigUtil.exportWordStylesMapConfiguration(tableModel, outputFile);
      
      String exportedContent = IOUtil.readFile(outputFile);
      assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
          "<styleMap>\n" + 
          "    <toHTML>\n" + 
          "        <relation>\n" + 
          "            <element>p</element>\n" + 
          "            <resultedHTML fresh=\"true\">h1</resultedHTML>\n" + 
          "            <styleName>Title</styleName>\n" + 
          "        </relation>\n" + 
          "        <relation>\n" + 
          "            <element>p</element>\n" + 
          "            <resultedHTML fresh=\"false\">h1</resultedHTML>\n" + 
          "            <styleName>Subtitle</styleName>\n" + 
          "        </relation>\n" + 
          "        <relation>\n" + 
          "            <element>p.Heading1</element>\n" + 
          "            <resultedHTML fresh=\"false\">h1</resultedHTML>\n" + 
          "            <styleName></styleName>\n" + 
          "        </relation>\n" + 
          "        <relation>\n" + 
          "            <element>u</element>\n" + 
          "            <resultedHTML fresh=\"false\">u</resultedHTML>\n" + 
          "            <styleName></styleName>\n" + 
          "        </relation>\n" + 
          "        <relation>\n" + 
          "            <element>p:unordered-list(1)</element>\n" + 
          "            <resultedHTML fresh=\"true\">ul &gt; li</resultedHTML>\n" + 
          "            <styleName></styleName>\n" + 
          "        </relation>\n" + 
          "    </toHTML>\n" + 
          "    <customToDefault/>\n" + 
          "</styleMap>", exportedContent);
    } finally {
      outputFile.delete();
      outputFile.getParentFile().delete();
    } 
  }
  
  /**
   * <p><b>Description:</b> Test import of Word styles mapping</p>
   * <p><b>Bug ID:</b> EXM-45729</p>
   *
   * @author cosmin_duna
   * @throws Exception 
   *
   * @throws Exception
   */
  @Test
  public void testImportWordStylesMapConfiguration() throws Exception {
    File configFile  = new File("test-sample/EXM-45729/wordStylesConfig.xml");   

    DefaultTableModel tableModel = new DefaultTableModel(0, 3);
    WordStylesConfigUtil.importWordStylesMapConfiguration(tableModel, configFile);

    assertEquals(
        "-|p|-|Title|-|h1:fresh|\n" + 
        "-|p|-|Subtitle|-|h2:fresh|\n" + 
        "-|p|-|Heading 1|-|h1:fresh|\n" + 
        "-|p.Heading1|-||-|h1:fresh|\n" + 
        "-|r|-|Strong|-|strong|\n" + 
        "-|b|-||-|strong|\n" + 
        "-|i|-||-|em|\n" + 
        "-|p:unordered-list(1)|-||-|ul > li:fresh|\n" + 
        "-|p:unordered-list(2)|-||-|ul|ol > li > ul > li:fresh|\n" + 
        "-|p:ordered-list(1)|-||-|ol > li:fresh|\n" + 
        "-|p|-|Document Title|-|h1:fresh|\n" + 
        "-|p|-|Document Subtitle|-|h2:fresh|\n",
        serializeTable(tableModel));
  }

  /**
   * <p><b>Description:</b> Test set word styles mapping in table</p>
   * <p><b>Bug ID:</b> EXM-45729</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testSetWordStylesMappingInTable() throws Exception {
    DefaultTableModel tableModel = new DefaultTableModel(0, 3);
    String wordStylesMappingInText = 
        "p[style-name='Title'] => h1:fresh\n" + 
        "p[style-name=''] => h2:fresh\n" + 
        "p => h1:fresh\n" +
        "p.Heading1[style-name=''] => h1:fresh\n"+
        "p.Heading2 => h2:fresh\n"+
        "p.Heading6 =>\n"+
        "p.Heading6 =>!\n"+
        "p[style-name='Heading 2'] => h2\n" + 
        "p[style-name='Heading 3'] => h3:fresh \n" + 
        "p[style-name='Heading 4'] => h4 :fresh\n" + 
        "b => strong\n"+
        "p:unordered-list(1)[style-name=''] => ul > li:fresh";
    WordStylesConfigUtil.setWordStylesMappingInTable(wordStylesMappingInText, tableModel);
    
    assertEquals(
        "-|p|-|Title|-|h1:fresh|\n" + 
        "-|p|-||-|h2:fresh|\n" + 
        "-|p|-||-|h1:fresh|\n" + 
        "-|p.Heading1|-||-|h1:fresh|\n" + 
        "-|p.Heading2|-||-|h2:fresh|\n" + 
        "-|p.Heading6|-||-||\n" + 
        "-|p.Heading6|-||-|!|\n" + 
        "-|p|-|Heading 2|-|h2|\n" + 
        "-|p|-|Heading 3|-|h3:fresh|\n" + 
        "-|p|-|Heading 4|-|h4 :fresh|\n" + 
        "-|b|-||-|strong|\n" + 
        "-|p:unordered-list(1)|-||-|ul > li:fresh|\n" + 
        "", serializeTable(tableModel));
  }
  
  /**
   * <p><b>Description:</b> Test serialize the word styles mapping in text format.</p>
   * <p><b>Bug ID:</b> EXM-45729</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testSerializeWordStylesMapping() throws Exception {
    DefaultTableModel tableModel = new DefaultTableModel(0, 3);
    tableModel.addRow(new String [] {"p", "Title", "h1:fresh"});
    tableModel.addRow(new String [] {"p", " Subtitle", "h1 "});
    tableModel.addRow(new String [] {"p.Heading1 ", "", "h1"});
    tableModel.addRow(new String [] {"u", "", "u"});
    tableModel.addRow(new String [] {"b", "", " "});
    tableModel.addRow(new String [] {"i", "", "! "});
    tableModel.addRow(new String [] {"p:unordered-list(1)", "", "ul > li:fresh "});
    
    String serializedWordStylesMapping = WordStylesConfigUtil.serializeWordStylesMapping(tableModel);
    
    assertEquals(
        "p[style-name='Title'] => h1:fresh\n" + 
        "p[style-name='Subtitle'] => h1\n" + 
        "p.Heading1 => h1\n" + 
        "u => u\n" + 
        "b => \n" + 
        "i => !\n" + 
        "p:unordered-list(1) => ul > li:fresh", serializedWordStylesMapping);
  }
  
  /**
   * Serialize content from table.
   * 
   * @param tableModel
   * 
   * @return The serialized content.
   */
  private String serializeTable(DefaultTableModel tableModel) {
    StringBuilder content = new StringBuilder();
    Vector dataVector = tableModel.getDataVector();
    int nuOfVectors = dataVector.size();
    for (int i = 0; i < nuOfVectors; i++) {
      Vector row = (Vector) dataVector.get(i);
      for (int j = 0; j < row.size(); j++) {
        content.append("-|");
        content.append(row.get(j));
        content.append("|");
      }
      content.append("\n");
    }
    return content.toString();
  }
}
