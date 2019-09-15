package kkonrad.web.crawler;

import kkonrad.web.crawler.core.Crawler;
import kkonrad.web.crawler.core.CrawlingOrchestrator;
import kkonrad.web.crawler.core.Link;
import kkonrad.web.crawler.core.PageDownloader;
import kkonrad.web.crawler.crawlers.ThreadedCrawler;
import kkonrad.web.crawler.orchestration.Filters;
import kkonrad.web.crawler.orchestration.SimpleCrawlingOrchestrator;
import kkonrad.web.crawler.scraping.SiteMapGeneratingPageScraper;
import kkonrad.web.crawler.webprocessing.JSoupBasedPageDownloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
public class Application {

    public static final String CRAWLED_PAGES_OUT_FILE = "./crawled_pages.out";
    public static final String SITE_MAP_OUT_FILE = "./site_map.out";

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);

        String pageUrl = args[0];
        Link seedLink = new Link(pageUrl);
        URI domain = URI.create(pageUrl);

        crawl(seedLink, domain);
    }

    public static SiteMapGeneratingPageScraper crawl(Link seedLink, URI domain) throws IOException {
        ExecutorService executorService = getExecutorService();
        CrawlingOrchestrator crawlingOrchestrator = getCrawlingOrchestratorForDomain(domain);
        PageDownloader pageDownloader = getPageDownloader();

        Crawler crawler = new ThreadedCrawler(
                crawlingOrchestrator,
                pageDownloader,
                executorService);

        SiteMapGeneratingPageScraper results = crawlPage(crawler, seedLink);
        closeExecutorService(executorService);
        presentResults(results);
        return results;
    }

    private static ExecutorService getExecutorService() {
        return Executors.newScheduledThreadPool(3 * Runtime.getRuntime().availableProcessors());
    }

    private static CrawlingOrchestrator getCrawlingOrchestratorForDomain(URI domain) {
        return new SimpleCrawlingOrchestrator(Filters.sameDomainFilter(domain));
    }

    private static PageDownloader getPageDownloader() {
        return new JSoupBasedPageDownloader();
    }

    private static void closeExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static SiteMapGeneratingPageScraper crawlPage(Crawler crawler, Link seedLink) {
        SiteMapGeneratingPageScraper resultsProcessor = new SiteMapGeneratingPageScraper();
        Instant start = Instant.now();
        crawler.crawl(seedLink, resultsProcessor);
        log.info("Crawler " + crawler.getClass() + " crawled for " + (Duration.between(start, Instant.now())));
        return resultsProcessor;
    }

    private static void presentResults(SiteMapGeneratingPageScraper siteMap) throws IOException {
        Map<String, List<String>> simplifiedMap = siteMap.getSimplifiedAndSortedSiteMap();

        String strCrawledPages = strCrawledPages(simplifiedMap.keySet());
        System.out.println(strCrawledPages);
        Files.write(Paths.get(CRAWLED_PAGES_OUT_FILE), strCrawledPages.getBytes());

        String strSiteMap = strSiteMap(simplifiedMap);
        Files.write(Paths.get(SITE_MAP_OUT_FILE), strSiteMap.getBytes());
    }

    private static String strCrawledPages(Collection<String> crawledPages) {
        return String.join(System.lineSeparator(), crawledPages) + System.lineSeparator();
    }

    private static String strSiteMap(Map<String, List<String>> siteMap) {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, List<String>> siteMapEntry: siteMap.entrySet()) {
            String mainUrl = siteMapEntry.getKey();
            List<String> links = siteMapEntry.getValue();

            out.append(mainUrl + System.lineSeparator());
            for (String link: links) {
                out.append("|- " + link + System.lineSeparator());
            }
            out.append(System.lineSeparator());
        }
        return out.toString();
    }

}
