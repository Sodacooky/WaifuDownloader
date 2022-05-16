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

//多线程获取下载链接的线程池
public class UrlExtractWorkerPool {

    public List<String> execute() {
//        没什么用，只是告诉你能够这样子实现可重入
//        if (executorService.isShutdown()) {
//            executorService = Executors.newFixedThreadPool(1);
//        }
        Logger logger = LoggerFactory.getLogger(UrlExtractWorkerPool.class);
        logger.warn("Start to fetch pictures' url, from page {} to page {},", pageStart, pageEnd);
        logger.warn("of tags {}.", procedure.getTags());
        //可复用worker
        UrlExtractWorker urlExtractWorker = new UrlExtractWorker(procedure, isDownloadOriginal);
        //future收集
        List<Future<List<String>>> futureContainer = new ArrayList<>();
        //添加任务
        for (int i = pageStart; i <= pageEnd; i++) {
            int pageNum = i;
            Future<List<String>> futureResult = executorService.submit(() -> urlExtractWorker.execute(pageNum));
            //收集future
            futureContainer.add(futureResult);
        }
        executorService.shutdown();
        //结果
        List<String> downloadUrls = new ArrayList<>();
        //等待任务
        for (Future<List<String>> future : futureContainer) {
            try {
                //wait and get
                List<String> urlPack = future.get();
                //transfer
                downloadUrls.addAll(urlPack);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        

        return downloadUrls;
    }


    public UrlExtractWorkerPool(BaseProcedure procedure, int pageStart, int pageEnd, boolean isDownloadOriginal, int threadAmount) {
        this.procedure = procedure;
        this.pageStart = pageStart;
        this.pageEnd = pageEnd;
        this.isDownloadOriginal = isDownloadOriginal;
        this.executorService = Executors.newFixedThreadPool(threadAmount);
    }

    //抽象的流程
    BaseProcedure procedure;
    //页码范围
    private int pageStart, pageEnd;
    //是否下载原图
    private boolean isDownloadOriginal;
    //多线程工具
    private ExecutorService executorService;
}
