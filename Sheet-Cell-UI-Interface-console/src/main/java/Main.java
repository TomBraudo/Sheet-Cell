import api.EngineOptions;
import menu.MainMenu;

public class Main {
    public static void main(String[] args)
    {
        EngineOptions options = new EngineOptions();
        MainMenu menu = new MainMenu(options);
        menu.show();
    }
}