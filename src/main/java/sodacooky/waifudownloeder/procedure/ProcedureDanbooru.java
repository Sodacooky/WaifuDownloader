package sodacooky.waifudownloeder.procedure;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProcedureDanbooru implements IProcedure {

    private ProcedureAsset procedureAsset;

    @Override
    public String getProcedureName() {
        return "Danbooru";
    }

    @Override
    public int getTagsAmountUpperBound() {
        return 2;
    }

    @Override
    public int getPageRageUpperBound() {
        return 1000;
    }

    @Override
    public void setAsset(ProcedureAsset procedureAsset) {
        this.procedureAsset = procedureAsset;
    }

    @Override
    public List<String> generatePreviewPageUrls() {
        //url header
        String header = "https://danbooru.donmai.us/posts?page=";
        //url tags arguments
        StringBuilder tagsArgs = new StringBuilder("&tags=");
        for (String tag : procedureAsset.getTags()) {
            tagsArgs.append(URLEncoder.encode(tag, StandardCharsets.UTF_8));
            tagsArgs.append("+");
        }
        //build urls
        List<String> result = new ArrayList<>();
        for (int now = procedureAsset.getStartPage(); now <= procedureAsset.getEndPage(); now++) {
            result.add(header + now + tagsArgs.toString());
        }
        //
        return result;
    }

    @Override
    public List<String> extractPostPageUrls(String previewPageUrl) {
        //download
        Document document = procedureAsset.getHtmlDownloader().download(previewPageUrl);
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
        Document document = procedureAsset.getHtmlDownloader().download(postPageUrl);
        //extract
        //only have original picture
        String downloadUrl = document.select("#post-option-download > a").attr("abs:href");
        //with download=1 param
        //remove the "download" param
        return downloadUrl.substring(0, downloadUrl.indexOf("?"));
    }
}
