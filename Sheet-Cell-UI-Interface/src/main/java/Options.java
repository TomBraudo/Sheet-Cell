import com.options.api.*;

public class Options {
    private final Sheet sheet;

    public Options(String filePath) {
        sheet = new Sheet(filePath); // Load the sheet from the XML file
    }

    public void ShowSheet() {
    }
}
