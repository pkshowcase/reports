package sp.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import sp.service.ReportService;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import sp.model.Report;
import sp.model.ajax.AjaxResponse;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.Is.*;
import org.junit.Ignore;
import org.springframework.beans.support.PagedListHolder;

/**
 *
 * @author Paul Kulitski
 */
public class AjaxControllerTest {

    public AjaxControllerTest() {
    }
    ReportService reportService;
    MessageSource messageSource;
    AjaxController controller;
    ExtendedModelMap model;

    @Test
    public void getReportTest() {
        Long idHas = 1L;
        Long idHasnt = 1000L;
        Long idInvalid = -1L;
        Report expReport = new Report(idHas, new Date(), new Date(), "", "");
        when(reportService.getReportById(idHas)).thenReturn(expReport);
        when(reportService.getReportById(idHasnt)).thenReturn(null);

        Report result = controller.getReport(idHas, model);
        assertNotNull(result);
        assertSame(expReport, result);

        model.clear();
        result = controller.getReport(idHasnt, model);
        assertNull(result);

        model.clear();
        result = controller.getReport(idInvalid, model);
        assertNull(result);
    }

    @Test
    public void removeReport() {
        Long idHas = 1L;
        Long idHasnt = 1000L;
        Long idInvalid = -1L;
        when(reportService.hasReport(idHas)).thenReturn(Boolean.TRUE);

        String result = controller.removeReport(idHas, model);
        assertEquals("success", result);

        model.clear();
        when(reportService.hasReport(idHasnt)).thenReturn(Boolean.FALSE);
        result = controller.removeReport(idHasnt, model);
        assertEquals("missing", result);

        model.clear();
        when(reportService.hasReport(idInvalid)).thenReturn(Boolean.FALSE);
        result = controller.removeReport(idInvalid, model);
        assertEquals("missing", result);
    }

    @Test
    public void updateTest() {
        Report expReport = new Report(1L, new Date(), new Date(), "", "");
        BindingResult bindResult = mock(BindingResult.class);
        when(bindResult.hasErrors()).thenReturn(Boolean.FALSE);
        when(reportService.hasReport(expReport.getId())).thenReturn(Boolean.TRUE);

        /*
         * If report exist and was updated
         */
        AjaxResponse successResult = controller.update(expReport, bindResult, Locale.ENGLISH, model);
        assertEquals(new AjaxResponse(AjaxResponse.SUCCESS), successResult);
        verify(reportService).updateReport(expReport);

        when(bindResult.hasErrors()).thenReturn(Boolean.TRUE);

        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("performer");
        when(fieldError.getRejectedValue()).thenReturn(expReport.getPerformer());

        ObjectError objectError = mock(ObjectError.class);
        when(objectError.getObjectName()).thenReturn("report");

        when(messageSource.getMessage(fieldError, Locale.ENGLISH)).thenReturn("not valid performer field");
        when(messageSource.getMessage(objectError, Locale.ENGLISH)).thenReturn("not valid report");

        List fl = Arrays.asList(fieldError);
        List ol = Arrays.asList(objectError);
        when(bindResult.getFieldErrors()).thenReturn(fl);
        when(bindResult.getGlobalErrors()).thenReturn(ol);

        AjaxResponse failResult = controller.update(expReport, bindResult, Locale.ENGLISH, model);
        assertEquals(AjaxResponse.ERROR, failResult.getStatus());
        verify(reportService, times(1)).updateReport(expReport);
        assertEquals(2, failResult.getErrors().size());

        /*
         * If report is absent
         */
        when(reportService.hasReport(expReport.getId())).thenReturn(Boolean.FALSE);
        AjaxResponse absentResult = controller.update(expReport, bindResult, Locale.ENGLISH, model);
        assertEquals(new AjaxResponse(AjaxResponse.ERROR), absentResult);
        verify(reportService, times(1)).updateReport(expReport);
    }

