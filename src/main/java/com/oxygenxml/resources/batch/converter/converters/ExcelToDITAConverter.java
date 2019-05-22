package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * The converter from EXCEL to DITA
 * 
 * @author cosmin_duna
 */
public class ExcelToDITAConverter implements Converter {
	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(ExcelToDITAConverter.class);
	
	@Override
	public String convert(File originalFile, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
			throws TransformerException {
		try {
			return convertInternal(originalFile);
		} catch (IOException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		} finally {
			if (contentReader != null) {
				try {
					contentReader.close();
				} catch (IOException e) {
					logger.warn(e, e);
				}
			}
		}
	}

	/**
	 * Convert the given excel inputSource in DITA.
	 * 
	 * @param originalFile 				The source file.
 	 * 
	 * @return The converted DITA content in String format.
 	 * @throws IOException
	 */
	private String convertInternal(File originalFile) throws IOException {
		int headerRowsNo = 1;
		URL url = originalFile.toURI().toURL();
		// Check out how many header rows we should have
		String query = url.getQuery();
		if (query != null) {
			String[] paramNameValue = query.split("&");
			for (int i = 0; i < paramNameValue.length; i++) {
				String nameValue = paramNameValue[i];
				String[] nameAndValue = nameValue.split("=");
				if (nameAndValue != null && nameAndValue.length == 2 
						&& "headerRowsNo".equals(nameAndValue[0])) {
					try {
						headerRowsNo = Integer.parseInt(nameAndValue[1]);
					} catch (Exception ex) {
						logger.warn(ex.getMessage(), ex);
					}
				}
			}
		}
		URL urlForConnect = url;
		if ("file".equals(url.getProtocol())) {
			String urlStr = urlForConnect.toString();
			if (urlStr.contains("?")) {
				// Remove query part
				urlForConnect = new URL(urlStr.substring(0, urlStr.indexOf('?')));
			}
		}
		InputStream is = urlForConnect.openStream();
		StringBuilder sb = new StringBuilder();
		String name = originalFile.getName();
		int dotIndex = name.lastIndexOf('.');
		String extension = "";
		if (dotIndex > -1) {
			extension = name.substring(dotIndex + 1);
			name = name.substring(0, dotIndex);
		}
		if (name.contains("/")) {
			name = name.substring(name.lastIndexOf('/') + 1, name.length());
			if (name.contains(".")) {
				name = name.substring(0, name.lastIndexOf('.'));
			}
		}
		sb.append("<topic id='" + name + "'>");
		sb.append("<title>" + name + "</title>");
		Workbook workbook = createWorkbook(extension, is);
		int noSheets = 0;
		if(workbook != null) {
			noSheets = workbook.getNumberOfSheets();
		}
		sb.append("<body>");
		for (int i = 0; i < noSheets; i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);
			Iterator<Row> iterator = datatypeSheet.iterator();
			if (iterator.hasNext()) {
				sb.append("<table id='" + datatypeSheet.getSheetName().replace(' ', '_') + "'>");
				sb.append("<title>").append(datatypeSheet.getSheetName()).append("</title>");
				List<StringBuilder> rowsData = new ArrayList<StringBuilder>();
				// For each sheet we have a table
				int maxColCount = 0;
				while (iterator.hasNext()) {
					StringBuilder rowData = new StringBuilder();
					rowData.append("<row>");
					Row currentRow = iterator.next();
					Iterator<Cell> cellIterator = currentRow.iterator();
					int colCount = 0;
					while (cellIterator.hasNext()) {
						colCount++;
						Cell currentCell = cellIterator.next();
						rowData.append("<entry>");
						rowData.append(getImportRepresentation(currentCell, true).replace("<", "&lt;").replace("&", "&amp;"));
						rowData.append("</entry>");
					}
					if (colCount > maxColCount) {
						maxColCount = colCount;
					}
					rowData.append("</row>");
					rowsData.add(rowData);
				}
				sb.append("<tgroup cols='" + maxColCount + "'>");
				if (headerRowsNo > 0) {
					sb.append("<thead>");
					for (int j = 0; j < headerRowsNo && j < rowsData.size(); j++) {
						sb.append(rowsData.get(j));
					}
					sb.append("</thead>");
				}
				sb.append("<tbody>");
				for (int j = headerRowsNo; j < rowsData.size(); j++) {
					sb.append(rowsData.get(j));
				}
				sb.append("</tbody>");
				sb.append("</tgroup>");

				sb.append("</table>");
			}
		}
		sb.append("</body>");
		sb.append("</topic>");

		return sb.toString();
	}

