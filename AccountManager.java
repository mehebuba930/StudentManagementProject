import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;

class AccountManager {
    private static final String FILE_NAME = "accounts.txt";
    private static final String LEGACY_FILE_NAME = "accounts.dat";
    private ArrayList<User> accounts = new ArrayList<>();

    public void loadFromFile() {
        if (loadFromTextFile(FILE_NAME)) {
            return;
        }

        if (loadFromLegacyBinary(LEGACY_FILE_NAME)) {
            saveToFile();
            return;
        }

        accounts = new ArrayList<>();
        seedDefaultAccounts();
        saveToFile();
    }

    public void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(FILE_NAME))) {
            for (User user : accounts) {
                out.println(serializeUser(user));
            }
        } catch (Exception e) {
            System.out.println("Error saving accounts.");
        }
    }

    private boolean loadFromTextFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        }

        ArrayList<User> loadedAccounts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length != 4) {
                    continue;
                }

                String role = parts[0];
                String name = unescape(parts[1]);
                String username = unescape(parts[2]);
                String password = unescape(parts[3]);

                if ("ADMIN".equals(role)) {
                    loadedAccounts.add(new Admin(name, username, password));
                } else if ("TEACHER".equals(role)) {
                    loadedAccounts.add(new Teacher(name, username, password));
                }
            }

            accounts = loadedAccounts;
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
            accounts = (ArrayList<User>) in.readObject();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String serializeUser(User user) {
        String role = user instanceof Admin ? "ADMIN" : "TEACHER";
        return role + "|" + escape(user.getName()) + "|" + escape(user.getUsername()) + "|" + escape(user.password);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
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

    public boolean usernameExists(String username) {
        for (User user : accounts) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean addAccount(User user) {
        if (usernameExists(user.getUsername())) {
            return false;
        }

        accounts.add(user);
        saveToFile();
        return true;
    }

    public Admin authenticateAdmin(String username, String password) {
        for (User user : accounts) {
            if (user instanceof Admin && user.getUsername().equalsIgnoreCase(username) && user.checkPassword(password)) {
                return (Admin) user;
            }
        }
        return null;
    }

    public Teacher authenticateTeacher(String username, String password) {
        for (User user : accounts) {
            if (user instanceof Teacher && user.getUsername().equalsIgnoreCase(username) && user.checkPassword(password)) {
                return (Teacher) user;
            }
        }
        return null;
    }

    private void seedDefaultAccounts() {
        accounts.add(new Admin("Administrator", "admin", "admin123"));
        accounts.add(new Teacher("Teacher", "teacher", "teacher123"));
    }
}
