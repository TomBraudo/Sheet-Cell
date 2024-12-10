package menu;

import command.api.Command;
public class MenuItem {
    protected String name;
    protected Command command;
    protected MainMenu main;
    public MenuItem(String name, Command command, MainMenu main) {
        this.name = name;
        this.command = command;
        this.main = main;
    }

    public String getName() {
        return name;
    }
    protected  void DoAction() {
        StringBuilder toShow = new StringBuilder();
        toShow.append(String.format("**%s**%n", name));
        toShow.append("-----------------------");
        System.out.println();
        System.out.println(toShow);
        command.execute();
        System.out.println();
        main.show();
    }
}
