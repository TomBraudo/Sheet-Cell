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

    public Sheet getCurSheet() {
        return curSheet;
    }
    public void setCellValue(String cellName, String value) {
        curSheet.setCell(cellName, value);
    }
}
