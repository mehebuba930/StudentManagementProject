import java.util.ArrayList;

class CurriculumCatalog {
    public static ArrayList<CourseResult> getSuggestedCourses(Department department, DegreeLevel level, int semester) {
        ArrayList<CourseResult> courses = new ArrayList<>();

        if (level == DegreeLevel.BACHELOR) {
            if (department == Department.CSE) {
                if (semester == 1) {
                    courses.add(course("CSE101", "Introduction to Programming", 3));
                    courses.add(course("MAT101", "Calculus I", 3));
                    courses.add(course("ENG101", "English Communication", 3));
                    courses.add(course("PHY101", "Physics", 3));
                } else if (semester == 2) {
                    courses.add(course("CSE201", "Object Oriented Programming", 3));
                    courses.add(course("CSE202", "Data Structures", 3));
                    courses.add(course("MAT201", "Calculus II", 3));
                    courses.add(course("PHY201", "Physics Lab", 1));
                }
            } else if (department == Department.EEE) {
                if (semester == 1) {
                    courses.add(course("EEE101", "Electrical Circuits I", 3));
                    courses.add(course("EEE102", "Basic Electronics", 3));
                    courses.add(course("MAT101", "Calculus I", 3));
                    courses.add(course("PHY101", "Physics", 3));
                } else if (semester == 2) {
                    courses.add(course("EEE201", "Circuit Analysis", 3));
                    courses.add(course("EEE202", "Digital Logic", 3));
                    courses.add(course("MAT201", "Calculus II", 3));
                    courses.add(course("CHE201", "Engineering Chemistry", 3));
                }
            } else if (department == Department.BBA) {
                if (semester == 1) {
                    courses.add(course("BBA101", "Introduction to Business", 3));
                    courses.add(course("BBA102", "Principles of Accounting", 3));
                    courses.add(course("ECO101", "Microeconomics", 3));
                    courses.add(course("ENG101", "English Communication", 3));
                } else if (semester == 2) {
                    courses.add(course("BBA201", "Marketing Principles", 3));
                    courses.add(course("BBA202", "Business Mathematics", 3));
                    courses.add(course("BBA203", "Business Communication", 3));
                    courses.add(course("ECO201", "Macroeconomics", 3));
                }
            } else if (department == Department.ARCHITECTURE) {
                if (semester == 1) {
                    courses.add(course("ARC101", "Design Studio I", 3));
                    courses.add(course("ARC102", "History of Architecture", 3));
                    courses.add(course("MAT101", "Mathematics", 3));
                    courses.add(course("PHY101", "Physics", 3));
                } else if (semester == 2) {
                    courses.add(course("ARC201", "Design Studio II", 3));
                    courses.add(course("ARC202", "Architectural Drawing", 3));
                    courses.add(course("ARC203", "Building Materials", 3));
                    courses.add(course("ARC204", "Environmental Studies", 3));
                }
            }
        } else {
            if (semester == 1) {
                courses.add(course("GRD501", "Research Methodology", 3));
                courses.add(course("GRD502", "Advanced Writing", 3));
                courses.add(course("GRD503", "Special Topic Seminar", 3));
            } else if (semester == 2) {
                courses.add(course("GRD601", "Thesis/Project Proposal", 6));
                courses.add(course("GRD602", "Advanced Elective", 3));
            }
        }

        return courses;
    }

    private static CourseResult course(String code, String name, int credits) {
        return new CourseResult(code, name, credits, 0.0);
    }
}
