package kkonrad.web.crawler.orchestration;

import kkonrad.web.crawler.core.Link;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Predicate;

@Slf4j
public final class Filters {

    private Filters() {
    }

    public static Predicate<Link> sameDomainFilter(URI domain) {
        return link -> isSameDomain(domain, link);
    }

    // Here we define what we mean by the same domain
    private static boolean isSameDomain(URI domain, Link link) {
        try {
            URI linkUri = new URI(link.getTo());
            return linkUri.getScheme().equals(domain.getScheme())
                    && linkUri.getHost().equals(domain.getHost())
                    && linkUri.getPort() == domain.getPort();
        } catch (URISyntaxException e) {
            log.warn("Skipping: Could not parse link " + link.getTo());
            return false;
        }
    }
}
