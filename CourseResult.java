class CourseResult {
    private final String code;
    private final String name;
    private final int credits;
    private final int semester;
    private double marks;

    public CourseResult(String code, String name, int credits, double marks) {
        this(code, name, credits, marks, 1);
    }

    public CourseResult(String code, String name, int credits, double marks, int semester) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.semester = semester;
        this.marks = marks;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public int getSemester() {
        return semester;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public String getGrade() {
        if (marks >= 90) return "A";
        if (marks >= 80) return "B";
        if (marks >= 70) return "C";
        if (marks >= 60) return "D";
        return "F";
    }

    public double getGradePoint() {
        if (marks >= 90) return 4.0;
        if (marks >= 80) return 3.5;
        if (marks >= 70) return 3.0;
        if (marks >= 60) return 2.5;
        return 0.0;
    }

    public boolean isPassed() {
        return marks >= 60;
    }

    public String catalogDisplay() {
        return code + " - " + name + " | Credits: " + credits;
    }

    public String resultDisplay() {
        return code + " - " + name + " | Semester: " + semester + " | Credits: " + credits + " | Marks: " + marks + " | Grade: " + getGrade();
    }

    public String display() {
        return resultDisplay();
    }
}
