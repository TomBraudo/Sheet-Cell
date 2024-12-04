import java.lang.StringBuilder;

public enum MainUserChoices {
    LOAD_NEW_SHEET,
    DISPLAY_SHEET,
    DISPLAY_SINGLE_CELL,
    CHANGE_CELL_VALUE,
    DISPLAY_VERSIONS;

    public int argsNeeded() {
        switch (this) {
            case LOAD_NEW_SHEET:
            case DISPLAY_SINGLE_CELL:
                return 1;
            case DISPLAY_SHEET:
            case DISPLAY_VERSIONS:
                return 0;
            case CHANGE_CELL_VALUE:
                return 2;
            default:
                throw new AssertionError();
        }
    }

    public static MainUserChoices getUserChoice(String choice){
        return MainUserChoices.values()[Integer.parseInt(choice)- 1];
    }

    public static String getOptions() {
        StringBuilder sb = new StringBuilder();
        for (MainUserChoices choice : MainUserChoices.values()) {
            sb.append(String.format("%d. " + choice.name() + "\n", choice.ordinal() + 1));
        }

        return sb.toString();
    }
}
