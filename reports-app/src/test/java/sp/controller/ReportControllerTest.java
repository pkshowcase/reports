package sp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.ui.ExtendedModelMap;
import sp.model.Report;
import sp.service.ReportService;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

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
    static List<Report> reports;
    static List<Report> reportsWithIdenticalPerformer;
    static List<Report> emptyReportsList = Collections.EMPTY_LIST;
    static int MAX_ON_PAGER = 4;
    static int CHECKLIST_INITIAL_CAPACITY = 32;
    static int PAGINATION_THRESHOLD = 10;
    static Map<String, PagedListHolder<Report>> pagers;

    @BeforeClass
    public static void beforeClassTest() {
        performers = new ArrayList<String>();
        performers.add("Daniel Manner");
        performers.add("Mark Waltenberf");
        performers.add("Garry Bolderman");
        performers.add("Li Young");
        performers.add("Daniel Manner");
        performers.add("Mark Waltenberf");
        performers.add("Garry Bolderman");
        performers.add("Li Young");
        performers.add("Daniel Manner");
        performers.add("Mark Waltenberf");
        performers.add("Garry Bolderman");
        performers.add("Li Young");
        reports = new ArrayList<Report>();
        for (int i = 0; i < performers.size(); i++) {
            reports.add(new Report(Long.valueOf(i), new Date(), new Date(),
                    performers.get(i), "acting"));
        }
        reportsWithIdenticalPerformer = new ArrayList<Report>();
        for (int i = 0; i < performers.size(); i++) {
            reportsWithIdenticalPerformer.add(new Report(Long.valueOf(i),
                    new Date(), new Date(), "Daniel Borget", "acting"));
        }
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
        pagers = new HashMap<String, PagedListHolder<Report>>(2);
        PagedListHolder pagerReports = new PagedListHolder(reports);
        pagerReports.setPageSize(PAGINATION_THRESHOLD);
        pagers.put("11111111", pagerReports);
        PagedListHolder pagerReportsWithIdenticalPerformer =
                new PagedListHolder(reportsWithIdenticalPerformer);
        pagerReports.setPageSize(PAGINATION_THRESHOLD);
        pagers.put("22222222", pagerReportsWithIdenticalPerformer);
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

    @Test
    public void doSearchEmptyResultTest() {
        ReportService reportService = mock(ReportService.class);
        controller.setReportService(reportService);
        Date startDate = new Date();
        Date endDate = new Date();
        String performer = reportsWithIdenticalPerformer.get(0).getPerformer();
        when(reportService.getReports(startDate, endDate)).thenReturn(emptyReportsList);
        when(reportService.getReports(performer, startDate, endDate))
                .thenReturn(emptyReportsList);
        /*
         * with performer set
         */
        String nothingFoundViewResult =
                controller.doSearch(startDate, endDate, performer, null, model);
        assertEquals(nothingFoundViewResult, "redirect:search/nothing");
        /*
         * without performer set
         */
        nothingFoundViewResult = controller.doSearch(startDate, endDate, "", null, model);
        assertEquals(nothingFoundViewResult, "redirect:search/nothing");
    }

    /**
     * Test 'doSearch' method
     */
    @Test
    public void doSearchWithResultsTest() {
        ReportService reportService = mock(ReportService.class);
        controller.setReportService(reportService);
        Date startDate = new Date();
        Date endDate = new Date();
        String performer = reportsWithIdenticalPerformer.get(0).getPerformer();
        when(reportService.getReports(startDate, endDate)).thenReturn(reports);
        when(reportService.getReports(performer, startDate, endDate))
                .thenReturn(reportsWithIdenticalPerformer);
        /*
         * with performer set
         */
        String successViewResult = controller.doSearch(startDate, endDate, performer,
                pagers, model);
        assertEquals(successViewResult, "redirect:search");
        assertTrue(model.containsKey("search_id"));
        assertNotNull(model.get("search_id"));
        String searchId = (String) model.get("search_id");
        assertTrue(model.containsKey("pagers"));
        assertNotNull(model.get("pagers"));
        assertEquals(pagers.size(), 3);
        assertNotNull(pagers.get(searchId));
        PagedListHolder<Report> pager = pagers.get(searchId);
        assertEquals(pager.getPage(), 0);
        assertEquals(pager.getPageSize(), PAGINATION_THRESHOLD);
        assertSame(pager.getSource(), reportsWithIdenticalPerformer);

        /*
         * without performer set
         */
        successViewResult = controller.doSearch(startDate, endDate, "", pagers, model);
        assertEquals(successViewResult, "redirect:search");
        assertTrue(model.containsKey("search_id"));
        assertNotNull(model.get("search_id"));
        searchId = (String) model.get("search_id");
        assertTrue(model.containsKey("pagers"));
        assertNotNull(model.get("pagers"));
        assertEquals(pagers.size(), 4);
        assertNotNull(pagers.get(searchId));
        pager = pagers.get(searchId);
        assertEquals(pager.getPage(), 0);
        assertEquals(pager.getPageSize(), PAGINATION_THRESHOLD);
        assertSame(pager.getSource(), reports);
    }

    /**
     * Test 'exposeList' method
     */
    @Test
    public void exposeListSuccessTest() {
        String searchId = "11111111";
        String[] pages = new String[]{"1", "2", "3", "prev", "next"};
        /*
         * Test basic usage
         */
        String successViewResult = controller.exposeList(pagers, pages[0],
                searchId, model);
        assertEquals(successViewResult, "list-paged");
        assertTrue(model.containsKey("search_id"));
        assertEquals(model.get("search_id"), searchId);
        assertTrue(model.containsKey("pager"));
        assertTrue(model.containsKey("pager"));
        assertSame(model.get("pager"), pagers.get(searchId));
        PagedListHolder<Report> pager = (PagedListHolder<Report>) model.get("pager");
        assertEquals(pager.getPage(), 0);
        /*
         * Test random page.
         */
        model.clear();
        successViewResult = controller.exposeList(pagers, pages[1],
                searchId, model);
        assertEquals(successViewResult, "list-paged");
        pager = (PagedListHolder<Report>) model.get("pager");
        assertNotNull(pager);
        System.out.println(pager.getPageCount());
        assertEquals(1, pager.getPage());
        /*
         * test 'prev' page.
         */
        successViewResult = controller.exposeList(pagers, pages[3],
                searchId, model);
        assertEquals(successViewResult, "list-paged");
        pager = (PagedListHolder<Report>) model.get("pager");
        assertNotNull(pager);
        assertEquals(0, pager.getPage());
        /*
         * test 'prev' page. Stay on the first page
         */
        successViewResult = controller.exposeList(pagers, pages[3],
                searchId, model);
        assertEquals(successViewResult, "list-paged");
        pager = (PagedListHolder<Report>) model.get("pager");
        assertNotNull(pager);
        assertEquals(0, pager.getPage());
        /**
         * Test 'next' page.
         */
        successViewResult = controller.exposeList(pagers, pages[4],
                searchId, model);
        assertEquals(successViewResult, "list-paged");
        pager = (PagedListHolder<Report>) model.get("pager");
        assertNotNull(pager);
        assertEquals(1, pager.getPage());
        /**
         * Test 'next' page. Stay on the last page
         */
        successViewResult = controller.exposeList(pagers, pages[4],
                searchId, model);
        assertEquals(successViewResult, "list-paged");
        pager = (PagedListHolder<Report>) model.get("pager");
        assertNotNull(pager);
        assertEquals(1, pager.getPage());
        /*
         * Page absense. Should render the first page
         */
        successViewResult = controller.exposeList(pagers, null,
                searchId, model);
        assertEquals(successViewResult, "list-paged");
        pager = (PagedListHolder<Report>) model.get("pager");
        assertNotNull(pager);
        assertEquals(0, pager.getPage());
    }

    @Test
    public void exposeListFailureTest() {
        String searchId = "11111111";
        String[] pages = new String[]{"1", "2", "3", "prev", "next"};
        /*
         * Absent pagers
         */
        String failureViewResult = controller.exposeList(null, pages[0],
                searchId, model);
        assertEquals("redirect:search/nothing", failureViewResult);
        /*
         * Empty page
         */
        failureViewResult = controller.exposeList(pagers, "",
                searchId, model);
        assertEquals("redirect:search/nothing", failureViewResult);
        /*
         * Out-of-range page
         */
        failureViewResult = controller.exposeList(pagers, "1000",
                searchId, model);
        assertEquals("redirect:search/nothing", failureViewResult);
        /*
         * Absense of search ID
         */
        failureViewResult = controller.exposeList(pagers, pages[0],
                null, model);
        assertEquals("redirect:search/nothing", failureViewResult);
        /*
         * Absense of a pager with a given search id
         */
        failureViewResult = controller.exposeList(pagers, pages[0],
                "", model);
        assertEquals("redirect:search/nothing", failureViewResult);
    }

    /**
     * Test 'nothinFound' method
     */
    @Test
    public void nothingFoundTest() {
        String result = controller.nothingFound(model);
        assertEquals("nothing", result);
    }

    /**
     * Test of detail method, of class ReportController.
     */
    @Test
    public void setupDetailFormTest() {
        String result = controller.setupDetailForm(model);
        assertEquals(model.containsAttribute("view"), true);
        assertEquals(model.asMap().get("view"), "byid");
        assertEquals(result, "byid");
    }

    /**
     * Test of detail method, of class ReportController.
     */
    @Test
    public void searchDetailsTest() {
        Long idHas = 1L;
        Long idHasnt = 100L;
        ReportService reportService = mock(ReportService.class);
        when(reportService.hasReport(idHas)).thenReturn(Boolean.TRUE);
        when(reportService.hasReport(idHasnt)).thenReturn(Boolean.FALSE);
        controller.setReportService(reportService);

        String successSearch = controller.searchDetails(idHas, model);
        assertEquals(successSearch, "redirect:detail/" + idHas);

        model.clear();
        String failureSearch = controller.searchDetails(idHasnt, model);
        assertEquals(failureSearch, "byid");
        assertEquals("byid", model.get("view"));
        assertEquals(idHasnt, model.get("id"));
        assertEquals("byid.id.wrong", model.get("error"));
    }

    /**
     * Test REST detail by id method
     */
    //@Test 
    public void detailByIdTest() {
        Long id = 1L;
        Report expReport = new Report(id, new Date(), new Date(), "", "");
        ReportService reportService = mock(ReportService.class);
        when(reportService.getReportById(id)).thenReturn(expReport);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURL()).thenReturn(new StringBuffer("/reports/" + id));

        String result = controller.detailById(id, model, req);
        assertEquals("detail", result);
        assertEquals("/reports/" + id, model.get("uri"));
        assertSame(expReport, model.get("report"));
    }

    /**
     * Test setup add form
     */
    @Test
    public void setupAddFormTest() {
        String result = controller.setupAddForm(model, null);
        assertEquals("add", model.get("view"));
        assertNotNull(model.get("report"));
        assertEquals("add", result);
    }

    /**
     * Test add method
     */
    @Test
    public void addSuccessTest() {
        Report expReport = new Report(1L, new Date(), new Date(),
                "Will Smith", "showoffing");
        System.out.println(expReport);
        BindingResult bindResult = mock(BindingResult.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(req.getRequestURL()).thenReturn(new StringBuffer("http://www.repors.com/add"));
        when(req.getServerName()).thenReturn("www.reports.com");
        when(req.getServerPort()).thenReturn(80);
        when(req.getContextPath()).thenReturn("/sp");
        when(reportService.addReport(expReport)).thenReturn(expReport);

        ModelAndView resultMav = controller.add(expReport, bindResult, model, req, res);
        assertNotNull(resultMav);
        ModelAndViewAssert.assertViewName(resultMav, "detail");
        ModelAndViewAssert.assertModelAttributeValues(resultMav, model);


    }

    @Test
    public void addFailureTest() {
        Report expReport = new Report(1L, new Date(), new Date(),
                "Will Smith", "showoffing");
        BindingResult bindResult = mock(BindingResult.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(bindResult.hasErrors()).thenReturn(Boolean.TRUE);

        ModelAndView resultMav = controller.add(expReport, bindResult, model, req, res);

        assertNotNull(resultMav);
        ModelAndViewAssert.assertViewName(resultMav, "add");
        ModelAndViewAssert.assertModelAttributeValue(resultMav, "view", "add");
        ModelAndViewAssert.assertModelAttributeValues(resultMav, model);
    }

    @Test
    public void realtimeSearchTest() {
        String result = controller.realTimeSearch(model);
        assertEquals("search", result);
        assertEquals("ajax", model.get("view"));
    }
}