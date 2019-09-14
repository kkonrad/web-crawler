package kkonrad.web.crawler.utils;

import java.util.ArrayList;
import java.util.List;

public final class CollectionsUtils {

    private CollectionsUtils() {
    }


    public static <T> List<T> joinLists(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<>(list1.size() + list2.size());
        list.addAll(list1);
        list.addAll(list2);
        return list;
    }
}
