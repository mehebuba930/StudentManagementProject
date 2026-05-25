import java.util.InputMismatchException;
import java.util.Scanner;

class TeacherConsoleService {
    private final Scanner sc;
    private final StudentManager studentManager;
    private final AccountManager accountManager;

    public TeacherConsoleService(Scanner sc, StudentManager studentManager, AccountManager accountManager) {
        this.sc = sc;
        this.studentManager = studentManager;
        this.accountManager = accountManager;
    }

    public void run() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        Teacher teacher = accountManager.authenticateTeacher(username, password);
        if (teacher == null) {
            System.out.println("Invalid teacher credentials!");
            return;
        }

        while (true) {
            try {
                System.out.println("\n===== Teacher Panel =====");
                System.out.println("1. Display Students");
                System.out.println("2. Search Student");
                System.out.println("3. Add Course to Student");
                System.out.println("4. Remove Course from Student");
                System.out.println("5. Update Course Marks");
                System.out.println("6. Logout");
                System.out.print("Enter your choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> studentManager.displayStudents();
                    case 2 -> searchStudent();
                    case 3 -> addCourseToStudent();
                    case 4 -> removeCourseFromStudent();
                    case 5 -> updateCourseMarks();
                    case 6 -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please try again.");
                sc.nextLine();
            }
        }
    }

    private void searchStudent() {
        System.out.print("Enter ID to search: ");
        int id = sc.nextInt();
        sc.nextLine();

        Student student = studentManager.searchStudent(id);
        if (student != null) {
            student.displayCoursesSemesterWise();
        } else {
            System.out.println("Student not found!");
        }
    }

    private void updateCourseMarks() {
        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = sc.nextLine();

        System.out.print("Enter new marks: ");
        double marks = sc.nextDouble();
        sc.nextLine();

        studentManager.updateStudentCourseMarks(id, courseCode, marks);
    }

    private void addCourseToStudent() {
        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = sc.nextLine();

        System.out.print("Enter Course Name: ");
        String courseName = sc.nextLine();

        System.out.print("Enter Credits: ");
        int credits = sc.nextInt();
        sc.nextLine();

        studentManager.addCourseToStudent(id, courseCode, courseName, credits);
    }

    private void removeCourseFromStudent() {
        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = sc.nextLine();

        studentManager.removeCourseFromStudent(id, courseCode);
    }
}
