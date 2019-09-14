package kkonrad.web.crawler.core;

public interface PageDownloader {

    WebPage download(Link nextLink);
}
