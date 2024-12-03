package com.options.api;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class Sheet {
    private static final DependencyGraph dependencyGraph = new DependencyGraph();
    private Cell[][] sheet;
    private String sheetName;
    private int rows;
    private int columns;
    private int rowsHeight;
    private int columnWidth;


    // Constructor to create a sheet from a file
    public Sheet(String filePath) {
        createSheet(filePath);
    }

    // Constructor to create an empty sheet
    public Sheet(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.rowsHeight = 20;
        this.columnWidth = 20;
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

                // Fetch the STL-Size element as a child of the layout element
                NodeList sizeList = layout.getElementsByTagName("STL-Size");
                if (sizeList.getLength() > 0) {
                    Element size = (Element) sizeList.item(0);
                    columnWidth = Integer.parseInt(size.getAttribute("column-width-units"));
                    rowsHeight = Integer.parseInt(size.getAttribute("rows-height-units"));
                } else {
                    throw new RuntimeException("STL-Size element is missing under STL-Layout.");
                }
            } else {
                throw new RuntimeException("STL-Layout element is missing.");
            }


            // Initialize the sheet
            sheet = new Cell[rows][columns];
            FunctionRegistry.setSheet(this);

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
                sheet[row][colIndex].setOwner(this);

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

    public int getRowsHeight() {
        return rowsHeight;
    }
    public int getColumnWidth() {
        return columnWidth;
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
            sheet[indices[0]][indices[1]].setOwner(this);
        } else {
            sheet[indices[0]][indices[1]].setValue(value);
        }
    }

    Cell getCell(String cellName) {
        int[] indices = getCellIndices(cellName);
        return sheet[indices[0]][indices[1]];
    }

    // Convert "A1" format to row and column indices
    private int[] getCellIndices(String cellName) {
        char colChar = cellName.charAt(0); // Column as a letter
        int row = Integer.parseInt(cellName.substring(1)) - 1; // Row as a 1-based number, convert to 0-based
        int col = colChar - 'A'; // Convert column letter to 0-based index
        if (!isCellPositionLegal(row, col)) {
            throw new RuntimeException("Invalid cell position: " + row + ", " + col);
        }
        return new int[]{row, col};
    }

    // Convert row and column indices to "A1" format
    private String getCellName(int row, int col) {
        if (!isCellPositionLegal(row, col)) {
            throw new RuntimeException("Invalid cell position: " + row + ", " + col);
        }

        return (char) ('A' + col) + Integer.toString(row + 1);
    }

    private boolean isCellPositionLegal (int row, int col) {
        return row >= 0 && row < this.rows && col >= 0 && col < this.columns;
    }

    public static DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }



}
