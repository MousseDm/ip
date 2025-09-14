package kenma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Represents a task that must be completed by a specific time. */
public class Deadline extends Task {
    private final String by;
    private LocalDate date;
    private LocalDateTime dateTime;

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm",
            Locale.ENGLISH);

    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        if (by == null || by.isBlank()) {
            throw new IllegalArgumentException("Deadline 'by' cannot be empty.");
        }
        this.by = by;
        parse(by);
    }

    public String getBy() {
        return by;
    }

    public LocalDateTime getDueDateTime() {
        return dateTime;
    }

    public LocalDate getDueDate() {
        return date;
    }

    private void parse(String s) {
        String t = s.trim();
        try {
            this.dateTime = LocalDateTime.parse(t, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            this.date = null;
            return;
        } catch (DateTimeParseException ignored) {
        }
        try {
            this.date = LocalDate.parse(t, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.dateTime = null;
            return;
        } catch (DateTimeParseException ignored) {
        }
        this.date = null;
        this.dateTime = null; // fall back to raw display
    }

    private String pretty() {
        if (dateTime != null) {
            return dateTime.format(FMT_DATETIME);
        }
        if (date != null) {
            return date.format(FMT_DATE);
        }
        return by;
    }

    /** Contribute a time key for duplicate detection (see Task.equals). */
    @Override
    protected String keyBy() {
        if (dateTime != null) {
            return dateTime.toString();
        }
        if (date != null) {
            return date.atStartOfDay().toString();
        }
        return by.trim().toLowerCase();
    }

    public boolean occursOn(LocalDate target) {
        if (target == null) {
            return false;
        }
        if (this.dateTime != null) {
            return this.dateTime.toLocalDate().equals(target);
        }
        if (this.date != null) {
            return this.date.equals(target);
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + getType().getSymbol() + "]" + super.toString() + " (by: " + pretty() + ")";
    }
}
