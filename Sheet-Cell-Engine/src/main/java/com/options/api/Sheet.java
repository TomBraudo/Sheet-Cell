package com.options.api;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class Sheet {
    private Cell[][] sheet;
    private String sheetName;
    private int rows;
    private int columns;

    // Constructor to create a sheet from a file
    public Sheet(String filePath) {
        createSheet(filePath);
        FunctionRegistry.setSheet(this);
    }

    // Constructor to create an empty sheet
    public Sheet(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        sheet = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sheet[i][j] = new Cell(getCellName(i, j), "");
            }
        }
        FunctionRegistry.setSheet(this);
    }

    // Method to initialize the sheet from an XML file
    private void createSheet(String filePath) {
        try {
            File file = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Normalize the document
            doc.getDocumentElement().normalize();

            // Parse the root element for sheet details
            Element root = doc.getDocumentElement();
            sheetName = root.getAttribute("name");

            // Extract layout details
            NodeList layoutList = root.getElementsByTagName("STL-Layout");
            if (layoutList.getLength() > 0) {
                Element layout = (Element) layoutList.item(0);
                rows = Integer.parseInt(layout.getAttribute("rows"));
                columns = Integer.parseInt(layout.getAttribute("columns"));
            }

            // Initialize the sheet
            sheet = new Cell[rows][columns];

            // Populate cells from the XML
            NodeList cellList = doc.getElementsByTagName("STL-Cell");
            for (int i = 0; i < cellList.getLength(); i++) {
                Element cellElement = (Element) cellList.item(i);
                int row = Integer.parseInt(cellElement.getAttribute("row")) - 1; // Convert to 0-based
                String col = cellElement.getAttribute("column").toUpperCase();
                int colIndex = col.charAt(0) - 'A'; // Convert letter to index

                // Extract cell value
                String value = cellElement.getElementsByTagName("STL-Original-Value").item(0).getTextContent();

                // Create the cell
                sheet[row][colIndex] = new Cell(getCellName(row, colIndex), value);

            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating sheet from file: " + filePath, e);
        }
    }

    // Getters
    public String getSheetName() {
        return sheetName;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public String getCellValue(String cellName) {
        int[] indices = getCellIndices(cellName);
        Cell cell = sheet[indices[0]][indices[1]];
        return cell != null ? cell.getValue().toString() : null;
    }

    public void setCell(String cellName, String value) {
        int[] indices = getCellIndices(cellName);
        if (sheet[indices[0]][indices[1]] == null) {
            sheet[indices[0]][indices[1]] = new Cell(cellName, value);
        } else {
            sheet[indices[0]][indices[1]].setValue(value);
        }
    }

    // Convert "A1" format to row and column indices
    private int[] getCellIndices(String cellName) {
        char rowChar = cellName.charAt(0); // Row as a letter
        int col = Integer.parseInt(cellName.substring(1)) - 1; // Column as a 1-based number, convert to 0-based
        int row = rowChar - 'A'; // Convert row letter to 0-based index
        return new int[]{row, col};
    }


    // Convert row and column indices to "A1" format
    private String getCellName(int row, int col) {
        return (char) ('A' + row) + Integer.toString(col + 1);
    }

}
