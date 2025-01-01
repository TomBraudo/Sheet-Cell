package api;

import engine.CellDTO;
import engine.SheetDTO;
import expression.FunctionRegistry;
import sheet.Sheet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        return curSheet.getVersionsData();
    }

    //Method that returns a version by its #
    public SheetDTO getVersion(int version) {
        return curSheet.getVersion(version);
    }

    //Method that announce to the engine that the user ended editing the file, and it should save a version
    public void endEditingSession(){ curSheet.endEditingSession();}

    public void saveState(String filePath){

        File file = new File(filePath);
        try {
            if(file.isDirectory()) {
                file = new File(file, "state.ser");
            }

            file.getParentFile().mkdirs();

            if(!file.exists()) {
                file.createNewFile();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
                oos.writeObject(curSheet);
            } catch (IOException e){
                throw new RuntimeException("Failed to save state to file " + filePath, e);
            }
        } catch (IOException e){
            throw new RuntimeException("Failed to save state to file " + filePath, e);
        }


    }

    public void loadState(String filePath) {
        File file = new File(filePath);

        // Ensure the file exists
        if (!file.exists()) {
            throw new RuntimeException("File not found: " + filePath);
        }

        // Deserialize the sheet
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Sheet loadedSheet = (Sheet) ois.readObject();
            curSheet = loadedSheet;

            // Restore the FunctionRegistry state
            FunctionRegistry.setSheet(curSheet);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load state from file: " + filePath, e);
        }
    }

    public List<String> getDependents(String cellName) {
        return curSheet.getDependentsNames(cellName);
    }
}
