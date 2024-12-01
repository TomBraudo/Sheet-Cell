import java.util.List;

public class Sheet {
    Cell[][] sheet;
    String sheetName;
    int rows;
    int columns;
    int columnsWidthUnits;
    int rowsHeightUnits;
    public Sheet(Cell[][] sheet, String sheetName, int rows, int columns, int columnsWidthUnits, int rowsHeightUnits) {
        this.sheet = sheet;
        this.sheetName = sheetName;
        this.rows = rows;
        this.columns = columns;
        this.columnsWidthUnits = columnsWidthUnits;
        this.rowsHeightUnits = rowsHeightUnits;
    }

    public Cell[][] getSheet() {
        return sheet;
    }

    public void setCell(int row, int col, String value) {
        sheet[row-1][col-1].setValue(value);
    }

    public void ShowSheet() {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < columns; col++) {

            }
        }
    }
}
