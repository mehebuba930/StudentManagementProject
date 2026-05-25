import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

class StudentConsoleService {
    private final Scanner sc;
    private final StudentManager studentManager;

    public StudentConsoleService(Scanner sc, StudentManager studentManager) {
        this.sc = sc;
        this.studentManager = studentManager;
    }

    public void run() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        Student student = studentManager.authenticateStudent(username, password);
        if (student == null) {
            System.out.println("Invalid student credentials!");
            return;
        }

        while (true) {
            try {
                System.out.println("\n===== Student Panel =====");
                System.out.println("1. View My Profile");
                System.out.println("2. View/Register Suggested Courses");
                System.out.println("3. View Courses Semester Wise");
                System.out.println("4. View Results Semester Wise");
                System.out.println("5. Logout");
                System.out.print("Enter your choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> student.display();
                    case 2 -> viewAndRegisterSuggestedCourses(student);
                    case 3 -> student.displayCoursesSemesterWise();
                    case 4 -> student.displayResultsSemesterWise();
                    case 5 -> {
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

    private void viewAndRegisterSuggestedCourses(Student student) {
        ArrayList<CourseResult> courses = studentManager.getSuggestedCoursesForStudent(student.getId());
        if (courses.isEmpty()) {
            System.out.println("No suggested courses found for this student.");
            return;
        }

        System.out.println("Suggested courses for Semester " + student.getSemester() + ":");
        for (CourseResult course : courses) {
            System.out.println("- " + course.catalogDisplay());
        }

        if (!student.canRegisterSuggestedCourses()) {
            System.out.println("You have already registered suggested courses for this semester.");
            System.out.println("You can register again when your semester changes and new suggested courses are available.");
            return;
        }

        System.out.print("Register these suggested courses now? (Y/N): ");
        String choice = sc.nextLine().trim();
        if (choice.equalsIgnoreCase("Y")) {
            if (studentManager.registerSuggestedCourses(student.getId())) {
                System.out.println("Registered courses will appear in your profile. The teacher will add marks at semester end.");
            }
        } else {
            System.out.println("Course registration skipped.");
        }
    }

}
