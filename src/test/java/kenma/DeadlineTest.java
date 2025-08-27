package kenma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

public class DeadlineTest {

    @Test
    void occursOn_parsesDate_onlyDateMatches() {
        Deadline d = new Deadline("submit report", "2019-12-02");
        assertTrue(d.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(d.occursOn(LocalDate.of(2019, 12, 3)));
        assertTrue(d.toString().contains("(by: Dec 2 2019)"));
    }

    @Test
    void occursOn_parsesDateTime_dateComponentMatches() {
        Deadline d = new Deadline("return book", "2019-12-02 1800");
        assertTrue(d.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(d.occursOn(LocalDate.of(2019, 12, 1)));
        assertTrue(d.toString().contains("(by: Dec 2 2019 18:00)"));
    }
}