	/**
	 * Create a workbook.
	 * 
	 * @param extension
	 *          The extension
	 * @param is
	 *          The input stream.
	 * @return The workbook
	 * @throws IOException
	 */
	private static Workbook createWorkbook(String extension, InputStream is) throws IOException {
		Workbook wb = null;
		try {
			if ("xlsx".equals(extension)) {
				// New XML-type Excel files.
				wb = new XSSFWorkbook(is);
			} else {
				POIFSFileSystem fs = new POIFSFileSystem(is);
				wb = new HSSFWorkbook(fs);
			}
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// Do nothing.
			}
		}
		return wb;
	}
	
	/**
	 * Check if a cell contains a date Since dates are stored internally in Excel
	 * as double values we infer it is a date if it is formatted as such.
	 * 
	 * @param cell
	 *          Excel cell to check.
	 * @return true if cell is succeptible of showing a date.
	 */
	private static boolean isCellDateFormatted(Cell cell) {
		// Starting with POI 3.5 we rely on a new mechanism to discover if the
		// number corresponds to a date or not.
		if (cell == null) {
			return false;
		}
		return DateUtil.isCellDateFormatted(cell);
	}

	/**
	 * Get the import string representation of the Excel cell.
	 * 
	 * @param cell
	 *          Excel cell to be represented.
	 * @param displayDataAsInExcel
	 *          <code>true</code> if the data will be displayed as it appears in
	 *          Excel, <code>false</code> if it will display the row data.
	 * @return String representation of the Excel cell.
	 */
	private static String getImportRepresentation(Cell cell, boolean displayDataAsInExcel) {
		String importPresentationString = "";
		DataFormatter df = null;
		if (cell != null) {
			df = new DataFormatter();
			if (displayDataAsInExcel && (cell.getCellType() == CellType.FORMULA)) {
				// We are trying to evaluate the formula.
				FormulaEvaluator fe = null;
				try {
					if (cell.getSheet().getWorkbook() instanceof HSSFWorkbook) {
						// Older Excel
						fe = new HSSFFormulaEvaluator((HSSFWorkbook) cell.getSheet().getWorkbook());
					} else {
						// Or newer 2007+
						fe = new XSSFFormulaEvaluator(new XSSFWorkbook());
					}
					fe.evaluateInCell(cell);
					// After evaluation, the cell will change its type.
				} catch (Exception e) {
					// EXM-21075 If we can't evaluate formula we fall back to the old
					// approach.
				  logger.warn("Could not evaluate the cell formula: " + e.getMessage(), e);
					// We were unable to evaluate the formula due an error, so we can't
					// show data as it appears in Excel.
					displayDataAsInExcel = false;
				}
			}
			switch (cell.getCellType()) {
			case NUMERIC:
				// Numeric types are succeptible of being dates/time too.
				boolean isDateCell = isCellDateFormatted(cell);
				if (isDateCell) {
					// For date, we prefer to have our own kind of representation.
					// Default value for date objects
					importPresentationString = getDescriptionOfDate(cell);
				} else {
					if (displayDataAsInExcel) {
						importPresentationString = df.formatCellValue(cell);
					} else {
						double doubleValue = cell.getNumericCellValue();
						int intValue = (int) doubleValue;
						if (doubleValue == intValue) {
							importPresentationString = Integer.toString(intValue);
						} else {
							importPresentationString = Double.toString(doubleValue);
						}
					}
				}
				break;
			case STRING:
				importPresentationString = displayDataAsInExcel ? df.formatCellValue(cell)
						: cell.getRichStringCellValue().getString();
				break;
			case BLANK:
				importPresentationString = "";
				break;
			case BOOLEAN:
				importPresentationString = displayDataAsInExcel ? df.formatCellValue(cell)
						: Boolean.toString(cell.getBooleanCellValue());
				break;
			case ERROR:
				importPresentationString = "#ERROR: " + Byte.toString(cell.getErrorCellValue());
				break;
			case FORMULA:
				importPresentationString = displayDataAsInExcel ? df.formatCellValue(cell) : getDescriptionOfFormula(cell);
				break;
			default:
				if (logger.isDebugEnabled()) {
					logger.debug("unsuported cell type " + cell.getCellType());
				}
				break;
			}
		}
		return importPresentationString;
	}

	/**
	 * Get the description of a cell having a Excel formula. We assume that the
	 * cell was already checked and it is a formula cell.
	 * 
	 * @param cell
	 *          Excel cell.
	 * @return Description of the cell having an Excel formula.
	 */
	private static String getDescriptionOfFormula(Cell cell) {
		String descr = "";
		switch (cell.getCachedFormulaResultType()) {
		case NUMERIC:
			// Numeric types are succeptible of being dates/time too.
			boolean isDateCell = isCellDateFormatted(cell);
			if (isDateCell) {
				// Default value for date objects
				descr = getDescriptionOfDate(cell);
			} else {
				double doubleValue = cell.getNumericCellValue();
				int intValue = (int) doubleValue;
				if (doubleValue == intValue) {
					descr = Integer.toString(intValue);
				} else {
					descr = Double.toString(doubleValue);
				}
			}
			break;
		case BOOLEAN:
			descr = Boolean.toString(cell.getBooleanCellValue());
			break;
		case ERROR:
			// The formula cannot be evaluated ...
			// Byte.toString(cell.getErrorCellValue()) is not an interesting value ..
			descr = "#ERROR: " + cell.getCellFormula();
			break;
		case STRING:
			descr = cell.getRichStringCellValue().getString();
			break;
		default:
			// Can't be of other type according to specs.
			break;
		}
		return descr;
	}

	/**
	 * Get the description of a cell representing a date cell.
	 * 
	 * @param cell
	 *          Date cell (we assume that this fact is valid)
	 * @return Description of the date cell.s
	 */
	private static String getDescriptionOfDate(Cell cell) {
		// This is unformatted, that is Excel type.
		DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(cell);
	}
}
