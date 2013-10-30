package sp.controller;

import org.springframework.ui.ExtendedModelMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.ui.Model;

/**
 *
 * @author Paul Kulitski
 */
public class ForwardControllerTest {
    
    Model model;
    ForwardController controller;
    
    @Before
    public void before() {
        model = new ExtendedModelMap();
        controller = new ForwardController();
    }
    
    @Test
    public void forwardTest() {
        String result = controller.forward(model);
        assertEquals("forward", result);
    }
    
}
