package waifudownloader.threadworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waifudownloader.procedure.BaseProcedure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//以预览页为多线程基础的多线程下载链接提取器
public class PreviewPageParallelUrlExtractor {

    //main method
    public List<String> run() {
        Logger logger = LoggerFactory.getLogger(PreviewPageParallelUrlExtractor.class);
        //generate url
        logger.warn("Preparing searching urls...");
        List<String> previewPageUrls = procedure.generatePreviewPageUrls();
        //extract all download url
        logger.warn("Extracting downloading urls...");
        ExecutorService executorService = createTasks(previewPageUrls);
        waitAndRetrieve(executorService);
        return pictureUrls;
    }


    private ExecutorService createTasks(List<String> previewPageUrls) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //submit all
        while (!previewPageUrls.isEmpty()) {
            String nowPageUrl = previewPageUrls.get(0);
            previewPageUrls.remove(0);
            Future<List<String>> future = executorService.submit(() -> {
                //extract posts from preview page
                List<String> postPageUrls = procedure.extractPostPageUrls(nowPageUrl);
                //to each post, extract downloading url
                List<String> picUrls = new ArrayList<>();
                postPageUrls.forEach(post -> picUrls.add(procedure.extractDownloadUrl(post, true)));
                //
                return picUrls;
            });
            //collect future
            taskFuture.add(future);
        }
        //transfer the executorService to control waiting
        return executorService;
    }


    private void waitAndRetrieve(ExecutorService executorService) {
        //
        int totalTask = taskFuture.size();
        int finishedTask = 0;
        //所有任务都结束后自动释放线程
        executorService.shutdown();
        //遍历等待
        while (!taskFuture.isEmpty()) {
            try {
                //阻塞，然后获取
                List<String> picUrlsOfThatPage = taskFuture.get(0).get();
                taskFuture.remove(0);
                //transfer
                pictureUrls.addAll(picUrlsOfThatPage);
                //message
                finishedTask++;
                LoggerFactory.getLogger(PreviewPageParallelUrlExtractor.class)
                        .info("\t {}/{} Extracted {} urls.", finishedTask, totalTask, pictureUrls);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public PreviewPageParallelUrlExtractor(BaseProcedure procedure) {
        this.procedure = procedure;
    }

    //procedure
    private BaseProcedure procedure;
    //task future
    private List<Future<List<String>>> taskFuture = new ArrayList<>();
    //result picture url
    private List<String> pictureUrls = new ArrayList<>();
}
