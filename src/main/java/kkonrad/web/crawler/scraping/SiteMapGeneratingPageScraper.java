package kkonrad.web.crawler.scraping;

import kkonrad.web.crawler.core.Link;
import kkonrad.web.crawler.core.PageScraper;
import kkonrad.web.crawler.core.WebPage;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * For each scraped website it stores information to what it linked to or what other HTML components it referer
 * <p>
 * Making it thread safe with assumptions that it won't be called for the same page twice
 */
@Slf4j
public class SiteMapGeneratingPageScraper implements PageScraper {

    private final Map<WebPage, Set<Link>> links = new ConcurrentHashMap<>();
//    private final Map<WebPage, List<Link>> links = new ConcurrentSkipListMap<>(Comparator.comparing(WebPage::getUrl));
    // using ConcurrentSkipListMap to provide sorted order of keys, not too important but convenient

    @Override
    public void scrape(WebPage webPage) {
        log.info("Scrapping page: " + webPage.getUrl());
        Set<Link> referencedItems = new HashSet<>(webPage.getLinks());
        referencedItems.addAll(webPage.getOthers());

        links.put(webPage, referencedItems);
    }

    // Probably this set should be copied but in our case object will be "read" after it is populated with data
    public Map<WebPage, Set<Link>> getSiteMap() {
        return links;
    }

    public Map<String, List<String>> getSimplifiedAndSortedSiteMap() {
        return links.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getUrl()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getUrl(),
                        entry -> simplifyAndSortLinkCollection(entry.getValue()),
                        (v1, v2) -> v1,
                        TreeMap::new)
                );
    }

    private List<String> simplifyAndSortLinkCollection(Collection<? extends Link> links) {
        return links.stream()
                .map(Link::getTo)
                .sorted()
                .collect(Collectors.toList());
    }
}
