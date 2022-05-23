package waifudownloader.procedure;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import waifudownloader.core.HtmlDownloader;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DanbooruProcedure extends BaseProcedure {

    @Override
    public boolean isTagsAmountAvailable() {
        //2 tags limit
        return this.tags.size() <= 2;
    }

    @Override
    public boolean isPageRangeAvailable() {
        //limit 1000 page
        if (this.pageStart > 1000 || this.pageEnd > 1000) return false;
        return super.isPageRangeAvailable();
    }

    @Override
    public List<String> generatePreviewPageUrls() {
        //url header
        String header = "https://danbooru.donmai.us/posts?page=";
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
        Elements postPageElements = document.select("a.post-preview-link[href]");
        //transfer
        ArrayList<String> urls = new ArrayList<>();
        postPageElements.forEach(element -> urls.add(element.attr("abs:href")));
        return urls;
    }

    @Override
    public String extractDownloadUrl(String postPageUrl, boolean original) {
        //download
        Document document = this.htmlDownloader.download(postPageUrl);
        //extract
        //only have original picture
        String url = document.select("#post-option-download > a").attr("abs:href");
        return url;//with download=1 param
        //remove the "download" param
        //return url.substring(0, url.indexOf("?"));
    }

    public DanbooruProcedure(List<String> tags, int pageStart, int pageEnd, HtmlDownloader htmlDownloader) {
        super(tags, pageStart, pageEnd, htmlDownloader);
    }
}
