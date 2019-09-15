package kkonrad.web.crawler;

import kkonrad.web.crawler.core.CrawlingOrchestrator;
import kkonrad.web.crawler.core.Link;
import kkonrad.web.crawler.core.WebPage;
import kkonrad.web.crawler.crawlers.ThreadedCrawler;
import kkonrad.web.crawler.scraping.SiteMapGeneratingPageScraper;
import org.assertj.core.util.Lists;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class E2ECrawlerTestOnJavaDoc {

    private static final String JAVADOC_URL = "http://localhost:8000";
    private static final Link JAVADOC_LINK = new Link(JAVADOC_URL);
    private static final URI JAVADOC_DOMAIN = URI.create(JAVADOC_URL);

    // TODO use reflection
    private static final List<Class<?>> EXISTING_CODE_CLASSES = Lists.list(
            Application.class,
            WebPage.class,
            Link.class,
            ThreadedCrawler.class,
            CrawlingOrchestrator.class
    );

    private SiteMapGeneratingPageScraper doCrawling(Link javadocLink, URI javadocDomain) throws IOException {
        return Application.crawl(javadocLink, javadocDomain);
    }

    @Before
    public void setUp() {
        Assume.assumeTrue(isJavadocPageServed());
    }

    private boolean isJavadocPageServed() {
        try {
            URL url = new URL(JAVADOC_URL + "/javadoc");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return 200 == connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();// should be good enough
            return false;
        }
    }

    @Test
    public void testDownloadAndLinksDetection() throws IOException {
        SiteMapGeneratingPageScraper resultsProcessor = doCrawling(JAVADOC_LINK, JAVADOC_DOMAIN);
        Set<String> foundLinks = resultsProcessor.getSimplifiedAndSortedSiteMap().keySet();

        for (Class c: EXISTING_CODE_CLASSES) {
            assertThat(
                    foundLinks.stream()
                            .anyMatch(link -> link.endsWith(c.getSimpleName() + ".html")),
                    is(true));
        }
    }
}