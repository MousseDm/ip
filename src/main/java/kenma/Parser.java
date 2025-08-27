package kenma;

public class Parser {
    public static Parsed parse(String input) throws DukeException {
        String s = input.trim();
        if (s.isEmpty())
            throw new DukeException("Empty command.");
        String lower = s.toLowerCase();
        if (lower.equals("bye"))
            return new Parsed(Cmd.BYE);
        if (lower.equals("list"))
            return new Parsed(Cmd.LIST);
        if (lower.startsWith("mark "))
            return new Parsed(Cmd.MARK, s.substring(5).trim());
        if (lower.startsWith("unmark "))
            return new Parsed(Cmd.UNMARK, s.substring(7).trim());
        if (lower.startsWith("delete "))
            return new Parsed(Cmd.DELETE, s.substring(7).trim());
        if (lower.startsWith("todo")) {
            String desc = s.length() > 4 ? s.substring(4).trim() : "";
            if (desc.isEmpty())
                throw new DukeException("The description of a todo cannot be empty.");
            return new Parsed(Cmd.TODO, desc);
        }
        if (lower.startsWith("deadline")) {
            String body = s.substring(8).trim();
            int byPos = body.toLowerCase().indexOf("/by");
            if (byPos < 0)
                throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
            String desc = body.substring(0, byPos).trim();
            String by = body.substring(byPos + 3).trim();
            if (desc.isEmpty() || by.isEmpty())
                throw new DukeException("Both description and '/by <when>' are required.");
            return new Parsed(Cmd.DEADLINE, desc, by);
        }
        if (lower.startsWith("event")) {
            String body = s.length() > 5 ? s.substring(5).trim() : "";
            String low = body.toLowerCase();
            int fromPos = low.indexOf("/from");
            int toPos = low.indexOf("/to");
            if (fromPos < 0 || toPos < 0 || toPos <= fromPos)
                throw new DukeException("Event must be 'event <desc> /from <start> /to <end>'.");
            String desc = body.substring(0, fromPos).trim();
            String from = body.substring(fromPos + 5, toPos).trim();
            String to = body.substring(toPos + 3).trim();
            if (desc.isEmpty() || from.isEmpty() || to.isEmpty())
                throw new DukeException("Event requires description, from and to.");
            return new Parsed(Cmd.EVENT, desc, from, to);
        }
        if (lower.startsWith("on ")) {
            String dateStr = s.substring(3).trim();
            return new Parsed(Cmd.ON, dateStr);
        }
        throw new DukeException("I'm sorry, but I don't know what that means :-(");
    }

    public enum Cmd {
        BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON
    }

    public static class Parsed {
        public final Cmd cmd;
        public final String a, b, c;

        public Parsed(Cmd cmd) {
            this(cmd, null, null, null);
        }

        public Parsed(Cmd cmd, String a) {
            this(cmd, a, null, null);
        }

        public Parsed(Cmd cmd, String a, String b) {
            this(cmd, a, b, null);
        }

        public Parsed(Cmd cmd, String a, String b, String c) {
            this.cmd = cmd;
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
}
