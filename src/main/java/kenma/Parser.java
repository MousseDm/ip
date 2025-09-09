package kenma;

public class Parser {
    public static Parsed parse(String input) throws DukeException {
        assert input != null;

        String s = input.trim();
        if (s.isEmpty())
            throw new DukeException("Empty command.");

        String lower = s.toLowerCase();
        if (lower.equals("bye"))
            return new Parsed(Command.BYE);
        if (lower.equals("list"))
            return new Parsed(Command.LIST);
        if (lower.startsWith("mark ")) {
            String arg = s.substring(5).trim();
            assert !arg.isEmpty();
            return new Parsed(Command.MARK, arg);
        }
        if (lower.startsWith("unmark ")) {
            String arg = s.substring(7).trim();
            assert !arg.isEmpty();
            return new Parsed(Command.UNMARK, arg);
        }
        if (lower.startsWith("delete ")) {
            String arg = s.substring(7).trim();
            assert !arg.isEmpty();
            return new Parsed(Command.DELETE, arg);
        }
        if (lower.startsWith("todo")) {
            String desc = s.length() > 4 ? s.substring(4).trim() : "";
            assert desc != null;
            if (desc.isEmpty())
                throw new DukeException("The description of a todo cannot be empty.");
            return new Parsed(Command.TODO, desc);
        }
        if (lower.startsWith("deadline")) {
            String body = s.substring(8).trim();
            int byPos = body.toLowerCase().indexOf("/by");
            if (byPos < 0)
                throw new DukeException("Missing '/by'. Usage: deadline <desc> /by <when>");
            String desc = body.substring(0, byPos).trim();
            String by = body.substring(byPos + 3).trim();
            assert desc != null && by != null;
            if (desc.isEmpty() || by.isEmpty())
                throw new DukeException("Both description and '/by <when>' are required.");
            return new Parsed(Command.DEADLINE, desc, by);
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
            assert desc != null && from != null && to != null;
            if (desc.isEmpty() || from.isEmpty() || to.isEmpty())
                throw new DukeException("Event requires description, from and to.");
            return new Parsed(Command.EVENT, desc, from, to);
        }
        if (lower.startsWith("on ")) {
            String dateStr = s.substring(3).trim();
            assert !dateStr.isEmpty();
            return new Parsed(Command.ON, dateStr);
        }
        if (lower.startsWith("sort")) {
            String mode = s.length() > 4 ? s.substring(4).trim().toLowerCase() : "";
            return new Parsed(Command.SORT, mode);
        }
        if (lower.startsWith("find")) {
            String kw = s.length() > 4 ? s.substring(4).trim() : "";
            assert kw != null;
            if (kw.isEmpty()) {
                throw new DukeException("Please provide a keyword to find.");
            }
            return new Parsed(Command.FIND, kw);
        }
        throw new DukeException("I'm sorry, but I don't know what that means :-(");
    }

    public enum Command {
        BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON, FIND, SORT
    }

    public static class Parsed {
        public final Command cmd;
        public final String a, b, c;

        public Parsed(Command cmd, String... args) {
            assert cmd != null;
            this.cmd = cmd;
            this.a = args.length > 0 ? args[0] : null;
            this.b = args.length > 1 ? args[1] : null;
            this.c = args.length > 2 ? args[2] : null;
        }
    }
}
