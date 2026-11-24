package model;

import java.util.ArrayList;

public class ProjectData {

    public String branch = "";
    public String department = "";
    public String year = "";
    public String semester = "";
    public String division = "";
    public String academicYear = "";
    public String effectiveDate = "";

    public ArrayList<TheorySubject> subjects = new ArrayList<>();
    public ArrayList<String> practicals = new ArrayList<>();
    public ArrayList<BatchAssignment> batchAssignments = new ArrayList<>();

    public static class TheorySubject {
        public String name;
        public String faculty;
        public int lecturesPerWeek;

        public TheorySubject(String name, String faculty, int lecturesPerWeek) {
            this.name = name;
            this.faculty = faculty;
            this.lecturesPerWeek = lecturesPerWeek;
        }
    }

    public static class BatchAssignment {
        public String batch;
        public String practical;
        public String faculty;

        public BatchAssignment(String batch, String practical, String faculty) {
            this.batch = batch;
            this.practical = practical;
            this.faculty = faculty;
        }
    }
}
