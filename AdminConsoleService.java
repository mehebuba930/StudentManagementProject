import java.util.InputMismatchException;
import java.util.Scanner;

class AdminConsoleService {
    private final Scanner sc;
    private final StudentManager studentManager;
    private final AccountManager accountManager;

    public AdminConsoleService(Scanner sc, StudentManager studentManager, AccountManager accountManager) {
        this.sc = sc;
        this.studentManager = studentManager;
        this.accountManager = accountManager;
    }

    public void run() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        Admin admin = accountManager.authenticateAdmin(username, password);
        if (admin == null) {
            System.out.println("Invalid admin credentials!");
            return;
        }

        while (true) {
            try {
                System.out.println("\n===== Admin Panel =====");
                System.out.println("1. Add Student");
                System.out.println("2. Display Students");
                System.out.println("3. Search Student");
                System.out.println("4. Update Student Academic Profile");
                System.out.println("5. Update Student Details");
                System.out.println("6. Delete Student");
                System.out.println("7. Sort by Marks");
                System.out.println("8. Sort by CGPA");
                System.out.println("9. Logout");
                System.out.print("Enter your choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> studentManager.displayStudents();
                    case 3 -> searchStudent();
                    case 4 -> updateAcademicProfile();
                    case 5 -> updateStudentDetails();
                    case 6 -> deleteStudent();
                    case 7 -> studentManager.sortByMarks();
                    case 8 -> studentManager.sortByGPA();
                    case 9 -> {
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

    private void addStudent() {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Username: ");
        String username = sc.nextLine();

        if (accountManager.usernameExists(username) || studentManager.isStudentUsernameTaken(username)) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        Department department = promptDepartment();
        DegreeLevel degreeLevel = promptDegreeLevel();

        Student student = new Student(id, name, username, password, department, degreeLevel, 1, 0, 0, new java.util.ArrayList<>());
        if (!studentManager.addStudent(student)) {
            System.out.println("Student ID already exists or academic data is invalid.");
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

    private void updateAcademicProfile() {
        System.out.print("Enter ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        Department department = promptDepartment();
        DegreeLevel degreeLevel = promptDegreeLevel();
        System.out.print("Enter semester: ");
        int semester = sc.nextInt();
        sc.nextLine();

        studentManager.updateStudentAcademicProfile(id, department, degreeLevel, semester, null);
    }

    private void updateStudentDetails() {
        System.out.print("Enter ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new Name: ");
        String name = sc.nextLine();

        System.out.print("Enter new Username: ");
        String username = sc.nextLine();

        if (accountManager.usernameExists(username) || studentManager.isStudentUsernameTaken(username, id)) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Enter new Password: ");
        String password = sc.nextLine();

        studentManager.updateStudentDetails(id, name, username, password);
    }

    private void deleteStudent() {
        System.out.print("Enter ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();
        studentManager.deleteStudent(id);
    }

    private Department promptDepartment() {
        while (true) {
            System.out.println("Select Department:");
            System.out.println("1. CSE");
            System.out.println("2. EEE");
            System.out.println("3. BBA");
            System.out.println("4. Architecture");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();
            Department department = Department.fromChoice(choice);
            if (department != null) {
                return department;
            }

            System.out.println("Invalid department choice!");
        }
    }

    private DegreeLevel promptDegreeLevel() {
        while (true) {
            System.out.println("Select Degree Level:");
            System.out.println("1. Bachelor");
            System.out.println("2. Master");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();
            DegreeLevel degreeLevel = DegreeLevel.fromChoice(choice);
            if (degreeLevel != null) {
                return degreeLevel;
            }

            System.out.println("Invalid degree level choice!");
        }
    }
}
