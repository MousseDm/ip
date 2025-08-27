package kenma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    void parse_deadline_withDateTime() throws DukeException {
        Parser.Parsed p = Parser.parse("deadline submit report /by 2019-12-02 1800");
        assertEquals(Parser.Command.DEADLINE, p.cmd);
        assertEquals("submit report", p.a);
        assertEquals("2019-12-02 1800", p.b);
        assertNull(p.c);
    }

    @Test
    void parse_event_withFromTo() throws DukeException {
        Parser.Parsed p = Parser.parse("event meeting /from 2019-12-02 1800 /to 2019-12-02 2000");
        assertEquals(Parser.Command.EVENT, p.cmd);
        assertEquals("meeting", p.a);
        assertEquals("2019-12-02 1800", p.b);
        assertEquals("2019-12-02 2000", p.c);
    }
}
