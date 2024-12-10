package engine;

//Class that helps to expose a sheet to the UI layer
public class SheetDTO {
    private final int version;
    private final int numOfCellChanged;
    private final String[][] effectiveDataOfCells;
    private final int columnWidth;
    private final int rowHeight;
    public SheetDTO(int version, int numOfCellChanged, String[][] effectiveDataOfCells, int columnWidth, int rowHeight) {
        this.version = version;
        this.numOfCellChanged = numOfCellChanged;
        this.effectiveDataOfCells = effectiveDataOfCells;
        this.columnWidth = columnWidth;
        this.rowHeight = rowHeight;
    }

    public int getColumnWidth(){
        return columnWidth;
    }
    public int getRowHeight(){
        return rowHeight;
    }

    public String[][] getEffectiveDataOfCells() {
        return effectiveDataOfCells;
    }

    public int getVersion() {
        return version;
    }
    public int getNumOfCellChanged() {
        return numOfCellChanged;
    }
}
