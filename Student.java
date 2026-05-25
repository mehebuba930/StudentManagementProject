import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

class Student extends User {
    private int id;
    private double marks;
    private Department department;
    private DegreeLevel degreeLevel;
    private int semester;
    private int completedCredits;
    private int lastRegisteredSemester;
    private ArrayList<CourseResult> courses;

    public Student(int id, String name, String username, String password, double marks) {
        this(id, name, username, password, Department.CSE, DegreeLevel.BACHELOR, 1, 0, 0, new ArrayList<>());
        this.marks = marks;
    }

    public Student(int id, String name, String username, String password, Department department, DegreeLevel degreeLevel, int semester, int completedCredits, int lastRegisteredSemester, ArrayList<CourseResult> courses) {
        super(name, username, password);
        this.id = id;
        this.department = department;
        this.degreeLevel = degreeLevel;
        this.semester = semester;
        this.completedCredits = completedCredits;
        this.lastRegisteredSemester = lastRegisteredSemester;
        this.courses = courses == null ? new ArrayList<>() : new ArrayList<>(courses);
        recalculateAcademicSummary();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public double getMarks() {
        return marks;
    }

    public Department getDepartment() {
        return department;
    }

    public DegreeLevel getDegreeLevel() {
        return degreeLevel;
    }

    public int getSemester() {
        return semester;
    }

    public int getCompletedCredits() {
        return completedCredits;
    }

    public int getLastRegisteredSemester() {
        return lastRegisteredSemester;
    }

    public ArrayList<CourseResult> getCourses() {
        return new ArrayList<>(courses);
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setDegreeLevel(DegreeLevel degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public void setCompletedCredits(int completedCredits) {
        this.completedCredits = completedCredits;
    }

    public void setLastRegisteredSemester(int lastRegisteredSemester) {
        this.lastRegisteredSemester = lastRegisteredSemester;
    }

    public void setCourses(ArrayList<CourseResult> courses) {
        this.courses = courses == null ? new ArrayList<>() : new ArrayList<>(courses);
        recalculateAcademicSummary();
    }

    public boolean hasCourse(String courseCode) {
        return findCourse(courseCode) != null;
    }

    public void addCourses(ArrayList<CourseResult> newCourses) {
        if (newCourses == null || newCourses.isEmpty()) {
            return;
        }

        ArrayList<CourseResult> mergedCourses = new ArrayList<>(courses);
        for (CourseResult course : newCourses) {
            if (!hasCourse(course.getCode())) {
                mergedCourses.add(course);
            }
        }

        setCourses(mergedCourses);
    }

    public boolean addCourse(CourseResult course) {
        if (course == null || hasCourse(course.getCode())) {
            return false;
        }

        courses.add(course);
        recalculateAcademicSummary();
        return true;
    }

    public boolean removeCourse(String courseCode) {
        boolean removed = courses.removeIf(course -> course.getCode().equalsIgnoreCase(courseCode));
        if (removed) {
            recalculateAcademicSummary();
        }
        return removed;
    }

    public boolean updateCourseMarks(String courseCode, double newMarks) {
        for (CourseResult course : courses) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                course.setMarks(newMarks);
                recalculateAcademicSummary();
                return true;
            }
        }
        return false;
    }

    public CourseResult findCourse(String courseCode) {
        for (CourseResult course : courses) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                return course;
            }
        }
        return null;
    }

    public boolean hasAnyRegisteredCourses() {
        return courses != null && !courses.isEmpty();
    }

    public boolean canRegisterSuggestedCourses() {
        return semester != lastRegisteredSemester;
    }

    public void markSuggestedCoursesRegistered() {
        lastRegisteredSemester = semester;
    }

    public void displaySuggestedCourses() {
        ArrayList<CourseResult> suggestedCourses = CurriculumCatalog.getSuggestedCourses(department, degreeLevel, semester);
        if (suggestedCourses.isEmpty()) {
            System.out.println("No suggested courses found for this student.");
            return;
        }

        System.out.println("Suggested courses for Semester " + semester + ":");
        for (CourseResult course : suggestedCourses) {
            System.out.println("- " + course.catalogDisplay());
        }
    }

    public ArrayList<CourseResult> getSuggestedCourses() {
        return CurriculumCatalog.getSuggestedCourses(department, degreeLevel, semester);
    }

    public void displayResultsSemesterWise() {
        if (courses == null || courses.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

        Map<Integer, ArrayList<CourseResult>> coursesBySemester = new TreeMap<>();
        for (CourseResult course : courses) {
            coursesBySemester.computeIfAbsent(course.getSemester(), ignored -> new ArrayList<>()).add(course);
        }

        for (Map.Entry<Integer, ArrayList<CourseResult>> entry : coursesBySemester.entrySet()) {
            int semesterNumber = entry.getKey();
            ArrayList<CourseResult> semesterCourses = entry.getValue();

            double totalQualityPoints = 0.0;
            double totalCredits = 0.0;

            System.out.println("Semester " + semesterNumber + " Results:");
            for (CourseResult course : semesterCourses) {
                System.out.println("  - " + course.resultDisplay());
                totalQualityPoints += course.getGradePoint() * course.getCredits();
                totalCredits += course.getCredits();
            }

            double semesterCgpa = totalCredits == 0.0 ? 0.0 : totalQualityPoints / totalCredits;
            System.out.println("  Semester CGPA: " + semesterCgpa);
        }

        System.out.println("Overall Completed Credits: " + completedCredits);
        System.out.println("Overall CGPA: " + getCgpa());
    }

    public void displayCoursesSemesterWise() {
        System.out.println("Student ID: " + id + ", Name: " + name + ", Department: " + department.getDisplayName() + ", Level: " + degreeLevel.getDisplayName() + ", Current Semester: " + semester);

        if (courses == null || courses.isEmpty()) {
            System.out.println("No registered courses found for this student.");
            return;
        }

        Map<Integer, ArrayList<CourseResult>> coursesBySemester = new TreeMap<>();
        for (CourseResult course : courses) {
            coursesBySemester.computeIfAbsent(course.getSemester(), ignored -> new ArrayList<>()).add(course);
        }

        for (Map.Entry<Integer, ArrayList<CourseResult>> entry : coursesBySemester.entrySet()) {
            System.out.println("Semester " + entry.getKey() + " Courses:");
            for (CourseResult course : entry.getValue()) {
                System.out.println("  - " + course.resultDisplay());
            }
        }
    }

    public void recalculateAcademicSummary() {
        if (courses == null || courses.isEmpty()) {
            marks = 0.0;
            completedCredits = 0;
            return;
        }

        double totalMarksWeight = 0.0;
        double totalCredits = 0.0;
        int passedCredits = 0;

        for (CourseResult course : courses) {
            totalMarksWeight += course.getMarks() * course.getCredits();
            totalCredits += course.getCredits();

            if (course.isPassed()) {
                passedCredits += course.getCredits();
            }
        }

        marks = totalCredits == 0.0 ? 0.0 : totalMarksWeight / totalCredits;
        completedCredits = passedCredits;
    }

    // Grade calculation
    public String getGrade() {
        if (marks >= 90) return "A";
        else if (marks >= 80) return "B";
        else if (marks >= 70) return "C";
        else if (marks >= 60) return "D";
        else return "F";
    }

    // GPA calculation
    public double getGPA() {
        return getCgpa();
    }

    public double getCgpa() {
        if (courses == null || courses.isEmpty()) {
            return 0.0;
        }

        double totalQualityPoints = 0.0;
        double totalCredits = 0.0;

        for (CourseResult course : courses) {
            totalQualityPoints += course.getGradePoint() * course.getCredits();
            totalCredits += course.getCredits();
        }

        return totalCredits == 0.0 ? 0.0 : totalQualityPoints / totalCredits;
    }

    @Override
    void display() {
        System.out.println("ID: " + id +
                ", Name: " + name +
                ", Username: " + username +
                ", Department: " + department.getDisplayName() +
                ", Level: " + degreeLevel.getDisplayName() +
                ", Semester: " + semester +
                ", Completed Credits: " + completedCredits +
                ", Overall Marks: " + marks +
                ", Grade: " + getGrade() +
                ", CGPA: " + getCgpa());
    }
}