    @Test
    public void addTest() {
        Report expReport = new Report(1L, new Date(), new Date(), "", "");
        BindingResult bindResult = mock(BindingResult.class);
        when(bindResult.hasErrors()).thenReturn(Boolean.FALSE);

        /*
         * If hasn't errors
         */
        AjaxResponse successResult = controller.add(expReport, bindResult, Locale.ENGLISH, model);
        assertEquals(new AjaxResponse(AjaxResponse.SUCCESS), successResult);
        verify(reportService).addReport(expReport);

        /*
         * If has errors
         */
        when(bindResult.hasErrors()).thenReturn(Boolean.TRUE);

        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("performer");
        when(fieldError.getRejectedValue()).thenReturn(expReport.getPerformer());

        ObjectError objectError = mock(ObjectError.class);
        when(objectError.getObjectName()).thenReturn("report");

        when(messageSource.getMessage(fieldError, Locale.ENGLISH)).thenReturn("not valid performer field");
        when(messageSource.getMessage(objectError, Locale.ENGLISH)).thenReturn("not valid report");

        List fl = Arrays.asList(fieldError);
        List ol = Arrays.asList(objectError);
        when(bindResult.getFieldErrors()).thenReturn(fl);
        when(bindResult.getGlobalErrors()).thenReturn(ol);

        AjaxResponse failResult = controller.add(expReport, bindResult, Locale.ENGLISH, model);
        assertEquals(AjaxResponse.ERROR, failResult.getStatus());
        verify(reportService, times(1)).addReport(expReport);
        assertEquals(2, failResult.getErrors().size());
    }

    @Test
    public void watchReportById() {
        Long id = 1L;
        when(reportService.hasReport(id)).thenReturn(Boolean.TRUE);
        HttpSession session = mock(HttpSession.class);
        Set<Long> checklist = new HashSet<Long>();
        checklist.addAll(Arrays.asList(2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L));
        int wasSize = checklist.size();
        when(session.getAttribute("checklist")).thenReturn(checklist);

        /*
         * if success
         */
        String result = controller.watchReportById(id, session, model);
        assertEquals("success", result);
        assertEquals(wasSize + 1, checklist.size());
        verify(session, times(1)).getAttribute("checklist");

        /*
         * hasReport is TRUE, but checklist is empty
         */
        when(session.getAttribute("checklist")).thenReturn(null);
        result = controller.watchReportById(id, session, model);
        assertEquals("", result);

        /*
         * hasReport is FALSE, but checklist isn't empty
         */
        when(reportService.hasReport(id)).thenReturn(Boolean.FALSE);
        when(session.getAttribute("checklist")).thenReturn(checklist);
        result = controller.watchReportById(id, session, model);
        assertEquals("", result);
    }

    @Test
    public void watchAllTest() {
        Long[] ids = new Long[]{1L, 11L, 12L, 13L};
        when(reportService.hasReports(ids)).thenReturn(new Long[]{1L});
        HttpSession session = mock(HttpSession.class);
        Set<Long> checklist = new HashSet<Long>();
        checklist.addAll(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L));
        int wasSize = checklist.size();

        /*
         * basic usecase
         */
        when(session.getAttribute("checklist")).thenReturn(checklist);
        String result = controller.watchAll(ids, session, model);
        assertThat("", is(not(result)));
        assertEquals(wasSize, checklist.size());

        /*
         * if checklist is empty
         */
        when(session.getAttribute("checklist")).thenReturn(null);
        result = controller.watchAll(ids, session, model);
        assertEquals("", result);

        /*
         * if hasn't any reports
         */
        when(session.getAttribute("checklist")).thenReturn(checklist);
        when(reportService.hasReports(ids)).thenReturn(null);
        result = controller.watchAll(ids, session, model);
        assertEquals("", result);
    }

    @Test
    @Ignore
    public void getUriTest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        Long id = 1L;
        String result = controller.getUri(id, req, model);
        assertThat("", is(not(result)));
        when(reportService.hasReport(id)).thenReturn(Boolean.FALSE);
        result = controller.getUri(id, req, model);
        assertEquals("", result);
    }

    @Test
    @Ignore
    public void removeFromPagerTest() {
        Long id = 1L;
        String searchId = "11111111";
        HttpSession session = mock(HttpSession.class);
        Map<String, PagedListHolder<Report>> pagers =
                new HashMap<String, PagedListHolder<Report>>();
        Report[] reports = new Report[32];
        for (int i = 0; i < 32; i++) {
            reports[i] = new Report(Long.valueOf(i), new Date(), new Date(), "", "");
        }
        PagedListHolder<Report> pager = new PagedListHolder<Report>(Arrays.asList(reports));
        int wasSize = pager.getSource().size();
        pagers.put("11111111", pager);

        when(session.getAttribute("pagers")).thenReturn(pagers);
        String result = controller.removeFromPager(id, searchId, session, model);
        assertEquals("success", result);
        assertEquals(wasSize + 1, pager.getSource().size());
        
        /*
         * id pager is null
         */
        pager = null;
        pagers.put("11111111", pager);
        result = controller.removeFromPager(id, searchId, session, model);
        assertEquals("", result);
    }

    @Before
    public void setUp() {
        controller = new AjaxController();
        model = new ExtendedModelMap();
        reportService = mock(ReportService.class);
        messageSource = mock(MessageSource.class);
        controller.reportService = reportService;
        controller.messageSource = messageSource;
    }
}