package kkonrad.web.crawler.scraping;

import kkonrad.web.crawler.core.PageScraper;
import kkonrad.web.crawler.core.Link;
import kkonrad.web.crawler.core.WebPage;
import kkonrad.web.crawler.utils.CollectionsUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For each scraped website it stores information to what it linked to or what other HTML components it referer
 *
 * Making it thread safe with assumptions that it won't be called for the same page twice
 *
 */
public class SiteMapGeneratingPageScraper implements PageScraper {

    private final Map<WebPage, List<Link>> links = new ConcurrentHashMap<>();

    @Override
    public void scrape(WebPage webPage) {
        List<Link> referencedItems = CollectionsUtils.joinLists(
                webPage.getLinks(),
                webPage.getOthers()
        );

        links.put(webPage, referencedItems);
    }

    // Probably this set should be copied but in our case object will be "read" after it is populated with data
    public Map<WebPage, List<Link>> getSiteMap() {
        return links;
    }
}
