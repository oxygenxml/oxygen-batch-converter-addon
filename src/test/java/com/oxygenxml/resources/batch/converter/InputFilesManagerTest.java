package com.oxygenxml.resources.batch.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link InputFilesManager}.
 *
 * @author cosmin_duna
 */
public class InputFilesManagerTest {

  private InputFilesManager manager;

  @Before
  public void setUp() {
    manager = new InputFilesManager();
  }

  /**
   * <p><b>Description:</b> Files added via folder are mapped to that folder as root directory.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testAddFilesFromFolderMapsRootDirectory() {
    File root = new File("root");
    File sub1 = new File("root/sub1/a.md");
    File sub2 = new File("root/sub2/a.md");

    manager.addFilesFromFolder(Arrays.asList(sub1, sub2), root);

    assertEquals(root, manager.getRootDirectoryForFile(sub1));
    assertEquals(root, manager.getRootDirectoryForFile(sub2));
  }

  /**
   * <p><b>Description:</b> Files added individually have no root directory association.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testAddFilesIndividuallyHasNoRootDirectory() {
    File file = new File("some/file.md");

    manager.addFiles(Arrays.asList(file));

    assertNull(manager.getRootDirectoryForFile(file));
  }

  /**
   * <p><b>Description:</b> Files from multiple folders are each mapped to their own root directory.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testMultipleFoldersTrackedSeparately() {
    File root1 = new File("root1");
    File root2 = new File("root2");
    File file1 = new File("root1/topic.md");
    File file2 = new File("root2/topic.md");

    manager.addFilesFromFolder(Arrays.asList(file1), root1);
    manager.addFilesFromFolder(Arrays.asList(file2), root2);

    assertEquals(root1, manager.getRootDirectoryForFile(file1));
    assertEquals(root2, manager.getRootDirectoryForFile(file2));
  }

  /**
   * <p><b>Description:</b> Removing a file also removes its root directory association.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testRemoveFileClearsRootAssociation() {
    File root = new File("root");
    File file = new File("root/sub/topic.md");

    manager.addFilesFromFolder(Arrays.asList(file), root);
    manager.removeFile(file);

    assertNull(manager.getRootDirectoryForFile(file));
    assertTrue(manager.getInputFiles().isEmpty());
  }

  /**
   * <p><b>Description:</b> Clearing the manager removes all files and all root directory mappings.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testClearRemovesAllFilesAndMappings() {
    File root = new File("root");
    File file1 = new File("root/sub1/a.md");
    File file2 = new File("root/sub2/b.md");

    manager.addFilesFromFolder(Arrays.asList(file1, file2), root);
    manager.clear();

    assertTrue(manager.isEmpty());
    assertNull(manager.getRootDirectoryForFile(file1));
    assertNull(manager.getRootDirectoryForFile(file2));
  }

  /**
   * <p><b>Description:</b> Adding the same file twice via folder keeps only one entry
   * and preserves the root directory mapping.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testDuplicateFileIsNotAddedTwice() {
    File root = new File("root");
    File file = new File("root/sub/topic.md");

    manager.addFilesFromFolder(Arrays.asList(file), root);
    manager.addFilesFromFolder(Arrays.asList(file), root);

    List<File> files = manager.getInputFiles();
    assertEquals(1, files.size());
    assertEquals(root, manager.getRootDirectoryForFile(file));
  }

  /**
   * <p><b>Description:</b> When a file already added from a folder is re-added individually,
   * its root directory mapping is removed so the converter treats it as a standalone file.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testReAddingFolderFileIndividuallyRemovesRootMapping() {
    File root = new File("root");
    File file = new File("root/sub/topic.md");

    manager.addFilesFromFolder(Arrays.asList(file), root);
    assertEquals(root, manager.getRootDirectoryForFile(file));

    manager.addFiles(Arrays.asList(file));

    assertNull(manager.getRootDirectoryForFile(file));
    assertEquals(1, manager.getInputFiles().size());
  }

  /**
   * <p><b>Description:</b> When a file added individually is subsequently included in a folder add,
   * its individual state is preserved — the folder mapping is not applied retroactively.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testAddingFolderAfterIndividualFilePreservesIndividualState() {
    File root = new File("root");
    File file = new File("root/sub/topic.md");

    manager.addFiles(Arrays.asList(file));
    assertNull(manager.getRootDirectoryForFile(file));

    manager.addFilesFromFolder(Arrays.asList(file), root);

    assertNull(manager.getRootDirectoryForFile(file));
    assertEquals(1, manager.getInputFiles().size());
  }

  /**
   * <p><b>Description:</b> When addFilesFromFolder is called with a null root directory,
   * the files are treated as individually added — no folder mapping is created.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testAddFilesFromFolderWithNullRootTreatsFilesAsIndividual() {
    File file = new File("root/sub/topic.md");

    manager.addFilesFromFolder(Arrays.asList(file), null);

    assertNull(manager.getRootDirectoryForFile(file));
    assertEquals(1, manager.getInputFiles().size());
    assertTrue("No folder mappings should exist", !manager.hasFolderMappings());
  }

  /**
   * <p><b>Description:</b> Files added from a folder and files added individually coexist correctly:
   * folder files have a root directory, individually added files do not.</p>
   *
   * <p><b>Bug ID:</b> BDC-107</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testMixedAddFilesAndFolderFiles() {
    File root = new File("root");
    File folderFile = new File("root/sub/topic.md");
    File individualFile = new File("other/doc.md");

    manager.addFilesFromFolder(Arrays.asList(folderFile), root);
    manager.addFiles(Arrays.asList(individualFile));

    assertEquals(root, manager.getRootDirectoryForFile(folderFile));
    assertNull(manager.getRootDirectoryForFile(individualFile));
    assertEquals(2, manager.getInputFiles().size());
  }
}
