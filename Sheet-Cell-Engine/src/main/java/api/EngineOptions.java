package api;

import engine.CellDTO;
import engine.SheetDTO;
import sheet.Sheet;

import java.util.ArrayList;

public class EngineOptions {
    private static Sheet curSheet = null;

    public void SetNewSheet(String filePath) { curSheet = new Sheet(filePath);}

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

    public SheetDTO getCurSheet() {
        return curSheet.getCurrentVersion();
    }

    public void setCellValue(String cellName, String value) {
        curSheet.setCell(cellName, value);
    }

    public ArrayList<SheetDTO> getVersionsData(){
        return Sheet.getVersionsData();
    }

    public SheetDTO getVersion(int version) {
        return Sheet.getVersion(version);
    }

    public void endEditingSession(){ curSheet.endEditingSession();}
}
