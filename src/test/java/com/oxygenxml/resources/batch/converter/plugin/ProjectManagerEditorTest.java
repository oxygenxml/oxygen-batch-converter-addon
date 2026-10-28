package com.oxygenxml.resources.batch.converter.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.resources.batch.converter.InputFilesManager;

/**
 * Tests for {@link ProjectManagerEditor}.
 *
 * @author cosmin_duna
 */
public class ProjectManagerEditorTest {

  /**
   * <p><b>Description:</b> The files from a folder selected in the Project view are associated with
   * that folder, so its structure is preserved in the output.</p>
   *
   * <p><b>Bug ID:</b> BDC-166</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testSelectedFolderKeepsRootDirectory() {
    File root = new File("test-sample/folder-structure-test");

    InputFilesManager manager = ProjectManagerEditor.collectInputFiles(
        new File[] {root}, ConverterTypes.MD_TO_DITA);

    List<File> inputFiles = manager.getInputFiles();
    assertEquals(2, inputFiles.size());
    for (File inputFile : inputFiles) {
      assertEquals(root, manager.getRootDirectoryForFile(inputFile));
    }
  }

  /**
   * <p><b>Description:</b> A file selected in the Project view has no root directory, so it is
   * converted directly in the output folder.</p>
   *
   * <p><b>Bug ID:</b> BDC-166</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testSelectedFileHasNoRootDirectory() {
    File file = new File("test-sample/folder-structure-test/sub1/topic.md");

    InputFilesManager manager = ProjectManagerEditor.collectInputFiles(
        new File[] {file}, ConverterTypes.MD_TO_DITA);

    assertEquals(1, manager.getInputFiles().size());
    assertNull(manager.getRootDirectoryForFile(manager.getInputFiles().get(0)));
  }
}
