package sodacooky.waifudownloeder.procedure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sodacooky.waifudownloeder.downloader.HtmlDownloader;

import java.util.List;

/**
 * 用于构建对应的流程，并设置参数同时校验
 */
public class ProcedureBuilder {

    //type of procedure
    private ProcedureType type;
    //tags of procedure
    private List<String> tags;
    //page range of procedure
    private int startPage, endPage;
    //the downloader
    private HtmlDownloader htmlDownloader;
    //temporary object and for parameters validation
    private IProcedure tempProcedure;


    /**
     * 构造需要指定目标下载站点
     *
     * @param procedureType 目标下载站点
     */
    public ProcedureBuilder(ProcedureType procedureType) {
        this.type = procedureType;
        switch (type) {
            case Danbooru:
                tempProcedure = new ProcedureDanbooru();
                break;
            case Yandere:
                tempProcedure = new ProcedureYandere();
                break;
        }
    }

    /**
     * 设置标签
     *
     * @param tags 标签
     * @return 返回false如果标签数量不符合限制
     */
    public boolean setTags(List<String> tags) {
        if (tags.size() > tempProcedure.getTagsAmountUpperBound()) return false;
        if (tags.size() == 0) return false;
        this.tags = tags;
        return true;
    }

    /**
     * 设置页范围
     *
     * @param startPage 起始页，包含
     * @param endPage   终止页，包含
     * @return 返回false如果超过了限制页数，或错误的数值
     */
    public boolean setPageRange(int startPage, int endPage) {
        if (startPage < 1) return false;
        if (endPage < startPage) return false;
        if (endPage > tempProcedure.getPageRageUpperBound()) return false;
        this.startPage = startPage;
        this.endPage = endPage;
        return true;
    }

    /**
     * 将设置好了代理设置的下载器传递给Procedure用于下载页面
     *
     * @param htmlDownloader 下载器
     */
    public void setHtmlDownloader(HtmlDownloader htmlDownloader) {
        this.htmlDownloader = htmlDownloader;
    }

    /**
     * 根据setXXX的参数获得构建的Procedure
     *
     * @return 结果Procedure
     */
    public IProcedure build() {
        Logger logger = LoggerFactory.getLogger(ProcedureBuilder.class);
        logger.warn("Procedure {} created.", tempProcedure.getProcedureName());
        logger.warn("From page {} to page {},totally {} page(s).", startPage, endPage, endPage - startPage + 1);
        logger.warn("Tags are: {}.", tags);
        tempProcedure.setAsset(new ProcedureAsset(htmlDownloader, tags, startPage, endPage));
        return tempProcedure;
    }
}
