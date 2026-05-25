enum Department {
    CSE("CSE"),
    EEE("EEE"),
    BBA("BBA"),
    ARCHITECTURE("Architecture");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Department fromChoice(int choice) {
        return switch (choice) {
            case 1 -> CSE;
            case 2 -> EEE;
            case 3 -> BBA;
            case 4 -> ARCHITECTURE;
            default -> null;
        };
    }
}
