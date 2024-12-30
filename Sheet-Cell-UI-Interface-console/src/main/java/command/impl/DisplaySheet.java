package command.impl;

import command.api.Command;
import api.EngineOptions;
import engine.SheetDTO;
import menu.SheetPrinter;

public class DisplaySheet implements Command {

    private final EngineOptions options;
    public DisplaySheet(EngineOptions options) {
        this.options = options;
    }

    @Override
    public void execute() {
         SheetDTO sheet = options.getCurSheet();
         SheetPrinter.printSheet(sheet);
    }
}
