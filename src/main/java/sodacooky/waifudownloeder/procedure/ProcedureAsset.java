package sodacooky.waifudownloeder.procedure;

import sodacooky.waifudownloeder.downloader.HtmlDownloader;

import java.util.List;

/**
 * Procedure所使用的一些变量
 */
public class ProcedureAsset {
    private HtmlDownloader htmlDownloader;
    private List<String> tags;
    private int startPage, endPage;

    public ProcedureAsset(HtmlDownloader htmlDownloader, List<String> tags, int startPage, int endPage) {
        this.htmlDownloader = htmlDownloader;
        this.tags = tags;
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public ProcedureAsset() {
    }

    public HtmlDownloader getHtmlDownloader() {
        return htmlDownloader;
    }

    public void setHtmlDownloader(HtmlDownloader htmlDownloader) {
        this.htmlDownloader = htmlDownloader;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }
}
