package com.options.api;

import java.util.ArrayList;
import java.util.List;

public class EngineOptions {
    private final Sheet curSheet;

    public EngineOptions(String filePath) {
        this.curSheet = new Sheet(filePath); // Initialize Sheet with the provided file path
    }

    public String getCellValue(String cellName) {
        return curSheet.getCellValue(cellName);
    }

    public CellData getCellData(String cellName) {
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

    public ArrayList<VersionData> getVersionsData(){
        return Sheet.getVersionsData();
    }

    public VersionData getVersion(int version) {
        return Sheet.getVersion(version);
    }
}
