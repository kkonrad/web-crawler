package kkonrad.web.crawler.core;


import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder // All args constructor could be error prone due to the same type of arguments
public class WebPage {

    private List<Link> links;
    private List<Link> others;

    public static WebPage emptyWebPage() {
        return WebPage.builder()
                .links(Collections.emptyList())
                .others(Collections.emptyList())
                .build();
    }

}
