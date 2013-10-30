package sp.controller;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import sp.model.ajax.Statistics;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import org.springframework.ui.ExtendedModelMap;

/**
 *
 * @author Paul Kulitski
 */
public class EmailControllerTest {

    EmailController controller;
    ExtendedModelMap model;

    @Before
    public void before() {
        model = new ExtendedModelMap();
        controller = new EmailController();
    }

    @Test
    public void populateStatisticsTest() {
        Object object = controller.populateStatistics();
        assertNotNull(object);
        assertThat(object, instanceOf(Statistics.class));
    }

    @Test
    public void generateEmailTest() {
        String result = controller.getHtmlWithStatistics(new Statistics(), model,
                null, null, null);
        assertNotNull(model.get("statistics"));
        assertEquals("stats-email", result);
    }
}
