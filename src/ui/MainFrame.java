package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import model.ProjectData;
import model.TimetableScheduler;

public class MainFrame extends Frame {

    private CardLayout cardLayout;
    private Panel mainPanel;

    private ProjectData data = new ProjectData();
    private TimetableScheduler scheduler = new TimetableScheduler();

    private TextField branchField = new TextField(22);
    private TextField departmentField = new TextField(22);
    private TextField yearField = new TextField(22);
    private TextField semesterField = new TextField(22);
    private TextField divisionField = new TextField(22);
    private TextField academicYearField = new TextField(22);
    private TextField effectiveDateField = new TextField(22);

    private TextField subjectField = new TextField(18);
    private TextField theoryFacultyField = new TextField(18);
    private TextField lecturesField = new TextField(5);
    private java.awt.List subjectList = new java.awt.List(8);

    private TextField practicalField = new TextField(22);
    private java.awt.List practicalList = new java.awt.List(8);

    private TextField batchField = new TextField(14);
    private Choice practicalChoice = new Choice();
    private TextField practicalFacultyField = new TextField(14);
    private java.awt.List batchList = new java.awt.List(8);

    private TextArea previewArea = new TextArea("", 30, 110,
        TextArea.SCROLLBARS_BOTH);

    private Label status = new Label("Enter your details to begin.", Label.CENTER);

    private final Color navy = new Color(30, 45, 75);
    private final Color background = new Color(245, 247, 251);
    private final Color blue = new Color(50, 100, 180);

