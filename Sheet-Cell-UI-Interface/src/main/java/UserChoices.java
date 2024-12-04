import java.lang.StringBuilder;

public enum UserChoices {
    LOAD_NEW_SHEET,
    DISPLAY_SHEET,
    DISPLAY_SINGLE_CELL,
    CHANGE_CELL_VALUE;

    public int argsNeeded() {
        switch (this) {
            case LOAD_NEW_SHEET:
            case DISPLAY_SINGLE_CELL:
                return 1;
            case DISPLAY_SHEET:
                return 0;
            case CHANGE_CELL_VALUE:
                return 2;
            default:
                throw new AssertionError();
        }
    }

    public static UserChoices getUserChoice(String choice){
        return UserChoices.values()[Integer.parseInt(choice)- 1];
    }

    public static String getOptions() {
        StringBuilder sb = new StringBuilder();
        for (UserChoices choice : UserChoices.values()) {
            sb.append(String.format("%d. " + choice.name() + "\n", choice.ordinal() + 1));
        }

        return sb.toString();
    }
}
