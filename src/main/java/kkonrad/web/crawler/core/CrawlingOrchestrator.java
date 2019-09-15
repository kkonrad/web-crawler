package kkonrad.web.crawler.core;

import java.util.Collection;
import java.util.List;

public interface CrawlingOrchestrator {

    List<Link> handleNewLinksReturnOnesForCrawling(Collection<? extends Link> links);
}
