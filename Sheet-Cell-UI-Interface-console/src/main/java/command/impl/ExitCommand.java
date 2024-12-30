package command.impl;

import command.api.Command;

public class ExitCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Exiting...");
        System.exit(0);
    }
}
