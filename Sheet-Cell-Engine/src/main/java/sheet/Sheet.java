package sheet;

import expression.FunctionRegistry;
import engine.CellDTO;
import engine.SheetDTO;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Sheet implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DependencyGraph dependencyGraph = new DependencyGraph();
    private Cell[][] sheet;
    private String sheetName;
    private int rows;
    private int columns;
    private int rowsHeight;
    private int columnWidth;

    private static final ArrayList<SheetDTO> versions = new ArrayList<>();
    private int currentVersion;
    private int curNumberOfCellsChanged;

    // Constructor to create a sheet from a file
    public Sheet(String filePath) {
        versions.clear();
        createSheet(filePath);
        currentVersion = 1;
        curNumberOfCellsChanged = 0;
        endEditingSession();
    }

    public static ArrayList<SheetDTO> getVersionsData() {
        return versions;
    }

    public static SheetDTO getVersion(int requestedVersion){
        return versions.get(requestedVersion);
    }

    public SheetDTO getCurrentVersion(){
        return new SheetDTO(currentVersion, curNumberOfCellsChanged, getEffectiveCellsData(), columnWidth, rowsHeight);
    }

    public void endEditingSession(){
        if(curNumberOfCellsChanged > 0) {
            versions.add(new SheetDTO(currentVersion++, curNumberOfCellsChanged, getEffectiveCellsData(), columnWidth, rowsHeight));
            curNumberOfCellsChanged = 0;
        }
    }

    private String[][] getEffectiveCellsData(){
        String[][] effectiveCellData = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(sheet[i][j] != null)
                    effectiveCellData[i][j] = sheet[i][j].getEffectiveValue().toString();
                else
                    effectiveCellData[i][j] = "";
            }
        }

        return effectiveCellData;
    }

    // Method to initialize the sheet from an XML file
    private void createSheet(String filePath) {
        try {
            File file = new File(filePath);

            // Check if the file has the correct XML extension
            if (!filePath.endsWith(".xml")) {
                throw new IllegalArgumentException("File is not an XML file: " + filePath);
            }

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

                // Validate rows and columns range
                if (rows < 1 || rows > 50) {
                    throw new IllegalArgumentException("Invalid number of rows: " + rows + ". Must be between 1 and 50.");
                }
                if (columns < 1 || columns > 20) {
                    throw new IllegalArgumentException("Invalid number of columns: " + columns + ". Must be between 1 and 20.");
                }

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

                // Validate cell position
                if (row < 0 || row >= rows || colIndex < 0 || colIndex >= columns) {
                    throw new IllegalArgumentException("Invalid cell position: Row " + (row + 1) + ", Column " + col);
                }

                // Extract cell value
                NodeList valueNodes = cellElement.getElementsByTagName("STL-Original-Value");
                if (valueNodes.getLength() == 0) {
                    throw new IllegalArgumentException("Missing STL-Original-Value for cell at Row " + (row + 1) + ", Column " + col);
                }
                String value = valueNodes.item(0).getTextContent();

                // Create the cell
                sheet[row][colIndex] = new Cell(getCellName(row, colIndex), value, this);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating sheet from file: " + filePath + "\n" + e.getMessage());
        }
    }


    public String getCellValue(String cellName) {
        int[] indices = getCellIndices(cellName);
        Cell cell = sheet[indices[0]][indices[1]];
        return cell != null ? cell.getEffectiveValue().toString() : null;
    }

    public String[][] getTableValues() {
        String[][] values = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                values[i][j] = sheet[i][j] != null ? sheet[i][j].getEffectiveValue().toString() : " ";
            }
        }

        return values;
    }

    public void setCell(String cellName, String value) {
        int[] indices = getCellIndices(cellName);
        if (sheet[indices[0]][indices[1]] == null) {
            sheet[indices[0]][indices[1]] = new Cell(cellName, value, this);
        } else {
            sheet[indices[0]][indices[1]].setValue(value);
        }

        curNumberOfCellsChanged++;
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

    static DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public CellDTO getCellData(String cellName) {
        int[] indices = getCellIndices(cellName);
        return sheet[indices[0]][indices[1]].getCellData();
    }
}
