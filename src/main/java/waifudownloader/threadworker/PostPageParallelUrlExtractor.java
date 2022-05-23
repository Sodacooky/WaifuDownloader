package waifudownloader.threadworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waifudownloader.procedure.Procedure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//以详情页为基础的多线程链接提取
public class PostPageParallelUrlExtractor extends UrlExtractor {

    //main method
    public List<String> run() {
        Logger logger = LoggerFactory.getLogger(PostPageParallelUrlExtractor.class);
        //把每一预览页都加进”队列“，再对每一个预览页里的详情页链接进行多线程
        logger.warn("Preparing searching urls...");
        List<String> previewPageUrls = procedure.generatePreviewPageUrls();
        logger.warn("Extracting urls...");
        for (int finishedTask = 0; finishedTask != previewPageUrls.size(); finishedTask++) {
            //无需回收
            postPageParallelProcess(procedure.extractPostPageUrls(previewPageUrls.get(finishedTask)));
            //打印页完成消息
            logger.info("\t {}/{} Total {} urls.", finishedTask + 1, previewPageUrls.size(), pictureUrls.size());
        }
        //
        return pictureUrls;
    }

    private void postPageParallelProcess(List<String> postPageUrls) {
        //多线程，线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<String>> tasks = new ArrayList<>();
        //对每一个详情页
        postPageUrls.forEach(postPageUrl -> {
            Future<String> submit = executorService.submit(() -> {
                return procedure.extractDownloadUrl(postPageUrl, true);
            });
            tasks.add(submit);
        });
        //等待并回收下载链接
        executorService.shutdown();
        while (!tasks.isEmpty()) {
            try {
                //阻塞，然后获取
                String picUrlsOfThatPage = tasks.get(0).get();
                tasks.remove(0);
                //transfer
                pictureUrls.add(picUrlsOfThatPage);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public PostPageParallelUrlExtractor(Procedure procedure) {
        super(procedure);
    }
}
