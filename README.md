# Oxygen Batch Converter add-on for Oxygen XML Editor
This add-on can be installed in Oxygen XML Editor to enable batch conversions between the following formats:  

* HTML to DITA
* Markdown to DITA
* Excel to DITA
* Word to DITA

* XML to JSON
* JSON to XML

* HTML to XHTML
* Markdown to XHTML
* Word to XHTML

* HTML to DocBook4 / DocBook5
* Markdown to DocBook4 / DocBook5 
* Word to DocBook4 / DocBook5

## Word to DITA
This conversion works best if you only use the default MS Word styles to semantically mark up your document.
 
The **Create DITA maps from Word documents containing multiple sections** option from the conversion dialog box allows you to decide whether the output will be a DITA map or a DITA topic.
When this option is selected, all sections from your Word document marked by titles or headings will be separated into individual DITA topics and referenced in a DITA map. If the word document does not contain multiple sections, the output will be a single topic.
When this option is not selected, the output will be a topic with nested topics or sections according to the number of sections from the Word document.
## Compatibility

The add-on is compatible with Oxygen XML Editor/Author/Developer version 20.0 or newer. 

## How to Install

1. Go to **Help->Install new add-ons** to open an add-on selection dialog box.
2. Enter or paste https://raw.githubusercontent.com/oxygenxml/oxygen-resources-convertor/master/build/addon.xml in the **Show add-ons from** field.
3. Select the **Batch Converter** add-on and click **Next**.
4. Select the **I accept all terms of the end user license agreement** option and click **Finish**.
5. Restart the application. 

Result: A **Batch Converter** submenu will now be available in the **Tools** menu and in the contextual menu. This submenu will contain a list of the various types of available conversions. Selecting one of the types of conversions will open a dialog box where you can configure options for the conversion.

## Converting a document:

1. Select the type of conversion from the **Tools -> Batch Converter** submenu.
1. Add the **Input files**.
1. Choose the path of the **Output folder** that will contain the converted document.
1. Click the **Convert** button.

or:

1. Select a folder or a set of files in the Project view, right-click, choose **Batch Convertor**. 
1. Choose the path of the **Output folder** that will contain the converted document.
1. Click the **Convert** button.

Copyright and License
---------------------
Copyright 2018-2019 Syncro Soft SRL.

This project is licensed under [Apache License 2.0](https://github.com/oxygenxml/oxygen-resources-converter/blob/master/LICENSE)

