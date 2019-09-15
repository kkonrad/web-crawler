package kkonrad.web.crawler.core;

/**
 * Classes implementing this interface should be thread safe
 */
public interface PageScraper {

    void scrape(WebPage webPage);
}
