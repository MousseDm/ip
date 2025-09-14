package kenma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Parses a user input line into a {@link Parsed} command+arguments tuple.
 * Adds robust handling for whitespace, missing/duplicate flags, and basic
 * date/time validation.
 */
public class Parser {

    // ----- Public API -----
    public static Parsed parse(String input) throws DukeException {
        if (input == null) {
            throw new DukeException("Command cannot be null.");
        }

        // Normalize: trim + collapse all internal whitespace to single spaces
        String s = normalize(input);
        if (s.isEmpty()) {
            throw new DukeException("Empty command.");
        }

        String lower = s.toLowerCase(Locale.ROOT);

        // bye
        if (lower.equals("bye")) {
            return new Parsed(Command.BYE);
        }

        // list
        if (lower.equals("list")) {
            return new Parsed(Command.LIST);
        }

        // mark <index>
        if (lower.startsWith("mark ")) {
            String arg = s.substring(5).trim();
            ensureNonEmpty(arg, "Expected an index: mark <index> (use 'list' to see indexes).");
            ensureInteger(arg, "Index must be a positive integer: mark <index>.");
            return new Parsed(Command.MARK, arg);
        }

        // unmark <index>
        if (lower.startsWith("unmark ")) {
            String arg = s.substring(7).trim();
            ensureNonEmpty(arg, "Expected an index: unmark <index>.");
            ensureInteger(arg, "Index must be a positive integer: unmark <index>.");
            return new Parsed(Command.UNMARK, arg);
        }

        // delete <index>
        if (lower.startsWith("delete ")) {
            String arg = s.substring(7).trim();
            ensureNonEmpty(arg, "Expected an index: delete <index>.");
            ensureInteger(arg, "Index must be a positive integer: delete <index>.");
            return new Parsed(Command.DELETE, arg);
        }

        // todo <desc>
        if (lower.startsWith("todo")) {
            String desc = s.length() > 4 ? s.substring(4).trim() : "";
            ensureNonEmpty(desc, "The description of a todo cannot be empty. Usage: todo <description>");
            return new Parsed(Command.TODO, desc);
        }

        // deadline <desc> /by <when>
        if (lower.startsWith("deadline")) {
            String body = s.substring(8).trim();
            // require exactly one /by (case-insensitive)
            ensureContainsOnce(body, "/by", "Missing '/by'. Usage: deadline <desc> /by <yyyy-MM-dd HHmm>");
            String[] split = BY_SPLIT.split(body, 2); // splits on \s+/by\s+ case-insensitive
            if (split.length < 2) {
                throw new DukeException("Invalid deadline format. Usage: deadline <desc> /by <yyyy-MM-dd HHmm>");
            }
            String desc = split[0].trim();
            String by = split[1].trim();
            ensureNonEmpty(desc, "Deadline description cannot be empty.");
            ensureNonEmpty(by, "Deadline time cannot be empty. Use: /by <yyyy-MM-dd HHmm>");

            // Validate time (keep original string for downstream)
            validateDateTime(by);
            return new Parsed(Command.DEADLINE, desc, by);
        }

        // event <desc> /from <start> /to <end>
        if (lower.startsWith("event")) {
            String body = s.length() > 5 ? s.substring(5).trim() : "";
            ensureContainsOnce(body, "/from", "Missing '/from'. Usage: event <desc> /from <start> /to <end>");
            ensureContainsOnce(body, "/to", "Missing '/to'. Usage: event <desc> /from <start> /to <end>");

            String[] left = FROM_SPLIT.split(body, 2); // <desc> | <start> /to <end>
            if (left.length < 2) {
                throw new DukeException("Invalid event format. Use: event <desc> /from <start> /to <end>");
            }
            String desc = left[0].trim();
            ensureNonEmpty(desc, "Event description cannot be empty.");

            String[] right = TO_SPLIT.split(left[1], 2); // <start> | <end>
            if (right.length < 2) {
                throw new DukeException("Invalid event time format. Use: /from <start> /to <end>");
            }
            String start = right[0].trim();
            String end = right[1].trim();
            ensureNonEmpty(start, "Event start time cannot be empty.");
            ensureNonEmpty(end, "Event end time cannot be empty.");

            // Validate times (keep originals)
            LocalDateTime st = validateDateTime(start);
            LocalDateTime ed = validateDateTime(end);
            if (!ed.isAfter(st)) {
                throw new DukeException("End time must be after start time.");
            }
            return new Parsed(Command.EVENT, desc, start, end);
        }

        // on <date> (e.g., on 2025-09-14 or on 2025-09-14 1300)
        if (lower.startsWith("on ")) {
            String dateStr = s.substring(3).trim();
            ensureNonEmpty(dateStr, "Usage: on <yyyy-MM-dd> or on <yyyy-MM-dd HHmm>");
            // Validate (either full date or date-time)
            validateDateOrDateTime(dateStr);
            return new Parsed(Command.ON, dateStr);
        }

        // sort [mode]
        if (lower.startsWith("sort")) {
            String mode = s.length() > 4 ? s.substring(4).trim().toLowerCase(Locale.ROOT) : "";
            // We accept any mode string here; actual support is decided downstream.
            return new Parsed(Command.SORT, mode);
        }

        // find <keyword>
        if (lower.startsWith("find")) {
            String kw = s.length() > 4 ? s.substring(4).trim() : "";
            ensureNonEmpty(kw, "Please provide a keyword to find. Usage: find <keyword>");
            return new Parsed(Command.FIND, kw);
        }

        throw new DukeException("I'm sorry, but I don't know what that means :-(");
    }

