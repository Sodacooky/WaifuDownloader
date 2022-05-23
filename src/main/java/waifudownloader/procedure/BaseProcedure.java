package waifudownloader.procedure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waifudownloader.core.HtmlDownloader;

import java.util.List;

//抽象的、基础的下载流程
abstract public class BaseProcedure {

    //检测输入的标签数量是否满足指定网站的需求
    abstract public boolean isTagsAmountAvailable();

    //检测输入的页码范围是否满足指定网站的需求，并且是否倒置
    public boolean isPageRangeAvailable() {
        //default, range check
        return pageEnd >= pageStart;
    }

    //打印下载内容相关提示
    public void printDownloadInfo() {
        Logger logger = LoggerFactory.getLogger(BaseProcedure.class);
        logger.warn("It's going to download from page {} to {},", pageStart, pageEnd);
        logger.warn("totally {} pages,", pageEnd - pageStart + 1);
        logger.warn("of tags {}.", tags);
    }

    //根据标签与页码范围，生成所有预览页的链接
    abstract public List<String> generatePreviewPageUrls();

    //根据预览页链接，下载预览页，然后获取每张图片详情页的链接
    //当返回的列表长度为0时，意味着可能到达末页后或无结果
    abstract public List<String> extractPostPageUrls(String previewPageUrl);

    //根据详情页的链接，下载详情页，然后根据设定获取每张图的有损大图或原图下载链接
    abstract public String extractDownloadUrl(String postPageUrl, boolean original);

    protected List<String> tags;
    protected int pageStart, pageEnd;//[start,end]
    protected HtmlDownloader htmlDownloader;

    public BaseProcedure(List<String> tags, int pageStart, int pageEnd, HtmlDownloader htmlDownloader) {
        this.tags = tags;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.htmlDownloader = htmlDownloader;
    }
}
