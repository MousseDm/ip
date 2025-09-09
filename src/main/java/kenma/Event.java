package kenma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Represents an event that spans a time window with a start and an end. */
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

    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        assert from != null && !from.isBlank();
        assert to != null && !to.isBlank();
        this.from = from;
        this.to = to;
        parse();
    }

    public boolean occursOn(LocalDate target) {
        assert target != null;
        boolean matchFrom = false, matchTo = false;
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

    private void parse() {
        this.fromDate = null;
        this.fromDateTime = null;
        this.toDate = null;
        this.toDateTime = null;

        if (from != null && !from.isBlank()) {
            try {
                this.fromDateTime = LocalDateTime.parse(from.trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            } catch (DateTimeParseException ignored) {
                try {
                    this.fromDate = LocalDate.parse(from.trim(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (DateTimeParseException ignored2) {
                }
            }
        }

        if (to != null && !to.isBlank()) {
            try {
                this.toDateTime = LocalDateTime.parse(to.trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
            } catch (DateTimeParseException ignored) {
                try {
                    this.toDate = LocalDate.parse(to.trim(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (DateTimeParseException ignored2) {
                }
            }
        }
    }

    private String prettyDate(String raw, LocalDate d, LocalDateTime dt) {
        if (dt != null)
            return dt.format(FMT_DATETIME);
        if (d != null)
            return d.format(FMT_DATE);
        return raw;
    }

    @Override
    public String toString() {
        return "[" + this.type.getSymbol() + "]"
                + super.toString()
                + " (from: " + prettyDate(from, fromDate, fromDateTime)
                + " to: " + prettyDate(to, toDate, toDateTime) + ")";
    }
}
