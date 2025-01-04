package engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//Class that helps to expose a sheet to the UI layer
public class SheetDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final int version;
    private final CellDTO[][] cells;
    private final int columnWidth;
    private final int rowHeight;
    private final Map<CellDTO, Set<CellDTO>> dependencies;
    public SheetDTO(String name, int version, CellDTO[][] cells, int columnWidth, int rowHeight, Map<CellDTO, Set<CellDTO>> dependencies) {
        this.name = name;
        this.version = version;
        this.cells = cells;
        this.columnWidth = columnWidth;
        this.rowHeight = rowHeight;
        this.dependencies = dependencies;
    }

    public Map<CellDTO, Set<CellDTO>> getDependencies() {
        return dependencies;
    }

    public int getColumnWidth(){
        return columnWidth;
    }

    public int getRowHeight(){
        return rowHeight;
    }

    public CellDTO[][] getVersionCells() {
        return cells;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String[][] getEffectiveDataOfCells() {
        String[][] effectiveData = new String[cells.length][cells[0].length];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                effectiveData[i][j] = cells[i][j].getEffectiveValue();
            }
        }

        return effectiveData;
    }
}
