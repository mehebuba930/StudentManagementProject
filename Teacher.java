class Teacher extends User {
    public Teacher(String name, String username, String password) {
        super(name, username, password);
    }

    @Override
    void display() {
        System.out.println("Teacher: " + name + " (" + username + ")");
    }
}