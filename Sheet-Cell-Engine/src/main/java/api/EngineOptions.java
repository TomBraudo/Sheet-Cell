package api;

import engine.CellDTO;
import engine.VersionDTO;
import sheet.Sheet;

import java.util.ArrayList;

public class EngineOptions {
    private final Sheet curSheet;

    public EngineOptions(String filePath) {
        this.curSheet = new Sheet(filePath); // Initialize Sheet with the provided file path
    }
    public String getCellValue(String cellName) {
        return curSheet.getCellValue(cellName);
    }

    public CellDTO getCellData(String cellName) {
        return curSheet.getCellData(cellName);
    }
    public void changeCellValue(String cellName, String newValue) {
        curSheet.setCell(cellName, newValue);
    }

    public int getRowCount() {
        return curSheet.getRows();
    }
    public int getColumnCount() {
        return curSheet.getColumns();
    }
    public int getColumnWidth(){
        return curSheet.getColumnWidth();
    }
    public int getRowHeight() {
        return curSheet.getRowsHeight();
    }

    public String[][] getTableValues(){
        return curSheet.getTableValues();
    }

    public void setCellValue(String cellName, String value) {
        curSheet.setCell(cellName, value);
    }

    public ArrayList<VersionDTO> getVersionsData(){
        return Sheet.getVersionsData();
    }

    public VersionDTO getVersion(int version) {
        return Sheet.getVersion(version);
    }
}
