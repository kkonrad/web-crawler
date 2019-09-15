package kkonrad.web.crawler.orchestration;

import kkonrad.web.crawler.core.CrawlingOrchestrator;
import kkonrad.web.crawler.core.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Provides BFS like crawling through websites
 */
public class SimpleCrawlingOrchestrator implements CrawlingOrchestrator {

    private final Set<Link> handled = ConcurrentHashMap.newKeySet();
    private final Predicate<? super Link> filter;

    public SimpleCrawlingOrchestrator() {
        this(x -> true);
    }

    /**
     * @param filter if returns true link is OK for processing
     */
    public SimpleCrawlingOrchestrator(Predicate<? super Link> filter) {
        this.filter = filter;
    }

    @Override
    public List<Link> handleNewLinksReturnOnesForCrawling(Collection<? extends Link> links) {
        return links.stream()
                .map(this::reducedLink)
                .filter(filter)
                .filter(handled::add)
                .collect(Collectors.toList());
    }

    /**
     * Removes parts related to subsections within page (#... part in URL)
     * @param original
     * @return
     */
    private Link reducedLink(Link original) {
        String url = original.getTo();
        if (!url.contains("#")) {
            return original;
        }
        String reducedUrl = url.substring(0, url.indexOf("#"));
        return new Link(reducedUrl);
    }
}
