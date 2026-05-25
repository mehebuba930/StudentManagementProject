class Admin extends User {
    public Admin(String name, String username, String password) {
        super(name, username, password);
    }

    @Override
    void display() {
        System.out.println("Admin: " + name + " (" + username + ")");
    }
}