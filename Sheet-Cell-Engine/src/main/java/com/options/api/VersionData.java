package com.options.api;

public class VersionData {
    private final int version;
    private final int numOfCellChanged;
    private final String[][] effectiveDataOfCells;
    public VersionData(int version, int numOfCellChanged, String[][] effectiveDataOfCells) {
        this.version = version;
        this.numOfCellChanged = numOfCellChanged;
        this.effectiveDataOfCells = effectiveDataOfCells;
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
