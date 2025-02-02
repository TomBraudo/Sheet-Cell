package sheet;

import expression.FunctionRegistry;
import engine.CellDTO;
import engine.SheetDTO;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Sheet implements Serializable {
    private static final long serialVersionUID = 1L;
    private final DependencyGraph dependencyGraph = new DependencyGraph();
    private Cell[][] sheet;
    private String sheetName;
    private int rows;
    private int columns;
    private int rowsHeight;
    private int columnWidth;
    private final ArrayList<SheetDTO> versions = new ArrayList<>();

    // Constructor to create a sheet from a file
    public Sheet(String filePath) {
        createSheet(filePath);
        endEditingSession();
    }
    public Sheet(String sheetName, int rows, int columns, int rowHeight, int columnWidth) {
        if (rows < 1 || rows > 50) {
            throw new IllegalArgumentException("Invalid number of rows: " + rows + ". Must be between 1 and 50.");
        }
        if (columns < 1 || columns > 20) {
            throw new IllegalArgumentException("Invalid number of columns: " + columns + ". Must be between 1 and 20.");
        }

        this.sheetName = sheetName;
        this.rows = rows;
        this.columns = columns;
        this.rowsHeight = rowHeight;
        this.columnWidth = columnWidth;

        // Initialize the blank sheet
        this.sheet = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String cellName = getCellName(i, j);
                this.sheet[i][j] = new Cell(cellName, "", this);
            }
        }

        // Clear dependency graph
        dependencyGraph.clear();

        // Add the initial version to the versions list
        endEditingSession();
    }


    public ArrayList<SheetDTO> getVersionsData() {
        return versions;
    }

    public SheetDTO getVersion(int requestedVersion){
        return versions.get(requestedVersion);
    }


    public void endEditingSession(){
        CellDTO[][] cellsDTO = new CellDTO[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = sheet[i][j];
                cellsDTO[i][j] = new CellDTO(cell.getCellData());
            }
        }

        versions.add(new SheetDTO(sheetName,versions.size(), cellsDTO, columnWidth, rowsHeight, saveDependenciesGraph()));
    }

    private Map<CellDTO, Set<CellDTO>> saveDependenciesGraph(){
        Map<CellDTO, Set<CellDTO>> savedDependenciesGraph = new HashMap<>();
        Map<Cell, Set<Cell>> currentDependenciesGraph = dependencyGraph.getDependenciesGraph();
        for (Map.Entry<Cell, Set<Cell>> entry : currentDependenciesGraph.entrySet()) {
            CellDTO cellData = entry.getKey().getCellData();
            savedDependenciesGraph.put(cellData, new HashSet<>());
            for (Cell cell : entry.getValue()) {
                savedDependenciesGraph.get(cellData).add(cell.getCellData());
            }
        }

        return savedDependenciesGraph;
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

            NodeList rangesList = root.getElementsByTagName("STL-Range");
            for (int i = 0; i < rangesList.getLength(); i++) {
                Element range = (Element) rangesList.item(i);
                String rangeName = range.getAttribute("name");
                NodeList boundariesList = range.getElementsByTagName("STL-Boundaries");
                Element boundary = (Element) boundariesList.item(0);
                String from = boundary.getAttribute("from");
                String to = boundary.getAttribute("to");
                FunctionRegistry.addRangeName(rangeName, from, to);
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

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (sheet[i][j] == null) {
                        sheet[i][j] = new Cell(getCellName(i, j), "", this);
                    }
                }
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

    public void setCell(String cellName, String value) {
        int[] indices = getCellIndices(cellName);
        if (sheet[indices[0]][indices[1]] == null) {
            sheet[indices[0]][indices[1]] = new Cell(cellName, value, this);
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

    DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public List<String> getDependentsNames(String cellName) {
        List<String> names = new ArrayList<>();
        int[] indices = getCellIndices(cellName);
        Cell cell = sheet[indices[0]][indices[1]];
        Set<Cell> dependents = dependencyGraph.getDependents(cell);
        for (Cell dependent : dependents) {
            names.add(dependent.getLocation());
        }

        return names;
    }

    public List<String> getDependentOn(String cellName) {
        List<String> names = new ArrayList<>();
        int[] indices = getCellIndices(cellName);
        Cell cell = sheet[indices[0]][indices[1]];
        Set<Cell> dependents = dependencyGraph.getDependencies(cell);
        for (Cell dependent : dependents) {
            names.add(dependent.getLocation());
        }

        return names;
    }

    public CellDTO getCellData(String cellName) {
        int[] indices = getCellIndices(cellName);
        if(sheet[indices[0]][indices[1]] == null) {
            return null;
        }
        return sheet[indices[0]][indices[1]].getCellData();
    }

    public List<Object> resolveRange(String range) {
        List<Object> values = new ArrayList<>();

        // Parse the range into start and end coordinates
        String[] parts = range.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

        String start = parts[0];
        String end = parts[1];

        int startRow = getCellIndices(start)[0];
        int startCol = getCellIndices(start)[1];
        int endRow = getCellIndices(end)[0];
        int endCol = getCellIndices(end)[1];
        // Iterate through the range and collect cell values
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                Cell cell = sheet[row][col];
                if (cell != null) {
                    values.add(cell.getEffectiveValue());
                }
            }
        }
        return values;
    }

    public void loadFromVersion(SheetDTO version) {
        // Restore the sheet structure
        this.sheetName = version.getName();
        this.rows = version.getVersionCells().length;
        this.columns = version.getVersionCells()[0].length;
        this.rowsHeight = version.getRowHeight();
        this.columnWidth = version.getColumnWidth();

        // Restore the cells
        this.sheet = new Cell[rows][columns];
        CellDTO[][] cellsDTO = version.getVersionCells();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cellsDTO[i][j] != null) {
                    this.sheet[i][j] = new Cell(cellsDTO[i][j].getLocation(), cellsDTO[i][j].getOriginalValue(), this);
                }
            }
        }

        // Restore the dependency graph
        this.dependencyGraph.clear();
        for (Map.Entry<CellDTO, Set<CellDTO>> entry : version.getDependencies().entrySet()) {
            Cell cell = this.getCell(entry.getKey().getLocation());
            for (CellDTO dependentDTO : entry.getValue()) {
                Cell dependentCell = this.getCell(dependentDTO.getLocation());
                this.dependencyGraph.addDependency(cell, dependentCell);
            }
        }
    }

}
