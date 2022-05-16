package waifudownloader.procedure;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import waifudownloader.core.HtmlDownloader;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class YandereProcedure extends BaseProcedure {

    @Override
    public String generatePreviewPageUrl(int page) {
        //url header
        StringBuilder stringBuilder = new StringBuilder("https://yande.re/post?page=");
        //page arg
        stringBuilder.append(page);
        //tags header
        stringBuilder.append("&tags=");
        //tags content
        for (String tag : super.getTags()) {
            stringBuilder.append(URLEncoder.encode(tag, StandardCharsets.UTF_8));
            stringBuilder.append("+");
        }
        //
        return stringBuilder.toString();
    }

    @Override
    public List<String> extractPostPageUrl(String previewPageUrl) {
        //download
        Document document = super.getHtmlDownloader().download(previewPageUrl);
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
        Document document = super.getHtmlDownloader().download(postPageUrl);
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


    public YandereProcedure(List<String> tags, HtmlDownloader htmlDownloader) {
        super(tags, htmlDownloader);
    }
}
