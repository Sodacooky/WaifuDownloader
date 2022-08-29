package sodacooky.waifudownloeder.procedure;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProcedureYandere implements IProcedure {

    private ProcedureAsset procedureAsset;

    @Override
    public String getProcedureName() {
        return "Yandere";
    }

    @Override
    public int getTagsAmountUpperBound() {
        return 6;
    }

    @Override
    public int getPageRageUpperBound() {
        return Integer.MAX_VALUE;//no limit
    }

    @Override
    public void setAsset(ProcedureAsset procedureAsset) {
        this.procedureAsset = procedureAsset;
    }

    @Override
    public List<String> generatePreviewPageUrls() {
        //url header
        String header = "https://yande.re/post?page=";
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
        Document document = procedureAsset.getHtmlDownloader().download(postPageUrl);
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
}
