package sp.controller;

import com.sun.net.httpserver.HttpServer;
import javax.servlet.http.HttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Paul Kulitski
 */
public class LoginControllerTest {

    LoginController controller;
    ExtendedModelMap model;

    @Before
    public void before() {
        model = new ExtendedModelMap();
        controller = new LoginController();
    }

    @Test
    public void referenceDataTest() {
        String result = controller.referenceData();
        assertEquals("title.login", result);
    }

    @Test
    public void setupLoginFormTest() {
        /*
         * Login usecase test
         */
        String result = controller.loginForm(true, false, false, null, null, model);
        assertEquals("login", result);
        assertEquals("login", model.get("view"));

        /*
         * Logout usecase
         */
        model.clear();
        result = controller.loginForm(false, true, false, null, null, model);
        assertEquals("logout", result);
        assertEquals("logout", model.get("view"));

        /*
         * User info usecase
         */
        model.clear();
        result = controller.loginForm(false, false, true, null, null, model);
        assertEquals("logout", result);
        assertEquals("logout", model.get("view"));
        assertEquals(true, model.get("info"));
        //TODO: write test for anonymous user
    }

    @Test
    public void loginHomePageTest() {
        String result = controller.loginHome(model);
        assertEquals("home", result);
    }

    @Test
    public void logoutPageTest() {
        HttpSession session = mock(HttpSession.class);
        String result = controller.logoutPage(session, model);
        assertEquals(true, model.get("login"));
        verify(session).invalidate();
        assertEquals("login", result);
    }
}
