package waifudownloader.procedure;

import waifudownloader.core.HtmlDownloader;

import java.util.List;

//抽象的、基础的下载流程
abstract public class BaseProcedure {

    //根据标签和页码，生成预览页的链接
    abstract public String generatePreviewPageUrl(int page);

    //根据预览页链接，下载预览页，然后获取每张图片详情页的链接
    //当返回的列表长度为0时，意味着可能到达末页后或无结果
    abstract public List<String> extractPostPageUrl(String previewPageUrl);

    //根据详情页的链接，下载详情页，然后根据设定获取每张图的有损大图或原图下载链接
    abstract public String extractDownloadUrl(String postPageUrl, boolean original);


    public BaseProcedure(List<String> tags, HtmlDownloader htmlDownloader) {
        this.tags = tags;
        this.htmlDownloader = htmlDownloader;
    }

    private List<String> tags;

    private HtmlDownloader htmlDownloader;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public HtmlDownloader getHtmlDownloader() {
        return htmlDownloader;
    }

    public void setHtmlDownloader(HtmlDownloader htmlDownloader) {
        this.htmlDownloader = htmlDownloader;
    }
}
