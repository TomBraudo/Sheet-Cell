package com.options.api;

public class EngineOptions {
    private final Sheet curSheet;

    public EngineOptions(String filePath) {
        this.curSheet = new Sheet(filePath); // Initialize Sheet with the provided file path
    }

    public String getCellValue(String cellName) {
        return curSheet.getCellValue(cellName);
    }

    public void setCellValue(String cellName, String value) {
        curSheet.setCell(cellName, value);
    }
}
