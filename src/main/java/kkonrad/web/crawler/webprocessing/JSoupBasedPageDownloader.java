package kkonrad.web.crawler.webprocessing;

import kkonrad.web.crawler.core.Link;
import kkonrad.web.crawler.core.PageDownloader;
import kkonrad.web.crawler.core.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static kkonrad.web.crawler.utils.CollectionsUtils.joinLists;

// Other implementation could render page with JS. Selenium could be used for this
// Extracting links code is from jsoup official documentation
@Slf4j
public class JSoupBasedPageDownloader implements PageDownloader {

    public WebPage download(Link nextLink) {
        String nextUrl = nextLink.getTo();
        log.info("Downloading page at {}", nextUrl);
        try {
            Document document = Jsoup.connect(nextUrl).get();
            return buildWebPage(document);
        } catch (IOException ex) {
            log.error("Failed to download page at {}", nextUrl, ex);
            return WebPage.emptyWebPage();
        }
    }

    private WebPage buildWebPage(Document document) {
        return WebPage.builder()
                .links(getLinks(document))
                .others(getOtherLinks(document))
                .build();
    }

    private List<Link> getLinks(Document document) {
        List<Link> links = extractLinks(document, "a[href]", "abs:href");
        List<Link> frames = extractLinks(document, "frame[src]", "abs:src");
        return joinLists(links, frames);
    }

    private List<Link> getOtherLinks(Document document) {
        List<Link> media = extractLinks(document, "[src]", "abs:src");
        List<Link> imports = extractLinks(document, "link[href]", "abs:href");
        return joinLists(media, imports);
    }

    private List<Link> extractLinks(Document document, String selectQuery, String attrKey) {
        return document.select(selectQuery)
                .stream()
                .map(element -> element.attr(attrKey))
                .filter(attr -> attr != null && !attr.isEmpty())
                .map(Link::new)
                .collect(Collectors.toList());
    }

}
