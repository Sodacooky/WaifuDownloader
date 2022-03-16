package soda;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class UrlExtractor {

    //从预览页直接提取大图的链接
    public static List<String> extractLargerUrls(Document PreviewPageHtmlDocument) {
        ArrayList<String> urls = new ArrayList<>();
        Elements directlinkElms = PreviewPageHtmlDocument.select("a.directlink[href]");
        directlinkElms.forEach(element -> {
            urls.add(element.attr("href"));
        });
        return urls;
    }

    //从预览页提取出每一张图的详情页链接
    public static List<String> extractPostPage(Document PreviewPageHtmlDocument) {
        ArrayList<String> urls = new ArrayList<>();
        Elements directlinkElms = PreviewPageHtmlDocument.select("a.thumb[href]");
        directlinkElms.forEach(element -> {
            urls.add(element.attr("abs:href"));
        });
        return urls;
    }

    //在详情页内提取出无损原图链接
    //当没有找到时返回一个空字符串
    public static String extractOriginalFromPost(Document PostPageHtmlDocument) {
        final String[] resultUrl = {""};
        PostPageHtmlDocument.select("a.original-file-unchanged[href]").forEach(element -> {
            resultUrl[0] = element.attr("href");
        });
        return resultUrl[0];
    }

    //在详情页内提取出有损大图链接
    public static String extractLargerFromPost(Document PostPageHtmlDocument) {
        final String[] resultUrl = {""};
        PostPageHtmlDocument.select("a.original-file-changed[href]").forEach(element -> {
            resultUrl[0] = element.attr("href");
        });
        return resultUrl[0];
    }
}
