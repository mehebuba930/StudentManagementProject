import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager();
        AccountManager accountManager = new AccountManager();
        AdminConsoleService adminService = new AdminConsoleService(sc, manager, accountManager);
        TeacherConsoleService teacherService = new TeacherConsoleService(sc, manager, accountManager);
        StudentConsoleService studentService = new StudentConsoleService(sc, manager);

        manager.loadFromFile();
        accountManager.loadFromFile();

        while (true) {
            try {
                System.out.println("\n===== Student Management System =====");
                System.out.println("1. Admin Login");
                System.out.println("2. Teacher Login");
                System.out.println("3. Student Login");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> adminService.run();

                    case 2 -> teacherService.run();

                    case 3 -> studentService.run();

                    case 4 -> {
                        System.out.println("Exiting program...");
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
}