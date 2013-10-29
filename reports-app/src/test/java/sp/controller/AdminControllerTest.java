package sp.controller;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.context.MessageSource;
import sp.util.service.PropertyService;

/**
 * {@link AdminController} tests
 *
 * @author Paul Kulitski
 */
public class AdminControllerTest {

    AdminController controller;
    ExtendedModelMap model;
    PropertyService propertyService;
    MessageSource messageSource;

    public AdminControllerTest() {
    }

    @Before
    public void beforeTest() {
        controller = new AdminController();
        propertyService = mock(PropertyService.class);
        messageSource = mock(MessageSource.class);
        controller.propertyService = propertyService;
        controller.messageSource = messageSource;
        model = new ExtendedModelMap();
    }

    @Test
    public void managerTest() {
        String result = controller.manager(model);
        assertEquals("admin", result);
    }

    @Test
    public void updateSystemPropertiesTest() {
        String result = controller.updateSystemProperties("front", "pageSize", "10",
                Locale.ENGLISH, model);
        assertEquals("success", result);
        verify(propertyService).updateFrontend("pageSize", "10");

        model.clear();
        result = controller.updateSystemProperties("back", "solrTimeout", "1000",
                Locale.ENGLISH, model);
        assertEquals("success", result);
        verify(propertyService).updateBackend("solrTimeout", "1000");

        model.clear();
        result = controller.updateSystemProperties("front", "badPropertyName", "10",
                Locale.ENGLISH, model);
        assertEquals("error", result);

        model.clear();
        result = controller.updateSystemProperties("front", "dburl", "12323httdd:bad-url",
                Locale.ENGLISH, model);
        verify(messageSource).getMessage("admin.invalidurl", null, Locale.ENGLISH);

        model.clear();
        result = controller.updateSystemProperties("front", "solrTimeout", "notANumber",
                Locale.ENGLISH, model);
        verify(messageSource).getMessage("admin.invalidnumber", null, Locale.ENGLISH);

        model.clear();
        result = controller.updateSystemProperties("badTypeName", "pageSize", "10",
                Locale.ENGLISH, model);
        assertEquals("error", result);
    }

    @Test
    public void referenceDateTest() {
        String result = controller.referenceData();
        assertEquals("title.admin", result);
    }
}
