package sp.controller;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 *
 * @author Paul Kulitski
 */
public class ErrorControllerTest {

    ErrorController controller;
    Model model;

    @Before
    public void before() {
        model = new ExtendedModelMap();
        controller = new ErrorController();
    }

    @Test
    public void errorTest() {
        String result = controller.error();
        assertEquals("error", result);

    }

    @Test
    public void missingTest() {
        String result = controller.missing();
        assertEquals("missing", result);

    }

    @Test
    public void accessTest() {
        String result = controller.access();
        assertEquals("access", result);

    }

    @Test
    public void notsupportedTest() {
        String result = controller.notssupported();
        assertEquals("notsupported", result);
    }

    @Test
    public void badrequestTest() {
        String result = controller.badrequest();
        assertEquals("badrequest", result);
    }
}
