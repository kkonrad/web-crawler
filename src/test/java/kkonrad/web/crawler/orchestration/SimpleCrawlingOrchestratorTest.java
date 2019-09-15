package kkonrad.web.crawler.orchestration;

import kkonrad.web.crawler.core.Link;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class SimpleCrawlingOrchestratorTest {

    private static final String SAMPLES_DOMAIN = "http://cool-website.com";

    private static final Link LINK_1 = new Link(SAMPLES_DOMAIN + "/aaaaa");
    private static final Link LINK_2 = new Link(SAMPLES_DOMAIN + "/bbbbb");

    private static final List<Link> SAMPLE_LINKS = Lists.list(LINK_1, LINK_2);

    private static final Link OTHER_DOMAIN_LINK = new Link("http://not-cool-domain-at-all.com");
    private static final List<Link> OTHER_DOMAIN_SAMPLE_LINKS = Lists.list(OTHER_DOMAIN_LINK);


    private SimpleCrawlingOrchestrator orchestrator;

    @Test
    public void normalUsageFlow() {
        orchestrator = new SimpleCrawlingOrchestrator();

        assertThat(orchestrator.handleNewLinksReturnOnesForCrawling(SAMPLE_LINKS), is(SAMPLE_LINKS));
        assertThat(orchestrator.handleNewLinksReturnOnesForCrawling(SAMPLE_LINKS), is(Collections.EMPTY_LIST));
    }

    @Test
    public void domainFilteringTests() {
        orchestrator = new SimpleCrawlingOrchestrator(Filters.sameDomainFilter(URI.create(SAMPLES_DOMAIN)));

        assertThat(orchestrator.handleNewLinksReturnOnesForCrawling(SAMPLE_LINKS), is(SAMPLE_LINKS));

        assertThat(orchestrator.handleNewLinksReturnOnesForCrawling(OTHER_DOMAIN_SAMPLE_LINKS), is(Collections.EMPTY_LIST));
    }

}