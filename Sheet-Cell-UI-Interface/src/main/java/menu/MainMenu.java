package menu;

import api.EngineOptions;
import command.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private static List<MenuItem> menuItems;
    private final EngineOptions options;
    private final String menuDisplay;

    public MainMenu(EngineOptions options) {
        this.options = options;
        menuItems = new ArrayList<MenuItem>();
        initializeMenuItems();
        this.menuDisplay = buildMenuDisplay();

    }

    private String buildMenuDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("**%s**%n", "Shticell Menu"));
        sb.append("-----------------------").append(System.lineSeparator());
        int i = 1;
        for (MenuItem item : menuItems) {
            sb.append(String.format("%d -> %s%n", i++, item.getName()));
        }
        sb.append("-----------------------").append(System.lineSeparator());
        return sb.toString();
    }

    private void initializeMenuItems() {
        menuItems.add(new MenuItem("Load Xml File", new LoadXmlFile(options), this));
        menuItems.add(new MenuItem("Display Sheet", new DisplaySheet(options), this));
        menuItems.add(new MenuItem("Show Cell Value", new ShowCellValue(options), this));
        menuItems.add(new MenuItem("Update Cell Value", new UpdateCellValue(options), this));
        menuItems.add(new MenuItem("Show Version", new ShowVersion(options), this));
        menuItems.add(new MenuItem("Exit", new ExitCommand(), this));
    }

    public void show() {
        System.out.println(menuDisplay);
        askForChoice();
    }

    private void askForChoice() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.printf("Enter your request: (1 to %d)%n", menuItems.size());

            if(!scanner.hasNextInt()) {
                System.out.println("Invalid input, please enter a number");
                scanner.next();
                continue;
            }

            choice = scanner.nextInt();
            scanner.nextLine();
            if (choice < 1 || choice > menuItems.size()) {
                System.out.println("Invalid input, please try again");
                continue;
            }

            if(!menuItems.get(choice - 1).getName().equalsIgnoreCase("Load Xml File")
                    && options.getVersionsData() == null
                    && options.getCurSheet() == null) {
                System.out.println("Please load an XML file or load system state before using the other options.");
                continue;
            }

            menuItems.get(choice - 1).DoAction();
            break;
        }
    }
}
