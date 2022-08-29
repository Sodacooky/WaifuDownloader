package sodacooky.waifudownloeder.procedure;

import java.util.List;

/**
 * 抽象的下载流程
 */
public interface IProcedure {

    /**
     * @return 具体实现的名称，如"Danbooru"，"yandere"
     */
    String getProcedureName();

    /**
     * @return 标签数量上限
     */
    int getTagsAmountUpperBound();

    /**
     * @return 末页上限（只能查看到多少页，如Danbooru的1000页
     */
    int getPageRageUpperBound();

    /**
     * 设置属性，包含tags、页范围、下载工具
     *
     * @param procedureAsset 属性
     */
    void setAsset(ProcedureAsset procedureAsset);

    /**
     * 根据标签与页码范围，生成所有预览页的链接
     *
     * @return 生成的预览页链接列表
     */
    List<String> generatePreviewPageUrls();

    /**
     * 根据预览页链接，下载预览页，然后获取每张图片详情页的链接
     *
     * @param previewPageUrl 预览页链接
     * @return 图片详情页链接列表。当返回的列表长度为0时，意味着可能到达末页后或无结果。
     */
    List<String> extractPostPageUrls(String previewPageUrl);

    /**
     * 根据详情页的链接，下载详情页，然后根据设定获取每张图的有损大图或原图下载链接
     *
     * @param postPageUrl 详情页链接
     * @param original    是否下载原图
     * @return 图片下载链接
     */
    String extractDownloadUrl(String postPageUrl, boolean original);


}
