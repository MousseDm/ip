package kenma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Represents an event that spans a time window with a start and an end. */
public class Event extends Task {
    private final String from;
    private final String to;

    private LocalDate fromDate;
    private LocalDateTime fromDateTime;
    private LocalDate toDate;
    private LocalDateTime toDateTime;

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm",
            Locale.ENGLISH);

    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("Event 'from' cannot be empty.");
        }
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Event 'to' cannot be empty.");
        }
        this.from = from;
        this.to = to;
        parseAndValidate();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public boolean occursOn(LocalDate target) {
        if (target == null) {
            return false;
        }
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

    private void parseAndValidate() {
        this.fromDate = null;
        this.fromDateTime = null;
        this.toDate = null;
        this.toDateTime = null;

        parseInto(from.trim(), true);
        parseInto(to.trim(), false);

        LocalDateTime start = (fromDateTime != null)
                ? fromDateTime
                : (fromDate != null ? fromDate.atStartOfDay() : null);
        LocalDateTime end = (toDateTime != null)
                ? toDateTime
                : (toDate != null ? toDate.atStartOfDay() : null);

        if (start == null || end == null) {
            throw new IllegalArgumentException("Event time(s) invalid: cannot parse 'from' or 'to'.");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Event end time must be AFTER start time.");
        }
    }

    private void parseInto(String raw, boolean isFrom) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        try {
            LocalDateTime dt = LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            if (isFrom) {
                this.fromDateTime = dt;
            } else {
                this.toDateTime = dt;
            }
        } catch (DateTimeParseException ignored) {
            try {
                LocalDate d = LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (isFrom) {
                    this.fromDate = d;
                } else {
                    this.toDate = d;
                }
            } catch (DateTimeParseException ignored2) {
                // leave null; validated later
            }
        }
    }

    private String prettyDate(String raw, LocalDate d, LocalDateTime dt) {
        if (dt != null) {
            return dt.format(FMT_DATETIME);
        }
        if (d != null) {
            return d.format(FMT_DATE);
        }
        return raw;
    }

    /** Contribute time keys for duplicate detection (see Task.equals). */
    @Override
    protected String keyStart() {
        if (fromDateTime != null) {
            return fromDateTime.toString();
        }
        if (fromDate != null) {
            return fromDate.atStartOfDay().toString();
        }
        return from.trim().toLowerCase();
    }

    @Override
    protected String keyEnd() {
        if (toDateTime != null) {
            return toDateTime.toString();
        }
        if (toDate != null) {
            return toDate.atStartOfDay().toString();
        }
        return to.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return "[" + getType().getSymbol() + "]"
                + super.toString()
                + " (from: " + prettyDate(from, fromDate, fromDateTime)
                + " to: " + prettyDate(to, toDate, toDateTime) + ")";
    }
}
