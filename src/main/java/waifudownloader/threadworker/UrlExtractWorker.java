package waifudownloader.threadworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waifudownloader.procedure.BaseProcedure;

import java.util.ArrayList;
import java.util.List;

//从一个预览页下载详情页后，再获得下载链接
//兼容callable
public class UrlExtractWorker {

    //在当前的tags和original条件下，指定页码进行获取
    public List<String> execute(int page) {
        Logger logger = LoggerFactory.getLogger(UrlExtractWorker.class);
        //generate
        String previewPageUrl = procedure.generatePreviewPageUrl(page);
        //get all post
        List<String> postPageUrls = procedure.extractPostPageUrl(previewPageUrl);
        if (postPageUrls.size() == 0) {
            logger.error("There is no result in page {} of tags {}! Skipping.", page, procedure.getTags());
            return postPageUrls;//empty
        }
        //get all download url
        List<String> result = new ArrayList<>();
        postPageUrls.forEach(postPageUrl -> result.add(procedure.extractDownloadUrl(postPageUrl, original)));
        logger.info("Found {} pictures in page {}.", result.size(), page);
        //
        return result;
    }

    public UrlExtractWorker(BaseProcedure procedure, boolean original) {
        this.procedure = procedure;
        this.original = original;
    }

    //抽象的流程
    private final BaseProcedure procedure;
    //是否原图
    private final boolean original;


}
