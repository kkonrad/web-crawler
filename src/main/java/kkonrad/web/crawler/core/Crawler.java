package kkonrad.web.crawler.core;

public interface Crawler {

    void crawl(Link seed, PageScraper pageScraper);
}
