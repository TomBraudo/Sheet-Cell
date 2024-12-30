package command.impl;

import api.EngineOptions;
import command.api.Command;
import engine.SheetDTO;
import menu.SheetPrinter;

import java.util.ArrayList;
import java.util.Scanner;

public class ShowVersion implements Command {

    private final EngineOptions options;
    public ShowVersion(final EngineOptions options) {
        this.options = options;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        displayVersionsTable();
        while (true){
            System.out.println("Enter the version number to peek at, or press 0 to return to the main menu:");
            String version = scanner.nextLine();
            if (version.equals("0")){
                return;
            }
            try {
                int versionNum = Integer.parseInt(version);
                displayVersion(versionNum);
                return;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. please enter a number");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again");
            }
        }

    }

    private void displayVersionsTable(){
        ArrayList<SheetDTO> versionsData = options.getVersionsData();
        System.out.println("+-----------+------------------------+");
        System.out.println("| Version # | Number of Cells Changed |");
        System.out.println("+-----------+------------------------+");
        for (SheetDTO sheetDTO : versionsData) {
            System.out.printf("| %-9d | %-22d |\n", sheetDTO.getVersion(), sheetDTO.getNumOfCellChanged());
        }

        System.out.println("+-----------+------------------------+");
    }

    private void displayVersion(int versionNum){
        SheetDTO sheetDTO = options.getVersion(versionNum - 1);
        SheetPrinter.printSheet(sheetDTO);
    }
}
