package sp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ui.ExtendedModelMap;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import static org.mockito.Mockito.*;
import sp.model.Register;
import sp.service.RegistryService;
import sp.util.SpLazyPager;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 *
 * @author Paul Kulitski
 */
public class RegistryControllerTest {

    RegistryController controller;
    ExtendedModelMap model;
    RegistryService registryService;
    static List registers;
    static int MAX_ON_PAGER = 4;
    static int PAGINATION_THRESHOLD = 10;

    @BeforeClass
    public static void beforeClass() {
        registers = new ArrayList();
        for (int i = 0; i < 10; i++) {
            registers.add(new Register());
        }
    }

    @Before
    public void before() {
        model = new ExtendedModelMap();
        controller = new RegistryController();
        registryService = mock(RegistryService.class);
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("maxOnPager", String.valueOf(MAX_ON_PAGER));
        settings.put("pagerThreshold", String.valueOf(PAGINATION_THRESHOLD));
        controller.PAGINATION_THRESHOLD = PAGINATION_THRESHOLD;
        controller.MAX_ON_PAGER = MAX_ON_PAGER;
        controller.registryService = registryService;
    }

    @Test
    public void referenceDataTest() {
        String result = controller.referenceData();
        assertEquals("title.registry", result);
    }

    @Test
    public void nothingFoundTest() {
        String result = controller.nothingFound(model);
        assertEquals("nothing", result);
        assertEquals("/registry?new", model.get("backpath"));
    }

    @Test
    public void showRegistryTest() {
        String[] pages = new String[]{"1", "2", "3", "prev", "next"};
        SpLazyPager pager = new SpLazyPager();

        /*
         * basic usecase: new search, registers were found
         */
        when(registryService.count()).thenReturn(1005L);
        when(registryService.getRegisters(0, controller.PAGINATION_THRESHOLD))
                .thenReturn(registers);
        String result = controller.showRegistry(pages[2], "", pager, model);
        assertEquals("registry", result);
        assertThat(model.get("registers"), instanceOf(List.class));
        assertEquals(controller.PAGINATION_THRESHOLD,
                ((List) (model.get("registers"))).size());
        assertEquals(0, pager.getPage());
        assertEquals(registryService.count().intValue(), pager.getSourceCount());
        assertEquals(controller.PAGINATION_THRESHOLD, pager.getPageSize());

        /*
         * basic usecase: new search, wesn't found any registers
         */
        model.clear();
        when(registryService.count()).thenReturn(0L);
        when(registryService.getRegisters(0, controller.PAGINATION_THRESHOLD))
                .thenReturn(Collections.EMPTY_LIST);
        result = controller.showRegistry(pages[2], "", pager, model);
        assertEquals("redirect:registry/nothing", result);
    }

    @Test
    public void showRegistryPagesTest() {
        String[] pages = new String[]{"1", "2", "3", "prev", "next"};
        SpLazyPager pager = new SpLazyPager();

        /*
         * request for a page, basic usage
         */
        when(registryService.count()).thenReturn(1005L);

        pager.setPageSize(controller.PAGINATION_THRESHOLD);
        pager.setSourceCount(controller.PAGINATION_THRESHOLD * 3);
        pager.setPage(2);

        when(registryService.getRegisters(pager.getPageOffset(), pager.getPageSize()))
                .thenReturn(registers);

        String result = controller.showRegistry(pages[2], null, pager, model);
        assertEquals("registry", result);
        assertNotNull(model.get("registers"));
        verify(registryService).getRegisters(pager.getPageOffset(), pager.getPageSize());
        assertSame(registers, model.get("registers"));
        assertEquals(2, pager.getPage());

        /*
         * test 'next' page. If on the last, then stays on the same
         */
        model.clear();
        result = controller.showRegistry(pages[4], null, pager, model);
        assertEquals("registry", result);
        assertNotNull(model.get("registers"));
        verify(registryService, times(2)).getRegisters(pager.getPageOffset(), pager.getPageSize());
        assertSame(registers, model.get("registers"));
        assertEquals(2, pager.getPage());

        /*
         * test 'prev' page. If on the first, then stays on the same page
         */
        model.clear();

        pager.setPage(pager.getPage() - 1);
        when(registryService.getRegisters(pager.getPageOffset(), pager.getPageSize()))
                .thenReturn(registers);
        pager.setPage(pager.getPage() + 1);

        result = controller.showRegistry(pages[3], null, pager, model);
        assertEquals("registry", result);
        assertNotNull(model.get("registers"));
        verify(registryService, times(1)).getRegisters(pager.getPageOffset(), pager.getPageSize());
        assertSame(registers, model.get("registers"));
        assertEquals(1, pager.getPage());
    }

    @Test
    public void showRegistryFailureTest() {
        SpLazyPager pager = new SpLazyPager();
        /*
         * Request for page, but pager is null
         */
        String result = controller.showRegistry("1", null, null, model);
        assertEquals("redirect:registry/nothing", result);
        verifyZeroInteractions(registryService);
        /*
         * Incorrect page value
         */
        model.clear();
        result = controller.showRegistry("1incorrect", "", pager, model);
        assertEquals("redirect:registry/nothing", result);

        /*
         * Nothing found in registryService
         */
        model.clear();
        when(registryService.getRegisters(0, controller.PAGINATION_THRESHOLD))
                .thenReturn(Collections.EMPTY_LIST);
        result = controller.showRegistry(null, "", pager, model);
        assertEquals("redirect:registry/nothing", result);
    }
}
