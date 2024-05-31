import org.junit.Before;
import org.junit.Test;
import java.util.Date;


import static org.junit.Assert.*;

public class PlannerTest {

    private Planner planner;

    @Before
    public void setUp() {
        planner = new Planner("Test Printer");
    }

    @Test
    public void calculateDurationString(){
        String test = planner.calculateDurationString(new Date(0), new Date(60000));
        assertEquals(test, "1min");


    }

    @Test
    public void isValidDate(){
        assertTrue(planner.isValidDate("20:13"));
    }

    @Test
    public void updateDateTimeLabels(){
        String old = planner.getCurrentTimeLabel().getText();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertNotEquals(planner.getCurrentTimeLabel().getText(), old);
    }
}