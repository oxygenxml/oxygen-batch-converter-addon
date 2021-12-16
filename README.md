# Oxygen Batch Documents Converter add-on for Oxygen XML Editor
This add-on can be installed in Oxygen XML Editor to enable batch conversions between the following formats:  

* HTML to XHTML
* HTML to DITA
* HTML to DocBook4 / DocBook5

* Markdown to XHTML
* Markdown to DITA
* Markdown to DocBook4 / DocBook5
 
* Word to XHTML
* Word to DITA
* Word to DocBook4 / DocBook5
* Excel to DITA

* JSON to XML
* XML to JSON
* JSON to YAML
* YAML to JSON

For more information about the add-on see the following documentation topic: [Batch Documents Converter Add-on](https://www.oxygenxml.com/doc/ug-editor/topics/batch-converter-addon.html)

## Compatibility

The add-on is compatible with Oxygen XML Editor/Author/Developer version 22.1 or newer. 

## How to Install

1. Go to **Help->Install new add-ons** to open an add-on selection dialog box.
2. Enter or paste https://www.oxygenxml.com/InstData/Addons/default/updateSite.xml in the **Show add-ons from** field.
3. Select the **Batch Documents Converter** add-on and click **Next**.
4. Read the end-user license agreement. Then select the **I accept all terms of the end-user license agreement** option and click **Finish**.
5. Restart the application. 

Result: A **Batch Documents Converter** submenu will now be available in the **Tools** menu and in the contextual menu. This submenu will contain a list of the various types of available conversions. Selecting one of the types of conversions will open a dialog box where you can configure options for the conversion.

The add-on can also be installed using the following alternative installation procedure:
1. Go to the [Releases page](https://github.com/oxygenxml/oxygen-resources-converter/releases/latest) and download the `oxygen-batch-converter-{version}-plugin.jar` file.
2. Unzip it inside `{oXygenInstallDir}/plugins`. Make sure you don't create any intermediate folders. After unzipping the archive, the file system should look like this: `{oXygenInstallDir}/plugins/oxygen-batch-converter-x.y.z`, and inside this folder, there should be a `plugin.xml`file.


## Converting a document:

1. Select the type of conversion from one of the following sub-menus 
- **Batch Documents Converter** located in the **Tools** menu 
- **Additional conversions** located in the **File -> Import/Convert** sub-menu.
- **Import** located in the **Append child**, **Insert Before**, and **Insert After** sub-menus from the contextual menu of the **DITA Maps Manager** view.
2. Add the **Input files**.
3. Choose the path of the **Output folder** that will contain the converted document.
4. Click the **Convert** button (or **Import** for the actions invoked from the contextual menu of the **DITA Maps Manager** view).

or:

1. Select a folder or a set of files in the **Project** view, right-click, choose **Batch Convertor**. 
1. Choose the path of the **Output folder** that will contain the converted document.
1. Click the **Convert** button.

When actions are invoked from the contextual menu of the **DITA Maps Manager** view, 
the resulting documents from the conversion are automatically inserted in the map as follows:
* Actions from **Append child** inserts map nodes as children of the currently selected node.
* Actions from **Insert Before** inserts map nodes as siblings of the currently selected node, above the current node in the map.
* Actions from **Insert After** inserts map nodes as siblings of the currently selected node, below the current node in the map.

Copyright and License
---------------------
Copyright 2018-2021 Syncro Soft SRL.

This project is licensed under [Apache License 2.0](https://github.com/oxygenxml/oxygen-resources-converter/blob/master/LICENSE)
