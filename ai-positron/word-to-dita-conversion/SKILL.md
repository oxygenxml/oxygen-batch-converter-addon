---
name: word-to-dita-conversion
description: Convert Microsoft Word (.docx) documents to DITA, XHTML or DocBook with the Batch Documents Converter tools (list_word_styles, preview_word_structure, convert_documents), mapping the custom Word styles of the document to DITA structure. Use it whenever the user asks to convert, import or migrate a Word document, or asks why a converted Word document lost its titles, code blocks, notes or other formatting.
profile: xml_editor, xml_author, xml_developer
---

# Word to DITA conversion

The converter maps Word styles to HTML elements, then the HTML to DITA (or XHTML/DocBook). Only the default Word
styles (Title, Heading 1-6, Strong, Emphasis, Quote, lists, tables...) are mapped out of the box. Custom styles
("Document Title", "Code Snippet", "Keyboard Key", "Note"...) are converted as plain paragraphs or text unless you
map them, and that is where the structure of the result is decided. Do not convert blindly: inspect first.

## Workflow

### 1. Inspect the styles: `list_word_styles`

Call it with the .docx files (or folders). For each style read `type` (`p` paragraph, `r` run/character,
`table`), `custom`, `occurrences`, `samples`, `basedOn` and `mappingSource`:

- `default` / `preference`: already mapped, leave it alone.
- `inherited:<styleId>`: a custom style that takes the rule of the style it is based on; usually fine.
- `none`: unmapped, converted as a plain paragraph or text. These are the candidates for a mapping.

Also note `headingCount` (paragraphs that currently become headings), `paragraphCount` and `tocLevels`
(the heading levels the document's table of contents includes, when it has one).

### 2. Build the `word_styles_mapping`

Map the custom and unmapped styles, judging from the samples and the counts. Typical correspondences:

| Word style (look at samples)                     | `element` | `html`                              |
|--------------------------------------------------|-----------|-------------------------------------|
| Document title, book title                       | `p`       | `h1:fresh`                          |
| Heading-like custom styles (Chapter, Section...) | `p`       | `h2:fresh`, `h3:fresh`... by level  |
| Keyboard key, menu item, UI control              | `r`       | `kbd`, `b.uicontrol`                |
| File path, file name                             | `r`       | `i.filepath`                        |
| Inline code, command, parameter                  | `r`       | `code`, `tt.parmname`               |
| Code snippet, source code paragraphs             | `p`       | `pre.codeblock:separator('\n')`     |
| Note, Tip, Warning, Caution paragraphs           | `p`       | `div.note`                          |
| Quote, citation                                  | `p`       | `blockquote`                        |
| Table caption, figure caption                    | `p`       | `p.caption`                         |
| Decorative/legal boilerplate, running headers    | `p`       | `!` (dropped)                       |

Rules:

- Use `:fresh` on headings and on every block that must start a new element for each paragraph; omit it for
  inline elements and for `pre...:separator('\n')`, which merges consecutive paragraphs into one block.
- `.class` becomes the DITA `@outputclass` attribute (`i.filepath` gives `<i outputclass="filepath">`), which
  you can refactor into the proper DITA element afterwards (step 6).
- Match by `style` (the Word style name) as a rule; use `element` = `p.StyleId` / `r.StyleId` only when the
  name is missing.
- Do not remap styles that already have a `default`/`preference` mapping.
- Skip styles used once unless they are clearly structural (a document title, a lone heading level).
- When the purpose of a style is ambiguous (is "Style1" a heading or a note?), show the user the samples and
  ask, do not guess.
- Custom styles based on a mapped style inherit its mapping; if the inherited result is wrong, give the custom
  style its own rule.

### 3. Check the outline: `preview_word_structure`

Call it with the same files and the mapping. It converts in memory and returns the headings outline with
`kind` (`topic` or `section`), the word counts, the totals (`topicCount`, `sectionCount`, `smallTopicCount`,
`maxDepth`) and `unmappedStyles`.

Choose `max_heading_level_for_topics` (headings up to this level become separate DITA topics, deeper ones become
sections):

- If `list_word_styles` reported `tocLevels` (e.g. `1-3`), use its upper bound: the author already decided
  what the navigable structure is.
- Otherwise read the outline: stop at the level where the sections become small (a few dozen words) or are
  things like "Note", "Example", "Parameters", "See also", which are sections of a topic, not topics.
- If it is still unclear, show the user the outline and ask.

If the outline is wrong (the custom title is missing, headings have the wrong level, `wordsBeforeFirstHeading`
is large, `unmappedStyles` still lists something structural), fix the mapping and preview again.

### 4. Convert: `convert_documents`

Use `input_format: "word"`, the output format the user wants, the mapping and the level you chose. On the
first pass set `open_converted_files: false`; open the result once it is verified. The converter never
overwrites an existing file (it adds a counter to the name), so use a new, empty output folder for each try.

### 5. Verify the result

Read the generated .ditamap and the `convertedFiles` entries (`sizeInBytes`, `wordCount`). Check
`unmappedStyles` of the result. If the map is nested deeper than the document needs, or many topics are tiny
(a few words), lower `max_heading_level_for_topics` by one and convert again into a new output folder. Do this at
most twice, then show the user what you have and ask how to proceed.

### 6. Post-process the DITA

- Validate the output with the validation tool, if one is available.
- Replace the `@outputclass` placeholders you introduced with the proper DITA elements: `i[@outputclass='filepath']`
  with `filepath`, `pre[@outputclass='codeblock']` with `codeblock`, `b[@outputclass='uicontrol']` with
  `uicontrol`, `tt[@outputclass='parmname']` with `parmname`, `div[@outputclass='note']` with `note`, and so on
  for every class you used. Use the DITA refactoring operations (Rename element, Convert to...) or XSLT.
- Suggest the Oxygen refactoring actions that finish a migration: Convert to Task / Convert to Concept for the
  topics that are procedures or concepts, Generate IDs, Define keys / Convert references to key references.

## What the converter cannot do

- Shapes, SmartArt and drawings are not converted: the user has to save them as SVG/PNG images in Word first.
- "Fake" headings made with direct formatting (bold, bigger font) instead of styles cannot be detected
  reliably. When `headingCount` is 0 or far below what the document obviously has, tell the user and ask them
  to apply heading styles in Word before converting.
- Only .docx documents can be inspected and mapped; a .doc (Word 97-2003) file must be saved as .docx first.
