package kenma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents an event that spans a time window with a start ({@code from}) and
 * an end ({@code to}).
 *
 * <p>
 * Each endpoint supports:
 * <ul>
 * <li>{@code yyyy-MM-dd HHmm} (datetime)</li>
 * <li>{@code yyyy-MM-dd} (date only)</li>
 * <li>Arbitrary string (kept raw if parsing fails)</li>
 * </ul>
 * If parsing succeeds, pretty-printed values are used in {@link #toString()}.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    private LocalDate fromDate;
    private LocalDateTime fromDateTime;
    private LocalDate toDate;
    private LocalDateTime toDateTime;

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm",
            Locale.ENGLISH);

    /**
     * Creates an event with a description and its start/end endpoints.
     *
     * @param description description of the event
     * @param from        start time in a supported format
     * @param to          end time in a supported format
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
        parse();
    }

    /**
     * Returns whether the event touches the given calendar date.
     *
     * <p>
     * A match occurs if either the parsed {@code from} date or the parsed
     * {@code to} date
     * equals {@code target}. If a datetime was provided, its date component is
     * used.
     *
     * @param target target calendar date
     * @return {@code true} if the event occurs on {@code target}; otherwise
     *         {@code false}
     */
    public boolean occursOn(LocalDate target) {
        boolean matchFrom = false;
        boolean matchTo = false;

        if (this.fromDateTime != null) {
            matchFrom = this.fromDateTime.toLocalDate().equals(target);
        } else if (this.fromDate != null) {
            matchFrom = this.fromDate.equals(target);
        }

        if (this.toDateTime != null) {
            matchTo = this.toDateTime.toLocalDate().equals(target);
        } else if (this.toDate != null) {
            matchTo = this.toDate.equals(target);
        }

        return matchFrom || matchTo;
    }

    /**
     * Parses both {@code from} and {@code to} strings into {@link LocalDateTime} or
     * {@link LocalDate} values.
     * If parsing fails for an endpoint, the corresponding parsed fields remain
     * {@code null}.
     */
    private void parse() {
        this.fromDate = null;
        this.fromDateTime = null;
        this.toDate = null;
        this.toDateTime = null;

        if (from != null && !from.isEmpty()) {
            try {
                this.fromDateTime = LocalDateTime.parse(
                        from.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            } catch (DateTimeParseException ignored) {
                try {
                    this.fromDate = LocalDate.parse(
                            from.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (DateTimeParseException ignored2) {
                    // Intentionally ignored: keep raw string if parsing fails.
                }
            }
        }

        if (to != null && !to.isEmpty()) {
            try {
                this.toDateTime = LocalDateTime.parse(
                        to.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            } catch (DateTimeParseException ignored) {
                try {
                    this.toDate = LocalDate.parse(
                            to.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (DateTimeParseException ignored2) {
                    // Intentionally ignored: keep raw string if parsing fails.
                }
            }
        }
    }

    /**
     * Returns a human-friendly representation for an endpoint, preferring parsed
     * values.
     *
     * @param raw original string
     * @param d   parsed date (may be {@code null})
     * @param dt  parsed datetime (may be {@code null})
     * @return pretty-printed endpoint text
     */
    private String prettyDate(String raw, LocalDate d, LocalDateTime dt) {
        if (dt != null) {
            return dt.format(FMT_DATETIME);
        }
        if (d != null) {
            return d.format(FMT_DATE);
        }
        return raw;
    }

    /**
     * Returns the string representation of this event with pretty-printed
     * endpoints.
     *
     * @return string representation of this event
     */
    @Override
    public String toString() {
        return "[" + this.type.getSymbol() + "]"
                + super.toString()
                + " (from: " + prettyDate(from, fromDate, fromDateTime)
                + " to: " + prettyDate(to, toDate, toDateTime) + ")";
    }
}
