package kenma;

/**
 * Parses raw user input lines into structured commands understood by the app.
 *
 * <p>Supported commands:
 * <ul>
 *   <li>{@code bye}</li>
 *   <li>{@code list}</li>
 *   <li>{@code mark <index>}</li>
 *   <li>{@code unmark <index>}</li>
 *   <li>{@code delete <index>}</li>
 *   <li>{@code todo <description>}</li>
 *   <li>{@code deadline <description> /by <when>}</li>
 *   <li>{@code event <description> /from <start> /to <end>}</li>
 *   <li>{@code on <yyyy-MM-dd>}</li>
 * </ul>
 */
public class Parser {

    /**
     * Parses a raw user input into a {@link Parsed} command.
     *
     * @param input raw user input (non-null)
     * @return the parsed command
     * @throws DukeException if the input is empty or violates the required syntax
     */
    public static Parsed parse(String input) throws DukeException {
        String s = input.trim();
        if (s.isEmpty()) {
            throw new DukeException("Empty command.");
        }
        String lower = s.toLowerCase();
        if (lower.equals("bye")) {
            return new Parsed(Command.BYE);
        }
        if (lower.equals("list")) {
            return new Parsed(Command.LIST);
        }
        if (lower.startsWith("mark ")) {
            return new Parsed(Command.MARK, s.substring(5).trim());
        }
        if (lower.startsWith("unmark ")) {
            return new Parsed(Command.UNMARK, s.substring(7).trim());
        }
        if (lower.startsWith("delete ")) {
            return new Parsed(Command.DELETE, s.substring(7).trim());
        }
        if (lower.startsWith("todo")) {
            String desc = s.length() > 4 ? s.substring(4).trim() : "";
            if (desc.isEmpty()) {
                throw new DukeException("The description of a todo cannot be empty.");
            }
            return new Parsed(Command.TODO, desc);
        }
        if (lower.startsWith("deadline")) {
            String body = s.substring(8).trim();
            int byPos = body.toLowerCase().indexOf("/by");
            if (byPos < 0) {
                throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
            }
            String desc = body.substring(0, byPos).trim();
            String by = body.substring(byPos + 3).trim();
            if (desc.isEmpty() || by.isEmpty()) {
                throw new DukeException("Both description and '/by <when>' are required.");
            }
            return new Parsed(Command.DEADLINE, desc, by);
        }
        if (lower.startsWith("event")) {
            String body = s.length() > 5 ? s.substring(5).trim() : "";
            String low = body.toLowerCase();
            int fromPos = low.indexOf("/from");
            int toPos = low.indexOf("/to");
            if (fromPos < 0 || toPos < 0 || toPos <= fromPos) {
                throw new DukeException("Event must be 'event <desc> /from <start> /to <end>'.");
            }
            String desc = body.substring(0, fromPos).trim();
            String from = body.substring(fromPos + 5, toPos).trim();
            String to = body.substring(toPos + 3).trim();
            if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new DukeException("Event requires description, from and to.");
            }
            return new Parsed(Command.EVENT, desc, from, to);
        }
        if (lower.startsWith("on ")) {
            String dateStr = s.substring(3).trim();
            return new Parsed(Command.ON, dateStr);
        }
        throw new DukeException("I'm sorry, but I don't know what that means :-(");
    }

    /** Supported command kinds. */
    public enum Command { BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON }

    /**
     * Parsed representation of a user command.
     * Fields {@code a}/{@code b}/{@code c} carry command-specific arguments.
     */
    public static class Parsed {
        /** Command kind. */
        public final Command cmd;
        /** First argument (may be {@code null}). */
        public final String a;
        /** Second argument (may be {@code null}). */
        public final String b;
        /** Third argument (may be {@code null}). */
        public final String c;

        /**
         * Creates a parsed command with no arguments.
         *
         * @param cmd command kind
         */
        public Parsed(Command cmd) {
            this(cmd, null, null, null);
        }

        /**
         * Creates a parsed command with one argument.
         *
         * @param cmd command kind
         * @param a   first argument
         */
        public Parsed(Command cmd, String a) {
            this(cmd, a, null, null);
        }

        /**
         * Creates a parsed command with two arguments.
         *
         * @param cmd command kind
         * @param a   first argument
         * @param b   second argument
         */
        public Parsed(Command cmd, String a, String b) {
            this(cmd, a, b, null);
        }

        /**
         * Creates a parsed command with up to three arguments.
         *
         * @param cmd command kind
         * @param a   first argument (may be {@code null})
         * @param b   second argument (may be {@code null})
         * @param c   third argument (may be {@code null})
         */
        public Parsed(Command cmd, String a, String b, String c) {
            this.cmd = cmd;
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
}
