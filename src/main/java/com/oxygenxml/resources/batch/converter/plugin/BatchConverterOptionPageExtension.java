package com.oxygenxml.resources.batch.converter.plugin;

import javax.swing.JComponent;
import javax.xml.bind.JAXBException;

import com.oxygenxml.batch.converter.core.word.styles.WordStyleMap;
import com.oxygenxml.batch.converter.core.word.styles.WordStyleMapLoader;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
import com.oxygenxml.resources.batch.converter.view.BatchConverterOptionPanel;

import ro.sync.exml.plugin.option.OptionPagePluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;

/**
 * Option page of the batch documents converter plugin.
 * 
 * @author cosmin_duna
 */
public class BatchConverterOptionPageExtension extends OptionPagePluginExtension {
  /**
   * The page with option for this plugin.
   */
  private BatchConverterOptionPanel optionPanel = null;

  /**
   * @see OptionPagePluginExtension#init(PluginWorkspace)
   */
  @Override
  public JComponent init(PluginWorkspace pluginWorkspace) {
    optionPanel = new BatchConverterOptionPanel();
    return optionPanel;
  }
  
  /**
   * @see OptionPagePluginExtension#apply(PluginWorkspace)
   */
  @Override
  public void apply(PluginWorkspace pluginWorkspace) {
    if (optionPanel != null) {
      optionPanel.savePageState();
    }
  }

  /**
   * @see OptionPagePluginExtension#restoreDefaults()
   */
  @Override
  public void restoreDefaults() {
    if (optionPanel != null) {
      optionPanel.restoreDefault();
    }
  }

  /**
   * @see OptionPagePluginExtension#getTitle()
   */
  @Override
  public String getTitle() {
    return BatchConverterPlugin.getInstance().getDescriptor().getName();
  }
 
  
  /**
   * Get the options for the plugin that can be saved at project level.
   * 
   * @return The plugin options that can be saved at project level
   * 
   * @since 24.0
   */
  public String[] getProjectLevelOptionKeys() {
    return  new String[] {
        OptionTags.WORD_STYLES_MAP_CONFIG
    };
  }
}
