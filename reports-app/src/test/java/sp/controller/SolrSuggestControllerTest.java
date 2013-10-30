package sp.controller;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.support.BindingAwareModelMap;
import sp.service.SolrService;
import static org.mockito.Mockito.*;
import org.springframework.data.solr.core.query.result.SimpleTermsFieldEntry;
import org.springframework.data.solr.core.query.result.TermsFieldEntry;
import org.springframework.data.solr.core.query.result.TermsPage;

/**
 *
 * @author Paul Kulitski
 */
public class SolrSuggestControllerTest {

    public SolrSuggestControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    ExtendedModelMap model;
    SolrSuggestController controller;
    SolrService solrService;

    @Before
    public void setUp() {
        model = new BindingAwareModelMap();
        controller = new SolrSuggestController();
        solrService = mock(SolrService.class);
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void setupFormTest() {
        TermsPage terms = mock(TermsPage.class);
        List<TermsFieldEntry> content = new ArrayList<TermsFieldEntry>();
        for (int i = 0; i < 10; i++) {
            content.add(new SimpleTermsFieldEntry(String.valueOf(i / 7), Long.valueOf(i)));
        }
        when(solrService.getSearchCloud()).thenReturn(terms);
        when(terms.getContent()).thenReturn(content);

        String result = controller.setupForm(model);
        assertEquals("search-solr", result);
        assertNotNull(model.get("maxOccurrence"));
        assertNotNull(model.get("cloud"));
    }

    @Test
    public void setupFormFailureTest() {
        when(solrService.getSearchCloud()).thenThrow(Exception.class);
        String result = controller.setupForm(model);
        assertEquals("nothing", result);
    }
}