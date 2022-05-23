package waifudownloader.procedure;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import waifudownloader.core.HtmlDownloader;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class YandereProcedure extends Procedure {

    @Override
    public boolean isTagsAmountAvailable() {
        //6 tags limit
        return this.tags.size() <= 6;
    }

    @Override
    public boolean isPageRangeAvailable() {
        //no limit, just check range
        return super.isPageRangeAvailable();
    }

    @Override
    public List<String> generatePreviewPageUrls() {
        //url header
        String header = "https://yande.re/post?page=";
        //url tags arguments
        StringBuilder tagsArgs = new StringBuilder("&tags=");
        for (String tag : this.tags) {
            tagsArgs.append(URLEncoder.encode(tag, StandardCharsets.UTF_8));
            tagsArgs.append("+");
        }
        //build urls
        List<String> result = new ArrayList<>();
        for (int now = this.pageStart; now <= this.pageEnd; now++) result.add(header + now + tagsArgs.toString());
        //
        return result;
    }

    @Override
    public List<String> extractPostPageUrls(String previewPageUrl) {
        //download
        Document document = this.htmlDownloader.download(previewPageUrl);
        //extract
        ArrayList<String> urls = new ArrayList<>();
        Elements postPageElements = document.select("a.thumb[href]");
        //transfer
        postPageElements.forEach(element -> urls.add(element.attr("abs:href")));
        //
        return urls;
    }

    @Override
    public String extractDownloadUrl(String postPageUrl, boolean original) {
        //download
        Document document = this.htmlDownloader.download(postPageUrl);
        //extract
        boolean originalSuccess = false;
        if (original) {
            //try to find the original picture url
            Elements select = document.select("a.original-file-unchanged[href]");
            if (!select.isEmpty()) {
                //found
                originalSuccess = true;
                //get
                return select.attr("abs:href");
            }
        }
        //not found, fallback to larger version
        Elements select = document.select("a.original-file-changed[href]");
        return select.attr("abs:href");
    }

    public YandereProcedure(List<String> tags, int pageStart, int pageEnd, HtmlDownloader htmlDownloader) {
        super(tags, pageStart, pageEnd, htmlDownloader);
    }
}
