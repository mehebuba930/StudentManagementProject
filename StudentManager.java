import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class StudentManager {
    private ArrayList<Student> students = new ArrayList<>();
    private final String FILE_NAME = "students.txt";
    private final String LEGACY_FILE_NAME = "students.dat";

    public boolean isValidMarks(double marks) {
        return marks >= 0 && marks <= 100;
    }

    // Load from file
    public void loadFromFile() {
        if (loadFromTextFile(FILE_NAME)) {
            return;
        }

        if (loadFromLegacyBinary(LEGACY_FILE_NAME)) {
            saveToFile();
            return;
        }

        System.out.println("No previous data found.");
    }

    // Save to file
    public void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(FILE_NAME))) {
            for (Student student : students) {
                out.println(serializeStudent(student));
            }
        } catch (Exception e) {
            System.out.println("Error saving data.");
        }
    }

    private boolean loadFromTextFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        ArrayList<Student> loadedStudents = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                List<String> parts = splitEscaped(line, '|');
                if (parts.size() == 5) {
                    int id = Integer.parseInt(parts.get(0));
                    String name = unescape(parts.get(1));
                    String username = unescape(parts.get(2));
                    String password = unescape(parts.get(3));
                    double marks = Double.parseDouble(parts.get(4));
                    loadedStudents.add(new Student(id, name, username, password, marks));
                } else if (parts.size() >= 9) {
                    int id = Integer.parseInt(parts.get(0));
                    String name = unescape(parts.get(1));
                    String username = unescape(parts.get(2));
                    String password = unescape(parts.get(3));
                    Department department = Department.valueOf(parts.get(4));
                    DegreeLevel degreeLevel = DegreeLevel.valueOf(parts.get(5));
                    int semester = Integer.parseInt(parts.get(6));
                    int completedCredits = Integer.parseInt(parts.get(7));
                    ArrayList<CourseResult> courses = deserializeCourses(parts.get(8), semester);
                    int lastRegisteredSemester = parts.size() >= 10 ? Integer.parseInt(parts.get(9)) : 0;
                    loadedStudents.add(new Student(id, name, username, password, department, degreeLevel, semester, completedCredits, lastRegisteredSemester, courses));
                }
            }

            students = loadedStudents;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean loadFromLegacyBinary(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            students = (ArrayList<Student>) in.readObject();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String serializeStudent(Student student) {
        return student.getId() + "|"
                + escape(student.getName()) + "|"
                + escape(student.getUsername()) + "|"
                + escape(student.password) + "|"
                + student.getDepartment().name() + "|"
                + student.getDegreeLevel().name() + "|"
                + student.getSemester() + "|"
                + student.getCompletedCredits() + "|"
            + serializeCourses(student.getCourses()) + "|"
            + student.getLastRegisteredSemester();
    }

    private String serializeCourses(ArrayList<CourseResult> courses) {
        ArrayList<String> encodedCourses = new ArrayList<>();
        for (CourseResult course : courses) {
            encodedCourses.add(escape(course.getCode()) + "," + escape(course.getName()) + "," + course.getCredits() + "," + course.getMarks() + "," + course.getSemester());
        }
        return String.join(";", encodedCourses);
    }

    private ArrayList<CourseResult> deserializeCourses(String encodedCourses, int defaultSemester) {
        ArrayList<CourseResult> courses = new ArrayList<>();
        if (encodedCourses == null || encodedCourses.isBlank()) {
            return courses;
        }

        List<String> courseParts = splitEscaped(encodedCourses, ';');
        for (String coursePart : courseParts) {
            List<String> fields = splitEscaped(coursePart, ',');
            if (fields.size() != 4 && fields.size() != 5) {
                continue;
            }

            String code = unescape(fields.get(0));
            String name = unescape(fields.get(1));
            int credits = Integer.parseInt(fields.get(2));
            double marks = Double.parseDouble(fields.get(3));
            int semester = fields.size() == 5 ? Integer.parseInt(fields.get(4)) : defaultSemester;
            courses.add(new CourseResult(code, name, credits, marks, semester));
        }

        return courses;
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace(";", "\\;")
                .replace(",", "\\,");
    }

    private String unescape(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaping = false;

        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (escaping) {
                builder.append(character);
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    private List<String> splitEscaped(String value, char delimiter) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (escaping) {
                current.append(character);
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else if (character == delimiter) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        parts.add(current.toString());
        return parts;
    }

    // Add
    public boolean addStudent(Student s) {
        if (!isValidMarks(s.getMarks())) {
            System.out.println("Marks must be between 0 and 100.");
            return false;
        }

        if (searchStudent(s.getId()) != null || searchStudentByUsername(s.getUsername()) != null) {
            return false;
        }

        students.add(s);
        saveToFile();
        System.out.println("Student added successfully!");
        return true;
    }

    // Display
    public void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        for (Student s : students) {
            s.displayCoursesSemesterWise();
        }
    }

    // Search
    public Student searchStudent(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public Student searchStudentByUsername(String username) {
        for (Student s : students) {
            if (s.getUsername().equalsIgnoreCase(username)) {
                return s;
            }
        }
        return null;
    }

    public boolean isStudentUsernameTaken(String username) {
        return searchStudentByUsername(username) != null;
    }

    public boolean isStudentUsernameTaken(String username, int ignoreId) {
        for (Student s : students) {
            if (s.getId() != ignoreId && s.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public Student authenticateStudent(String username, String password) {
        Student student = searchStudentByUsername(username);
        if (student != null && student.checkPassword(password)) {
            return student;
        }
        return null;
    }

    public ArrayList<CourseResult> getSuggestedCoursesForStudent(int id) {
        Student student = searchStudent(id);
        if (student == null) {
            return new ArrayList<>();
        }

        return CurriculumCatalog.getSuggestedCourses(student.getDepartment(), student.getDegreeLevel(), student.getSemester());
    }

    public boolean registerSuggestedCourses(int id) {
        Student student = searchStudent(id);
        if (student == null) {
            System.out.println("Student not found!");
            return false;
        }

        if (!student.canRegisterSuggestedCourses()) {
            System.out.println("Suggested courses have already been registered for this semester.");
            return false;
        }

        ArrayList<CourseResult> suggestedCourses = getSuggestedCoursesForStudent(id);
        if (suggestedCourses.isEmpty()) {
            System.out.println("No suggested courses found for this department and semester.");
            return false;
        }

        ArrayList<CourseResult> semesterCourses = new ArrayList<>();
        for (CourseResult course : suggestedCourses) {
            semesterCourses.add(new CourseResult(course.getCode(), course.getName(), course.getCredits(), 0.0, student.getSemester()));
        }

        student.addCourses(semesterCourses);
        student.markSuggestedCoursesRegistered();
        saveToFile();
        System.out.println("Suggested courses added successfully!");
        return true;
    }

    public boolean addCourseToStudent(int id, String courseCode, String courseName, int credits) {
        Student student = searchStudent(id);
        if (student == null) {
            System.out.println("Student not found!");
            return false;
        }

        boolean added = student.addCourse(new CourseResult(courseCode, courseName, credits, 0.0, student.getSemester()));
        if (added) {
            saveToFile();
            System.out.println("Course added successfully!");
            return true;
        }

        System.out.println("Course already exists.");
        return false;
    }

    public boolean removeCourseFromStudent(int id, String courseCode) {
        Student student = searchStudent(id);
        if (student == null) {
            System.out.println("Student not found!");
            return false;
        }

        boolean removed = student.removeCourse(courseCode);
        if (removed) {
            saveToFile();
            System.out.println("Course removed successfully!");
            return true;
        }

        System.out.println("Course not found.");
        return false;
    }

    // Update
    public boolean updateStudentMarks(int id, double marks) {
        if (!isValidMarks(marks)) {
            System.out.println("Marks must be between 0 and 100.");
            return false;
        }

        Student s = searchStudent(id);
        if (s != null) {
            s.setMarks(marks);
            saveToFile();
            System.out.println("Student updated successfully!");
            return true;
        }

        System.out.println("Student not found!");
        return false;
    }

    public boolean updateStudentCourseMarks(int id, String courseCode, double marks) {
        if (!isValidMarks(marks)) {
            System.out.println("Marks must be between 0 and 100.");
            return false;
        }

        Student student = searchStudent(id);
        if (student == null) {
            System.out.println("Student not found!");
            return false;
        }

        boolean updated = student.updateCourseMarks(courseCode, marks);
        if (updated) {
            student.recalculateAcademicSummary();
            saveToFile();
            System.out.println("Course marks updated successfully!");
            return true;
        }

        System.out.println("Course not found!");
        return false;
    }

    public boolean updateStudentAcademicProfile(int id, Department department, DegreeLevel degreeLevel, int semester, ArrayList<CourseResult> courses) {
        Student student = searchStudent(id);
        if (student == null) {
            System.out.println("Student not found!");
            return false;
        }

        student.setDepartment(department);
        student.setDegreeLevel(degreeLevel);
        student.setSemester(semester);
        if (courses != null) {
            student.setCourses(courses);
        }
        if (semester != student.getLastRegisteredSemester()) {
            student.setLastRegisteredSemester(0);
        }
        saveToFile();
        System.out.println("Academic profile updated successfully!");
        return true;
    }

    public boolean updateStudentDetails(int id, String name, String username, String password) {
        Student s = searchStudent(id);
        if (s != null) {
            if (isStudentUsernameTaken(username, id)) {
                System.out.println("Username already exists for another student.");
                return false;
            }

            s.setName(name);
            s.setUsername(username);
            s.setPassword(password);
            saveToFile();
            System.out.println("Student details updated successfully!");
            return true;
        }

        System.out.println("Student not found!");
        return false;
    }

    // Delete
    public void deleteStudent(int id) {
        boolean removed = students.removeIf(s -> s.getId() == id);
        if (removed) {
            saveToFile();
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Student not found!");
        }
    }

    // Sort by Marks
    public void sortByMarks() {
        students.sort(Comparator.comparingDouble(Student::getMarks).reversed());
        System.out.println("Sorted by Marks (Highest First):");
        displayStudents();
    }

    // Sort by GPA
    public void sortByGPA() {
        students.sort(Comparator.comparingDouble(Student::getGPA).reversed());
        System.out.println("Sorted by GPA:");
        displayStudents();
    }
}
