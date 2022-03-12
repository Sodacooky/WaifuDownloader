package soda;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DownloadUrlExtractor {

    public static List<String> extract(Document htmlDocument) {
        ArrayList<String> urls = new ArrayList<>();
        Elements directlinkElms = htmlDocument.select("a.directlink[href]");
        directlinkElms.forEach(element -> {
            urls.add(element.attr("href"));
        });
        return urls;
    }
}
