package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class TimetableScheduler {

    private static final Random RANDOM = new Random();

    private static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    private static final String[] WEEKDAY_TIMES = {
        "11:00-12:00",
        "12:00-1:00",
        "1:15-2:15",
        "2:15-3:15",
        "3:45-4:45",
        "4:45-5:45"
    };

    private static final String[] SATURDAY_TIMES = {
        "8:30-9:30",
        "9:30-10:30",
        "10:45-11:45",
        "11:45-12:45"
    };

    private static class Slot {
        int day;
        int period;
        String time;

        Slot(int day, int period, String time) {
            this.day = day;
            this.period = period;
            this.time = time;
        }
    }

    private static class PracticalBlock {
        int firstSlot;
        int secondSlot;
        ArrayList<ProjectData.BatchAssignment> assignments =
            new ArrayList<>();

        PracticalBlock(int firstSlot, int secondSlot) {
            this.firstSlot = firstSlot;
            this.secondSlot = secondSlot;
        }
    }

    public String generate(ProjectData data) throws Exception {

        validateInput(data);

        ArrayList<Slot> slots = new ArrayList<>();
        ArrayList<PracticalBlock> blocks = new ArrayList<>();

        // Create weekday periods and practical blocks.
        for (int day = 0; day < 5; day++) {
            int start = slots.size();

            for (int p = 0; p < WEEKDAY_TIMES.length; p++) {
                slots.add(new Slot(day, p, WEEKDAY_TIMES[p]));
            }

            blocks.add(new PracticalBlock(start, start + 1));
            blocks.add(new PracticalBlock(start + 2, start + 3));
            blocks.add(new PracticalBlock(start + 4, start + 5));
        }

        // Saturday has four periods and two possible practical blocks.
        int saturdayStart = slots.size();

        for (int p = 0; p < SATURDAY_TIMES.length; p++) {
            slots.add(new Slot(5, p, SATURDAY_TIMES[p]));
        }

        blocks.add(new PracticalBlock(saturdayStart, saturdayStart + 1));
        blocks.add(new PracticalBlock(saturdayStart + 2, saturdayStart + 3));

        // No fixed day/period pattern is imposed. The generator may place
        // practical sessions in any available two-period block, including
        // early slots such as Tuesday P1-P2.
        ArrayList<ProjectData.BatchAssignment> practicalWork =
            new ArrayList<>(data.batchAssignments);

        // Shuffle the candidate blocks so valid generated timetables can vary
        // instead of reproducing the official college timetable.
        Collections.shuffle(blocks, RANDOM);

        sortPracticalWork(practicalWork);

        if (!placePracticalWork(practicalWork, 0, blocks)) {
            throw new Exception(
                "The practicals could not be placed without a conflict. " +
                "Check faculty, practical subject, and batch assignments."
            );
        }

        boolean[] practicalPeriod = new boolean[slots.size()];
        String[] timetable = new String[slots.size()];

        for (PracticalBlock block : blocks) {
            if (!block.assignments.isEmpty()) {
                practicalPeriod[block.firstSlot] = true;
                practicalPeriod[block.secondSlot] = true;
                timetable[block.firstSlot] = "PRACTICAL";
                timetable[block.secondSlot] = "PRACTICAL";
            }
        }

        int freePeriods = 0;
        for (boolean practical : practicalPeriod) {
            if (!practical) {
                freePeriods++;
            }
        }

        int requiredLectures = 0;
        for (ProjectData.TheorySubject subject : data.subjects) {
            requiredLectures += subject.lecturesPerWeek;
        }

        if (requiredLectures > freePeriods) {
            throw new Exception(
                "There are " + requiredLectures +
                " theory lectures, but only " + freePeriods +
                " periods remain after placing practicals. " +
                "Reduce weekly lectures or practical assignments."
            );
        }

        placeTheoryLectures(data.subjects, practicalPeriod, timetable, slots);

        return buildTimetable(data, slots, timetable, blocks);
    }

    private void validateInput(ProjectData data) throws Exception {

        if (data.branch.trim().isEmpty()
                || data.department.trim().isEmpty()
                || data.year.trim().isEmpty()
                || data.semester.trim().isEmpty()
                || data.division.trim().isEmpty()
                || data.academicYear.trim().isEmpty()) {
            throw new Exception("Please save all Class Details first.");
        }

        if (data.subjects.isEmpty()) {
            throw new Exception("Please add at least one theory subject.");
        }

        Set<String> batches = new LinkedHashSet<>();

        for (ProjectData.BatchAssignment item : data.batchAssignments) {
            batches.add(item.batch);
            if (!data.practicals.contains(item.practical)) {
                throw new Exception(
                    "Practical '" + item.practical +
                    "' is not present in the Practical Details list."
                );
            }
        }

        if (!data.practicals.isEmpty() && data.batchAssignments.isEmpty()) {
            throw new Exception(
                "You added practical subjects but no batch assignments. " +
                "Add the practical assignment for every batch."
            );
        }

        // Each batch must have every practical exactly once.
        for (String batch : batches) {
            for (String practical : data.practicals) {
                int count = 0;

                for (ProjectData.BatchAssignment item : data.batchAssignments) {
                    if (item.batch.equalsIgnoreCase(batch)
                            && item.practical.equalsIgnoreCase(practical)) {
                        count++;
                    }
                }

                if (count != 1) {
                    throw new Exception(
                        "Batch " + batch + " must have practical '" +
                        practical + "' exactly once. Current entries: " + count
                    );
                }
            }
        }

        // Every entered batch must have the same complete practical list.
        if (!data.practicals.isEmpty() && batches.isEmpty()) {
            throw new Exception("Please add batch practical assignments.");
        }
    }

    private void sortPracticalWork(
            ArrayList<ProjectData.BatchAssignment> work) {

        Collections.shuffle(work, RANDOM);
        // Put repeated practicals and repeated faculty near the front.
        // This makes conflicts easier to detect during allocation.
        Collections.sort(work, new Comparator<ProjectData.BatchAssignment>() {
            public int compare(ProjectData.BatchAssignment a,
                               ProjectData.BatchAssignment b) {
                int scoreA = conflictCount(a, work);
                int scoreB = conflictCount(b, work);
                return Integer.compare(scoreB, scoreA);
            }
        });
    }

    private int conflictCount(ProjectData.BatchAssignment item,
                              ArrayList<ProjectData.BatchAssignment> work) {
        int count = 0;

        for (ProjectData.BatchAssignment other : work) {
            if (item == other) {
                continue;
            }

            if (item.practical.equalsIgnoreCase(other.practical)) {
                count++;
            }
            if (item.faculty.equalsIgnoreCase(other.faculty)) {
                count++;
            }
            if (item.batch.equalsIgnoreCase(other.batch)) {
                count++;
            }
        }

        return count;
    }

    private boolean placePracticalWork(
            ArrayList<ProjectData.BatchAssignment> work,
            int index,
            ArrayList<PracticalBlock> blocks) {

        if (index == work.size()) {
            return true;
        }

        ProjectData.BatchAssignment current = work.get(index);

        // Try occupied blocks first to group compatible practical batches,
        // then try every empty block. This preserves compact scheduling while
        // allowing practicals in any valid period of the week.
        for (int pass = 0; pass < 2; pass++) {
            for (PracticalBlock block : blocks) {
                boolean occupied = !block.assignments.isEmpty();
                if ((pass == 0 && !occupied) || (pass == 1 && occupied)) {
                    continue;
                }

                if (canPlace(current, block)) {
                    block.assignments.add(current);

                    if (placePracticalWork(work, index + 1, blocks)) {
                        return true;
                    }

                    block.assignments.remove(block.assignments.size() - 1);
                }
            }
        }

        return false;
    }

    private boolean canPlace(ProjectData.BatchAssignment current,
                             PracticalBlock block) {

        for (ProjectData.BatchAssignment existing : block.assignments) {

            // A batch cannot do two practicals at the same time.
            if (existing.batch.equalsIgnoreCase(current.batch)) {
                return false;
            }

            // Only one lab is available for each practical subject.
            if (existing.practical.equalsIgnoreCase(current.practical)) {
                return false;
            }

            // A faculty member cannot teach two batches simultaneously.
            if (existing.faculty.equalsIgnoreCase(current.faculty)) {
                return false;
            }
        }

        return true;
    }

    private void placeTheoryLectures(
            ArrayList<ProjectData.TheorySubject> subjects,
            boolean[] practicalPeriod,
            String[] timetable,
            ArrayList<Slot> slots) {

        int[] remaining = new int[subjects.size()];
        int[][] dailyCount = new int[DAYS.length][subjects.size()];
        int remainingLectures = 0;

        for (int i = 0; i < subjects.size(); i++) {
            remaining[i] = subjects.get(i).lecturesPerWeek;
            remainingLectures += remaining[i];
        }

        // Distribute theory lectures across the week as evenly as possible.
        // Practical sessions can occupy any block; days with fewer free
        // periods naturally receive fewer theory lectures.
        for (int day = 0; day < DAYS.length; day++) {
            if (remainingLectures <= 0) break;

            ArrayList<Integer> daySlots = new ArrayList<>();
            for (int slotIndex = 0; slotIndex < slots.size(); slotIndex++) {
                if (slots.get(slotIndex).day == day
                        && !practicalPeriod[slotIndex]
                        && timetable[slotIndex] == null) {
                    daySlots.add(slotIndex);
                }
            }

            int daysLeft = DAYS.length - day;
            int targetToday = (remainingLectures + daysLeft - 1) / daysLeft;
            targetToday = Math.min(targetToday, daySlots.size());
            if (targetToday <= 0) continue;

            // Vary which periods are used, then put them back in chronological
            // order for a natural-looking daily timetable.
            Collections.shuffle(daySlots, RANDOM);
            ArrayList<Integer> selectedSlots = new ArrayList<>(
                daySlots.subList(0, targetToday));
            Collections.sort(selectedSlots);

            int previousSubject = -1;
            for (int slotIndex : selectedSlots) {
                ArrayList<Integer> candidates = new ArrayList<>();
                for (int i = 0; i < subjects.size(); i++) {
                    if (remaining[i] > 0) candidates.add(i);
                }
                if (candidates.isEmpty()) break;
                Collections.shuffle(candidates, RANDOM);

                int chosen = -1;
                int bestScore = Integer.MAX_VALUE;
                for (int candidate : candidates) {
                    int score = dailyCount[day][candidate] * 2;
                    if (candidate == previousSubject) score += 3;
                    if (score < bestScore) {
                        bestScore = score;
                        chosen = candidate;
                    }
                }

                if (chosen >= 0) {
                    ProjectData.TheorySubject subject = subjects.get(chosen);
                    timetable[slotIndex] = subject.name + " - " + subject.faculty;
                    remaining[chosen]--;
                    remainingLectures--;
                    dailyCount[day][chosen]++;
                    previousSubject = chosen;
                }
            }
        }
    }

    private String buildTimetable(
            ProjectData data,
            ArrayList<Slot> slots,
            String[] timetable,
            ArrayList<PracticalBlock> blocks) {

        StringBuilder out = new StringBuilder();

        out.append("SHRI SANT GAJANAN MAHARAJ COLLEGE OF ENGINEERING, SHEGAON\n");
        out.append("DEPARTMENT: ").append(data.department).append("\n");
        out.append("ACADEMIC SESSION: ").append(data.academicYear).append("\n");
        out.append("\n");
        out.append("                         TIME TABLE\n");
        out.append("Branch: ").append(data.branch)
           .append("    Year: ").append(data.year)
           .append("    Semester: ").append(data.semester)
           .append("    Division: ").append(data.division).append("\n");

        if (!data.effectiveDate.trim().isEmpty()) {
            out.append("Effective From: ").append(data.effectiveDate).append("\n");
        }

        out.append("\n");
        out.append("MONDAY-FRIDAY PERIODS:\n");
        out.append("P1 11:00-12:00 | P2 12:00-1:00 | P3 1:15-2:15 | ");
        out.append("P4 2:15-3:15 | P5 3:45-4:45 | P6 4:45-5:45\n");
        out.append("Break: 1:00-1:15     Recess: 3:15-3:45\n");
        out.append("SATURDAY PERIODS:\n");
        out.append("P1 8:30-9:30 | P2 9:30-10:30 | P3 10:45-11:45 | P4 11:45-12:45\n");
        out.append("Break: 10:30-10:45\n\n");

        final int cellWidth = 19;
        String[] headings = {"DAY", "P1", "P2", "P3", "P4", "P5", "P6"};

        appendBorder(out, cellWidth);
        appendTableRow(out, headings, cellWidth);
        appendBorder(out, cellWidth);

        int slotIndex = 0;

        for (int day = 0; day < DAYS.length; day++) {
            String[] row = new String[7];
            row[0] = DAYS[day];

            int periods = (day == 5) ? 4 : 6;

            for (int p = 0; p < 6; p++) {
                if (p < periods) {
                    String cell = timetable[slotIndex];
                    if (cell == null || cell.trim().isEmpty()) {
                        cell = "-";
                    }
                    row[p + 1] = cell;
                    slotIndex++;
                } else {
                    row[p + 1] = "-";
                }
            }

            appendTableRow(out, row, cellWidth);
            appendBorder(out, cellWidth);
        }

        out.append("\nPRACTICAL BATCH ALLOCATION (each session is 2 consecutive hours)\n");
        out.append("---------------------------------------------------------------\n");

        boolean anyPractical = false;

        for (PracticalBlock block : blocks) {
            if (block.assignments.isEmpty()) {
                continue;
            }

            anyPractical = true;

            Slot first = slots.get(block.firstSlot);
            Slot second = slots.get(block.secondSlot);

            String startTime = first.time.substring(0, first.time.indexOf('-'));
            String endTime = second.time.substring(second.time.indexOf('-') + 1);

            out.append(DAYS[first.day]).append("  ")
               .append(startTime).append(" to ").append(endTime).append("\n");

            for (ProjectData.BatchAssignment assignment : block.assignments) {
                out.append("   ")
                   .append(assignment.batch).append(" : ")
                   .append(assignment.practical).append(" (")
                   .append(assignment.faculty).append(")\n");
            }
        }

        if (!anyPractical) {
            out.append("No practical sessions entered.\n");
        }

        out.append("\nTHEORY LECTURE SUMMARY\n");
        out.append("----------------------\n");

        for (ProjectData.TheorySubject subject : data.subjects) {
            out.append(subject.name).append(" - ")
               .append(subject.faculty).append(" : ")
               .append(subject.lecturesPerWeek).append(" lectures/week\n");
        }

        out.append("\nPrepared By: ____________________");
        out.append("       HOD: ____________________");
        out.append("\n\nPrincipal: ______________________\n");

        return out.toString();
    }

    private void appendBorder(StringBuilder out, int width) {
        out.append("+");
        for (int i = 0; i < 7; i++) {
            int w = (i == 0) ? 12 : width;
            for (int j = 0; j < w + 2; j++) {
                out.append("-");
            }
            out.append("+");
        }
        out.append("\n");
    }

    private void appendTableRow(StringBuilder out, String[] values, int width) {
        out.append("|");
        for (int i = 0; i < values.length; i++) {
            int w = (i == 0) ? 12 : width;
            String value = values[i] == null ? "" : values[i];
            value = value.replace('\n', ' ').replace('\r', ' ');

            if (value.length() > w) {
                value = value.substring(0, w - 3) + "...";
            }

            out.append(" ").append(value);
            for (int j = value.length(); j < w; j++) {
                out.append(" ");
            }
            out.append(" |");
        }
        out.append("\n");
    }
}