    // ----- Commands & Parsed tuple -----
    public enum Command {
        BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON, FIND, SORT
    }

    public static class Parsed {
        public final Command cmd;
        public final String a, b, c;

        public Parsed(Command cmd, String... args) {
            if (cmd == null) {
                throw new IllegalArgumentException("cmd cannot be null");
            }
            this.cmd = cmd;
            this.a = args.length > 0 ? args[0] : null;
            this.b = args.length > 1 ? args[1] : null;
            this.c = args.length > 2 ? args[2] : null;
        }
    }

    // ----- Helpers -----

    /** Trim + collapse internal whitespace to single spaces. */
    private static String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ");
    }

    /** Ensure string is not empty; otherwise throw with message. */
    private static void ensureNonEmpty(String s, String message) {
        if (s == null || s.isBlank()) {
            throw new DukeException(message);
        }
    }

    /** Ensure arg is a positive integer. */
    private static void ensureInteger(String s, String message) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                throw new DukeException(message);
            }
        }
        if (s.isEmpty() || s.equals("0")) {
            throw new DukeException(message);
        }
    }

    /** Ensure a flag like "/by" appears exactly once (case-insensitive). */
    private static void ensureContainsOnce(String haystack, String token, String missingMessage) {
        String h = haystack.toLowerCase(Locale.ROOT);
        String t = token.toLowerCase(Locale.ROOT);
        int count = 0;
        int idx = 0;
        while ((idx = h.indexOf(t, idx)) >= 0) {
            count++;
            idx += t.length();
        }
        if (count == 0) {
            throw new DukeException(missingMessage);
        }
        if (count > 1) {
            throw new DukeException("Duplicate '" + token + "' found. Please specify it only once.");
        }
    }

    // Case-insensitive splits like \s+/by\s+
    private static final Pattern BY_SPLIT = Pattern.compile("(?i)\\s+/by\\s+");
    private static final Pattern FROM_SPLIT = Pattern.compile("(?i)\\s+/from\\s+");
    private static final Pattern TO_SPLIT = Pattern.compile("(?i)\\s+/to\\s+");

    // Accepted date/time formats
    private static final DateTimeFormatter[] DATE_TIME_CANDIDATES = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    };
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Validate as date-time (supports a few common formats). Returns parsed
     * LocalDateTime.
     */
    private static LocalDateTime validateDateTime(String raw) {
        String t = normalize(raw);
        for (DateTimeFormatter f : DATE_TIME_CANDIDATES) {
            try {
                return LocalDateTime.parse(t, f);
            } catch (Exception ignore) {
                /* try next */ }
        }
        // Accept date-only by interpreting start-of-day
        try {
            LocalDate d = LocalDate.parse(t, DATE_ONLY);
            return d.atStartOfDay();
        } catch (Exception ignore) {
            /* fallthrough */ }
        throw new DukeException("Invalid date/time: " + raw
                + ". Try formats like: yyyy-MM-dd HHmm (e.g., 2025-09-14 1300).");
    }

    /** Validate as either date-only or date-time. */
    private static void validateDateOrDateTime(String raw) {
        String t = normalize(raw);
        try {
            // Try date-only first
            LocalDate.parse(t, DATE_ONLY);
            return;
        } catch (Exception ignore) {
            /* try datetime */ }
        validateDateTime(t); // will throw if invalid
    }
}
