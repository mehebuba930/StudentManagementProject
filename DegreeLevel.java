enum DegreeLevel {
    BACHELOR("Bachelor"),
    MASTER("Master");

    private final String displayName;

    DegreeLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DegreeLevel fromChoice(int choice) {
        return switch (choice) {
            case 1 -> BACHELOR;
            case 2 -> MASTER;
            default -> null;
        };
    }
}
