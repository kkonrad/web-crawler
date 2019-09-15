package kkonrad.web.crawler.crawlers;

import kkonrad.web.crawler.core.Crawler;
import kkonrad.web.crawler.core.CrawlingOrchestrator;
import kkonrad.web.crawler.core.Link;
import kkonrad.web.crawler.core.PageDownloader;
import kkonrad.web.crawler.core.PageScraper;
import kkonrad.web.crawler.core.WebPage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

/**
 * Given more time this class could be refactored so that instances of if are re-usable
 * (or this code is the one that provides re-usability for higher layer).
 *
 * Now it is "ugly" as its objects can be used only once although interface might suggest something different
 */
public class ThreadedCrawler implements Crawler {

    private final CrawlingOrchestrator orchestrator;
    private final PageDownloader pageDownloader;
    private PageScraper pageScraper;

    private final ExecutorService executorService;
    private Phaser phaser;

    public ThreadedCrawler(CrawlingOrchestrator orchestrator, PageDownloader pageDownloader, ExecutorService executorService) {
        this.orchestrator = orchestrator;
        this.pageDownloader = pageDownloader;
        this.executorService = executorService;
    }

    @Override
    public void crawl(Link seed, PageScraper pageScraper) {
        this.pageScraper = pageScraper;
        phaser = new Phaser(1);
        int phase = phaser.getPhase();
        scheduleProcessingLink(seed);
        phaser.awaitAdvance(phase);
    }

    private void scheduleProcessingLink(Link nextLink) {
        executorService.submit(() -> processLink(nextLink));
    }

    private void processLink(Link nextLink) {
        WebPage page = pageDownloader.download(nextLink);
        pageScraper.scrape(page);
        handleLinks(page.getLinks());
        phaser.arriveAndDeregister();
    }

    private void handleLinks(List<Link> links) {
        List<Link> linksToCrawl = orchestrator.handleNewLinksReturnOnesForCrawling(links);
        linksToCrawl.forEach(this::addLinkToCrawl);
    }

    private void addLinkToCrawl(Link link) {
        phaser.register();
        scheduleProcessingLink(link);
    }
}
