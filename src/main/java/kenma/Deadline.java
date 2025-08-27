package kenma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific time.
 *
 * <p>The {@code by} value supports:
 * <ul>
 *   <li>{@code yyyy-MM-dd HHmm} (e.g., {@code 2025-08-21 2359})</li>
 *   <li>{@code yyyy-MM-dd} (date only)</li>
 *   <li>Any arbitrary string (kept verbatim if parsing fails)</li>
 * </ul>
 * If parsing succeeds, a pretty-printed date/time is shown in {@link #toString()}.
 */
public class Deadline extends Task {
    protected String by;
    private LocalDate date;
    private LocalDateTime dateTime;

    private static final DateTimeFormatter FMT_DATE =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter FMT_DATETIME =
            DateTimeFormatter.ofPattern("MMM d yyyy HH:mm", Locale.ENGLISH);

    /**
     * Creates a deadline task.
     *
     * @param description description of the task
     * @param by          due time in a supported format
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
        parse(by);
    }

    /**
     * Attempts to parse the raw {@code by} string into either a {@link LocalDateTime} or a {@link LocalDate}.
     * If both attempts fail, both parsed fields remain {@code null} and the original string is kept.
     *
     * @param s raw input to parse
     */
    private void parse(String s) {
        String t = s.trim();
        try {
            this.dateTime = LocalDateTime.parse(t, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            this.date = null;
            return;
        } catch (DateTimeParseException ignored) {
            // Intentionally ignored: fall back to date-only parsing.
        }
        try {
            this.date = LocalDate.parse(t, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.dateTime = null;
            return;
        } catch (DateTimeParseException ignored) {
            // Intentionally ignored: keep raw string if parsing fails.
        }
        this.date = null;
        this.dateTime = null;
    }

    /**
     * Returns a human-friendly representation of the due time, preferring the parsed date/time.
     *
     * @return pretty-printed due time text
     */
    private String pretty() {
        if (dateTime != null) {
            return dateTime.format(FMT_DATETIME);
        }
        if (date != null) {
            return date.format(FMT_DATE);
        }
        return by;
    }

    /**
     * Returns whether this deadline falls on the given calendar date.
     *
     * <p>If the deadline was parsed as a datetime, only the date component is compared.
     *
     * @param target target calendar date
     * @return {@code true} if this deadline occurs on {@code target}; otherwise {@code false}
     */
    public boolean occursOn(LocalDate target) {
        if (this.dateTime != null) {
            return this.dateTime.toLocalDate().equals(target);
        }
        if (this.date != null) {
            return this.date.equals(target);
        }
        return false;
    }

    /**
     * Returns the string representation of this deadline, including its type symbol and pretty time.
     *
     * @return string representation of this deadline
     */
    @Override
    public String toString() {
        return "[" + this.type.getSymbol() + "]" + super.toString() + " (by: " + pretty() + ")";
    }
}