    public MainFrame() {
        setTitle("Automatic College Timetable Generator");
        setSize(1150, 780);
        setMinimumSize(new Dimension(900, 650));
        setLayout(new BorderLayout());
        setBackground(background);

        cardLayout = new CardLayout();
        mainPanel = new Panel(cardLayout);

        mainPanel.add(createDashboard(), "dashboard");
        mainPanel.add(createClassPage(), "class");
        mainPanel.add(createSubjectPage(), "subjects");
        mainPanel.add(createPracticalPage(), "practicals");
        mainPanel.add(createBatchPage(), "batches");
        mainPanel.add(createPreviewPage(), "preview");

        add(mainPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Panel createDashboard() {
        Panel page = new Panel(new BorderLayout(16, 16));
        page.setBackground(background);

        Panel header = new Panel(new GridLayout(2, 1, 0, 4));
        header.setBackground(navy);
        Label college = new Label(
            "Shri Sant Gajanan Maharaj College Of Engineering, Shegaon",
            Label.CENTER
        );
        college.setForeground(Color.WHITE);
        college.setFont(new Font("Arial", Font.BOLD, 21));
        Label appName = new Label("AUTOMATIC COLLEGE TIMETABLE GENERATOR", Label.CENTER);
        appName.setForeground(Color.LIGHT_GRAY);
        appName.setFont(new Font("Arial", Font.PLAIN, 14));
        header.add(college);
        header.add(appName);
        header.setPreferredSize(new Dimension(100, 100));
        page.add(header, BorderLayout.NORTH);

        Panel content = new Panel(new BorderLayout(10, 12));
        content.setBackground(background);
        Label welcome = new Label("Create Your College Timetable", Label.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 25));
        welcome.setForeground(navy);
        welcome.setPreferredSize(new Dimension(100, 55));
        content.add(welcome, BorderLayout.NORTH);

        Panel steps = new Panel(new GridLayout(4, 1, 10, 10));
        steps.setBackground(background);
        addDashboardStep(steps, "01", "Class Details",
            "Enter branch, year, semester and academic session", "class");
        addDashboardStep(steps, "02", "Theory Subjects",
            "Add subjects, faculty and weekly lecture count", "subjects");
        addDashboardStep(steps, "03", "Practical Subjects",
            "Add practical and laboratory subject names", "practicals");
        addDashboardStep(steps, "04", "Practical Batch Details",
            "Assign practicals and faculty to batches A, B, C and D", "batches");

        Panel centered = new Panel(new GridBagLayout());
        centered.setBackground(background);
        centered.add(steps);
        content.add(centered, BorderLayout.CENTER);
        page.add(content, BorderLayout.CENTER);

        Label note = new Label(
            "Complete each step in order. Information remains available until you close the program.",
            Label.CENTER
        );
        note.setForeground(Color.DARK_GRAY);
        note.setPreferredSize(new Dimension(100, 35));
        page.add(note, BorderLayout.SOUTH);
        return page;
    }

    private void addDashboardStep(Panel parent, String number,
                                  String heading, String description,
                                  String pageName) {
        Panel card = new Panel(new BorderLayout(12, 5));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(780, 85));

        Label numberLabel = new Label(number, Label.CENTER);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 22));
        numberLabel.setForeground(blue);
        numberLabel.setPreferredSize(new Dimension(75, 60));

        Panel text = new Panel(new GridLayout(2, 1));
        text.setBackground(Color.WHITE);

        Label title = new Label(heading);
        title.setFont(new Font("Arial", Font.BOLD, 17));

        Label descriptionLabel = new Label(description);
        descriptionLabel.setForeground(Color.GRAY);

        text.add(title);
        text.add(descriptionLabel);

        Button open = new Button("Open");
        open.addActionListener(e -> showPage(pageName));

        card.add(numberLabel, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        card.add(open, BorderLayout.EAST);

        parent.add(card);
    }

    private Panel createClassPage() {
        Panel page = new Panel(new BorderLayout(14, 14));
        page.setBackground(background);
        page.add(pageTitle("STEP 1 - CLASS DETAILS"), BorderLayout.NORTH);

        Panel fields = new Panel(new GridLayout(7, 2, 16, 10));
        fields.setBackground(Color.WHITE);
        fields.add(new Label("Branch"));
        fields.add(branchField);
        fields.add(new Label("Department"));
        fields.add(departmentField);
        fields.add(new Label("Year"));
        fields.add(yearField);
        fields.add(new Label("Semester"));
        fields.add(semesterField);
        fields.add(new Label("Division"));
        fields.add(divisionField);
        fields.add(new Label("Academic Year"));
        fields.add(academicYearField);
        fields.add(new Label("Effective From (optional)"));
        fields.add(effectiveDateField);

        Panel formCard = new Panel(new BorderLayout(12, 12));
        formCard.setBackground(Color.WHITE);
        formCard.add(fields, BorderLayout.CENTER);
        formCard.setPreferredSize(new Dimension(650, 350));

        Panel center = new Panel(new GridBagLayout());
        center.setBackground(background);
        center.add(formCard);
        page.add(center, BorderLayout.CENTER);

        Panel bottom = new Panel(new BorderLayout(5, 5));
        bottom.setBackground(background);
        Button save = new Button("Save Class Details");
        save.setFont(new Font("Arial", Font.BOLD, 13));
        save.addActionListener(e -> saveClassDetails());
        Panel savePanel = new Panel(new FlowLayout());
        savePanel.setBackground(background);
        savePanel.add(save);
        bottom.add(savePanel, BorderLayout.NORTH);
        bottom.add(navigation("dashboard", "subjects"), BorderLayout.SOUTH);
        page.add(bottom, BorderLayout.SOUTH);
        return page;
    }

    private Panel createSubjectPage() {
        Panel page = new Panel(new BorderLayout(14, 14));
        page.setBackground(background);
        page.add(pageTitle("STEP 2 - THEORY SUBJECT DETAILS"), BorderLayout.NORTH);

        Panel input = new Panel(new GridLayout(2, 3, 12, 7));
        input.setBackground(Color.WHITE);
        input.add(new Label("Subject Name"));
        input.add(new Label("Assigned Faculty"));
        input.add(new Label("Lectures per Week"));
        input.add(subjectField);
        input.add(theoryFacultyField);
        input.add(lecturesField);
        input.setPreferredSize(new Dimension(850, 75));

        Button add = new Button("Add Subject");
        add.addActionListener(e -> addSubject());
        Button remove = new Button("Remove Selected");
        remove.addActionListener(e -> removeSelectedSubject());

        Panel controls = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 7));
        controls.setBackground(background);
        controls.add(add);
        controls.add(remove);

        Panel inputCard = new Panel(new BorderLayout(8, 8));
        inputCard.setBackground(Color.WHITE);
        inputCard.add(input, BorderLayout.NORTH);
        inputCard.add(controls, BorderLayout.SOUTH);
        inputCard.setPreferredSize(new Dimension(1000, 125));

        Panel listPanel = new Panel(new BorderLayout(6, 6));
        listPanel.setBackground(Color.WHITE);
        Label heading = new Label("Added Theory Subjects");
        heading.setFont(new Font("Arial", Font.BOLD, 14));
        heading.setForeground(navy);
        listPanel.add(heading, BorderLayout.NORTH);
        subjectList.setFont(new Font("Arial", Font.PLAIN, 15));
        listPanel.add(subjectList, BorderLayout.CENTER);

        Panel center = new Panel(new BorderLayout(12, 12));
        center.setBackground(background);
        center.add(inputCard, BorderLayout.NORTH);
        center.add(listPanel, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);
        page.add(navigation("class", "practicals"), BorderLayout.SOUTH);
        return page;
    }

    private Panel createPracticalPage() {
        Panel page = new Panel(new BorderLayout(14, 14));
        page.setBackground(background);
        page.add(pageTitle("STEP 3 - PRACTICAL DETAILS"), BorderLayout.NORTH);

        Panel input = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        input.setBackground(Color.WHITE);
        input.add(new Label("Practical Subject"));
        input.add(practicalField);
        Button add = new Button("Add Practical");
        add.addActionListener(e -> addPractical());
        Button remove = new Button("Remove Selected");
        remove.addActionListener(e -> removeSelectedPractical());
        input.add(add);
        input.add(remove);
        input.setPreferredSize(new Dimension(1000, 65));

        Panel listPanel = new Panel(new BorderLayout(6, 6));
        listPanel.setBackground(Color.WHITE);
        Label heading = new Label("Added Practical Subjects");
        heading.setFont(new Font("Arial", Font.BOLD, 14));
        heading.setForeground(navy);
        listPanel.add(heading, BorderLayout.NORTH);
        practicalList.setFont(new Font("Arial", Font.PLAIN, 15));
        listPanel.add(practicalList, BorderLayout.CENTER);

        Panel center = new Panel(new BorderLayout(12, 12));
        center.setBackground(background);
        center.add(input, BorderLayout.NORTH);
        center.add(listPanel, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);
        page.add(navigation("subjects", "batches"), BorderLayout.SOUTH);
        return page;
    }

    private Panel createBatchPage() {
        Panel page = new Panel(new BorderLayout(14, 14));
        page.setBackground(background);
        page.add(pageTitle("STEP 4 - PRACTICAL BATCH DETAILS"), BorderLayout.NORTH);

        // Keep the input area compact. GridLayout in a large center panel
        // was stretching the text fields vertically and making the screen look
        // like three giant white boxes.
        Panel fields = new Panel(new GridLayout(2, 3, 14, 7));
        fields.setBackground(background);
        fields.add(new Label("Batch Name"));
        fields.add(new Label("Practical Subject"));
        fields.add(new Label("Faculty for this Batch"));
        fields.add(batchField);
        fields.add(practicalChoice);
        fields.add(practicalFacultyField);
        fields.setPreferredSize(new Dimension(900, 78));

        Button load = new Button("Load Practical Subjects");
        load.addActionListener(e -> loadPracticalChoices());

        Button add = new Button("Add Batch Assignment");
        add.addActionListener(e -> addBatchAssignment());

        Button remove = new Button("Remove Selected");
        remove.addActionListener(e -> removeSelectedBatchAssignment());

        Panel controls = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        controls.setBackground(background);
        controls.add(load);
        controls.add(add);
        controls.add(remove);

        Panel inputArea = new Panel(new BorderLayout(8, 8));
        inputArea.setBackground(background);
        inputArea.add(fields, BorderLayout.NORTH);
        inputArea.add(controls, BorderLayout.SOUTH);
        inputArea.setPreferredSize(new Dimension(1000, 130));

        Panel listPanel = new Panel(new BorderLayout(6, 6));
        listPanel.setBackground(Color.WHITE);
        Label listHeading = new Label(
            "Batch Assignments  |  Batch        Practical Subject        Faculty",
            Label.LEFT
        );
        listHeading.setFont(new Font("Arial", Font.BOLD, 14));
        listHeading.setForeground(navy);
        listPanel.add(listHeading, BorderLayout.NORTH);
        batchList.setFont(new Font("Arial", Font.PLAIN, 15));
        listPanel.add(batchList, BorderLayout.CENTER);

        Panel center = new Panel(new BorderLayout(10, 10));
        center.setBackground(background);
        center.add(inputArea, BorderLayout.NORTH);
        center.add(listPanel, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);

        Panel bottom = new Panel(new BorderLayout(5, 5));
        bottom.setBackground(background);
        status.setFont(new Font("Arial", Font.PLAIN, 13));
        bottom.add(status, BorderLayout.NORTH);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttons.setBackground(background);

        Button back = new Button("Back");
        back.addActionListener(e -> showPage("practicals"));

        Button dashboard = new Button("Dashboard");
        dashboard.addActionListener(e -> showPage("dashboard"));

        Button generate = new Button("Generate Timetable");
        generate.setFont(new Font("Arial", Font.BOLD, 13));
        generate.addActionListener(e -> generateTimetable());

        buttons.add(back);
        buttons.add(dashboard);
        buttons.add(generate);
        bottom.add(buttons, BorderLayout.SOUTH);

        page.add(bottom, BorderLayout.SOUTH);
        return page;
    }

    private Panel createPreviewPage() {
        Panel page = new Panel(new BorderLayout(12, 12));
        page.setBackground(background);
        page.add(pageTitle("GENERATED COLLEGE TIMETABLE"), BorderLayout.NORTH);

        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        previewArea.setBackground(Color.WHITE);
        previewArea.setEditable(false);
        page.add(previewArea, BorderLayout.CENTER);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttons.setBackground(background);
        Button back = new Button("Back to Batch Details");
        back.addActionListener(e -> showPage("batches"));
        Button save = new Button("Save Timetable as TXT");
        save.setFont(new Font("Arial", Font.BOLD, 13));
        save.addActionListener(e -> saveTimetable());
        Button home = new Button("Dashboard");
        home.addActionListener(e -> showPage("dashboard"));
        buttons.add(back);
        buttons.add(save);
        buttons.add(home);
        page.add(buttons, BorderLayout.SOUTH);
        return page;
    }

    private Label pageTitle(String text) {
        Label title = new Label(text, Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(navy);
        title.setPreferredSize(new Dimension(100, 58));
        return title;
    }

    private Panel navigation(String backPage, String nextPage) {
        Panel buttons = new Panel(new FlowLayout());

        Button back = new Button("Back");
        back.addActionListener(e -> showPage(backPage));

        Button dashboard = new Button("Dashboard");
        dashboard.addActionListener(e -> showPage("dashboard"));

        Button next = new Button("Next");
        next.addActionListener(e -> showPage(nextPage));

        buttons.add(back);
        buttons.add(dashboard);
        buttons.add(next);
        return buttons;
    }

    private void showPage(String name) {
        cardLayout.show(mainPanel, name);
    }

    private void saveClassDetails() {
        if (branchField.getText().trim().isEmpty()
                || departmentField.getText().trim().isEmpty()
                || yearField.getText().trim().isEmpty()
                || semesterField.getText().trim().isEmpty()
                || divisionField.getText().trim().isEmpty()
                || academicYearField.getText().trim().isEmpty()) {
            status.setText("Please fill all required class details.");
            return;
        }

        data.branch = branchField.getText().trim();
        data.department = departmentField.getText().trim();
        data.year = yearField.getText().trim();
        data.semester = semesterField.getText().trim();
        data.division = divisionField.getText().trim();
        data.academicYear = academicYearField.getText().trim();
        data.effectiveDate = effectiveDateField.getText().trim();

        status.setText("Class details saved.");
    }

    private void addSubject() {
        String name = subjectField.getText().trim();
        String faculty = theoryFacultyField.getText().trim();
        String countText = lecturesField.getText().trim();

        if (name.isEmpty() || faculty.isEmpty() || countText.isEmpty()) {
            status.setText("Enter subject, faculty and lectures per week.");
            return;
        }

        try {
            int count = Integer.parseInt(countText);
            if (count < 1) {
                status.setText("Lectures per week must be at least 1.");
                return;
            }

            for (ProjectData.TheorySubject item : data.subjects) {
                if (item.name.equalsIgnoreCase(name)) {
                    status.setText("This subject is already added.");
                    return;
                }
            }

            data.subjects.add(new ProjectData.TheorySubject(name, faculty, count));
            subjectList.add(name + " | " + faculty + " | " + count + " lectures");

            subjectField.setText("");
            theoryFacultyField.setText("");
            lecturesField.setText("");
            status.setText("Theory subject added.");
        } catch (NumberFormatException ex) {
            status.setText("Lectures per week must be a whole number.");
        }
    }

    private void removeSelectedSubject() {
        int selected = subjectList.getSelectedIndex();
        if (selected < 0) {
            status.setText("Select a subject to remove.");
            return;
        }

        data.subjects.remove(selected);
        subjectList.remove(selected);
        status.setText("Subject removed.");
    }

    private void addPractical() {
        String name = practicalField.getText().trim();

        if (name.isEmpty()) {
            status.setText("Enter a practical subject name.");
            return;
        }

        for (String existing : data.practicals) {
            if (existing.equalsIgnoreCase(name)) {
                status.setText("This practical is already added.");
                return;
            }
        }

        data.practicals.add(name);
        practicalList.add(name);
        practicalField.setText("");
        status.setText("Practical subject added.");
    }

    private void removeSelectedPractical() {
        int selected = practicalList.getSelectedIndex();
        if (selected < 0) {
            status.setText("Select a practical subject to remove.");
            return;
        }

        String name = data.practicals.get(selected);
        data.practicals.remove(selected);
        practicalList.remove(selected);

        // Also remove batch entries that refer to the removed practical.
        for (int i = data.batchAssignments.size() - 1; i >= 0; i--) {
            if (data.batchAssignments.get(i).practical.equalsIgnoreCase(name)) {
                data.batchAssignments.remove(i);
            }
        }

        refreshBatchList();
        status.setText("Practical and its batch assignments removed.");
    }

    private void loadPracticalChoices() {
        practicalChoice.removeAll();

        for (String practical : data.practicals) {
            practicalChoice.add(practical);
        }

        if (data.practicals.isEmpty()) {
            status.setText("Add practical subjects in Step 3 first.");
        } else {
            status.setText("Practical subjects loaded. Now add batch assignments.");
        }
    }

    private void addBatchAssignment() {
        String batch = batchField.getText().trim();
        String faculty = practicalFacultyField.getText().trim();

        if (batch.isEmpty() || faculty.isEmpty()
                || practicalChoice.getItemCount() == 0) {
            status.setText("Enter batch, load practical subjects and enter faculty.");
            return;
        }

        String practical = practicalChoice.getSelectedItem();

        for (ProjectData.BatchAssignment item : data.batchAssignments) {
            if (item.batch.equalsIgnoreCase(batch)
                    && item.practical.equalsIgnoreCase(practical)) {
                status.setText(
                    "This batch already has this practical. Remove or edit the old entry."
                );
                return;
            }
        }

        data.batchAssignments.add(
            new ProjectData.BatchAssignment(batch, practical, faculty)
        );

        batchList.add(batch + " | " + practical + " | " + faculty);
        batchField.setText("");
        practicalFacultyField.setText("");
        status.setText("Batch practical assignment added.");
    }

    private void removeSelectedBatchAssignment() {
        int selected = batchList.getSelectedIndex();
        if (selected < 0) {
            status.setText("Select a batch assignment to remove.");
            return;
        }

        data.batchAssignments.remove(selected);
        batchList.remove(selected);
        status.setText("Batch assignment removed.");
    }

    private void refreshBatchList() {
        batchList.removeAll();

        for (ProjectData.BatchAssignment item : data.batchAssignments) {
            batchList.add(
                item.batch + " | " + item.practical + " | " + item.faculty
            );
        }
    }

    private void generateTimetable() {
        try {
            // Save the class form if the user has filled it but not pressed Save.
            if (!branchField.getText().trim().isEmpty()) {
                saveClassDetails();
            }

            String result = scheduler.generate(data);
            previewArea.setText(result);
            previewArea.setCaretPosition(0);
            showPage("preview");
            status.setText("Timetable generated successfully.");
        } catch (Exception ex) {
            status.setText("Cannot generate: " + ex.getMessage());
        }
    }

    private void saveTimetable() {
        if (previewArea.getText().trim().isEmpty()) {
            return;
        }

        FileDialog dialog = new FileDialog(
            this, "Save Timetable", FileDialog.SAVE
        );
        dialog.setFile("College_Timetable.txt");
        dialog.setVisible(true);

        if (dialog.getFile() == null) {
            return;
        }

        File file = new File(dialog.getDirectory(), dialog.getFile());

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(previewArea.getText());
            status.setText("Timetable saved to " + file.getAbsolutePath());
        } catch (IOException ex) {
            status.setText("Could not save timetable: " + ex.getMessage());
        }
    }
}
