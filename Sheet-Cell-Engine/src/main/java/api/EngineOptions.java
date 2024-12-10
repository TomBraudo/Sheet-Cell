package api;

import engine.CellDTO;
import engine.SheetDTO;
import sheet.Sheet;

import java.util.ArrayList;

//The only class available for the UI layer, all requests come through here
public class EngineOptions {
    //The current sheet being worked on
    private static Sheet curSheet = null;

    //Replacing the working sheet with another sheet created from an XML file
    public void SetNewSheet(String filePath) { curSheet = new Sheet(filePath);}

    //Method that returns a cellDTO to the UI from the location
    public CellDTO getCellData(String cellName) {
        return curSheet.getCellData(cellName);
    }

    //Method that returns the current sheet (or null if not yet created a sheet)
    public SheetDTO getCurSheet() {
        if(curSheet == null) {
            return null;
        }
        return curSheet.getCurrentVersion();
    }

    //Method that sets a cell by location to a new value
    public void setCellValue(String cellName, String value) {
        curSheet.setCell(cellName, value);
    }

    //Method that returns all the different sheet versions of the current sheet
    public ArrayList<SheetDTO> getVersionsData(){
        return Sheet.getVersionsData();
    }

    //Method that returns a version by its #
    public SheetDTO getVersion(int version) {
        return Sheet.getVersion(version);
    }

    //Method that announce to the engine that the user ended editing the file, and it should save a version
    public void endEditingSession(){ curSheet.endEditingSession();}
}
