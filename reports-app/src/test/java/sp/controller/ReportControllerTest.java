package sp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import sp.model.Report;
import sp.service.ReportService;
import org.springframework.beans.support.PagedListHolder;

/**
 * Report controller test
 *
 * @author Paul Kulitski
 */
public class ReportControllerTest {

    public ReportControllerTest() {
    }
    ReportController controller;
    ExtendedModelMap model;
    ReportService reportService;
    static List<String> performers;
    static int MAX_ON_PAGER = 4;
    static int CHECKLIST_INITIAL_CAPACITY = 32;
    static int PAGINATION_THRESHOLD = 10;

    @BeforeClass
    public static void beforeClassTest() {
        performers = new ArrayList<String>();
        performers.add("Daniel Manner");
        performers.add("Mark Waltenberf");
        performers.add("Garry Bolderman");
        performers.add("Li Young");
    }

    @Before
    public void beforeTest() {
        reportService = mock(ReportService.class, "reportService");
        when(reportService.getPerformers()).thenReturn(performers);
        controller = new ReportController(reportService);
        controller.MAX_ON_PAGER = MAX_ON_PAGER;
        controller.CHECKLIST_INITIAL_CAPACITY = CHECKLIST_INITIAL_CAPACITY;
        controller.PAGINATION_THRESHOLD = PAGINATION_THRESHOLD;
        model = new ExtendedModelMap();
    }

    @After
    public void afterTest() {
        controller = null;
    }

    /**
     * Tests reference data method
     */
    @Test
    public void referenceDataTest() {
        String result = controller.populatePageKey();
        assertNotNull(result);
        assertEquals(result, "title.reports");
    }

    /**
     * Test settings population
     */
    @Test
    public void settingsPopulationTest() {
        Map<String, String> settings = controller.populateReferenceData();
        assertNotNull(settings);
        assertTrue(settings.containsKey("maxOnPager"));
        assertTrue(settings.containsKey("pagerThreshold"));
        assertEquals(settings.get("maxOnPager"), String.valueOf(MAX_ON_PAGER));
        assertEquals(settings.get("pagerThreshold"), String.valueOf(PAGINATION_THRESHOLD));
    }

    /**
     * Test pager population
     */
    @Test
    public void populatePagerTest() {
        Map<String, PagedListHolder<Report>> pagers = controller.populatePager();
        assertNotNull(pagers);
        assertEquals(pagers.size(), 0);
    }

    /**
     * Checklist population test
     */
    @Test
    public void populateChecklistTest() {
        Set<Long> checklist = controller.createChecklist();
        assertNotNull(checklist);
        assertEquals(checklist.size(), 0);
    }

    @Test
    public void setupFormTest() {
        String result = controller.setupForm(model);
        assertNotNull(model);
        assertTrue(model.containsKey("view"));
        assertEquals(model.get("view"), "search");
        assertTrue(model.containsKey("performers"));
        assertNotNull(model.get("performers"));
        assertSame(performers, model.get("performers"));
        assertTrue(model.containsKey("startDate"));
        assertEquals(model.get("startDate").getClass(), Date.class);
        assertTrue(model.containsKey("endDate"));
        assertEquals(model.get("endDate").getClass(), Date.class);
        assertTrue(model.containsKey("performer"));
        assertEquals(model.get("performer").getClass(), String.class);
        assertEquals(result, "form");
    }

    /**
     * Test of detail method, of class ReportController.
     */
    @Test
    public void testSetupDetailForm() {
        String result = controller.setupDetailForm(model);
        assertEquals(model.containsAttribute("view"), true);
        assertEquals(model.asMap().get("view"), "byid");
        assertEquals(result, "byid");
    }

    /**
     * Test of detail method, of class ReportController.
     */
    //@Test
    public void testDetailBuId() {
        Model model = new ExtendedModelMap();
        Long id = 1L;

        Report expReport = new Report();
        expReport.setId(id);
        expReport.setStartDate(new Date());
        expReport.setEndDate(new Date());
        expReport.setPerformer("Michael Douglas");
        expReport.setActivity("swimming");

        ReportService reportService = mock(ReportService.class);
        when(reportService.getReportById(id)).thenReturn(expReport);

        ReportController reportController = new ReportController(reportService);
        assertSame(expReport, model.asMap().get("report"));
    }
}