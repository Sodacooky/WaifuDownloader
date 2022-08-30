package sodacooky.waifudownloeder;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sodacooky.waifudownloeder.downloader.FileDownloader;
import sodacooky.waifudownloeder.downloader.HtmlDownloader;
import sodacooky.waifudownloeder.procedure.IProcedure;
import sodacooky.waifudownloeder.user.PromptInputer;
import sodacooky.waifudownloeder.user.WDConfig;
import sodacooky.waifudownloeder.user.WDConfigFileReader;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class WaifuDownloaderApplication {

    private static final Logger logger = LoggerFactory.getLogger(WaifuDownloaderApplication.class);
    private static WDConfig config = null;
    private static HtmlDownloader htmlDownloader = null;
    private static FileDownloader fileDownloader = null;

    public static void main(String[] args) {
        //init
        readConfig();
        buildDownloader();
        //user input
        PromptInputer promptInputer = new PromptInputer(config.downloadSource, htmlDownloader);
        promptInputer.inputTags();
        promptInputer.inputPageRange();
        //get procedure
        IProcedure procedure = promptInputer.buildProcedure();
        //fetch download url
        List<String> downloadUrls = fetchAllDownloadUrl(procedure, fetchAllPostPageUrl(procedure));
        //download picture to tag-associated directory
        String dirNameForCurrentTask = promptInputer.getDirNameForCurrentTask();
        fileDownloader.setSaveDirectory(new File(dirNameForCurrentTask));
        downloadPictures(downloadUrls);
        //finished
        logger.warn("All downloading are finished.");
    }

    public static void readConfig() {
        if (WDConfigFileReader.isExist()) {
            config = WDConfigFileReader.read();
        } else {
            config = WDConfigFileReader.createDefault();
            logger.warn("Default config file created, edit it and run again!");
            System.exit(0);
        }
    }

    public static void buildDownloader() {
        htmlDownloader = new HtmlDownloader();
        fileDownloader = new FileDownloader();
        if (config.proxyEnable) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.proxyAddress, config.proxyPort));
            htmlDownloader.setProxy(proxy);
            fileDownloader.setProxy(proxy);
        }
    }

    public static List<String> fetchAllPostPageUrl(IProcedure procedure) {
        //生成所有预览页的链接
        logger.warn("Generating URLs of preview pages...");
        List<String> previewPageUrls = procedure.generatePreviewPageUrls();
        //并行访问所有预览页，提取详情页链接
        logger.warn("Fetching URLs from preview pages...");
        List<String> postPageUrls = new ArrayList<>();
        List<Future<?>> tasks = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(config.urlFetchThreadAmount);
        previewPageUrls.forEach(url -> {
            Future<?> submittedTask = executorService.submit(() -> {
                List<String> postPageUrlsInPage = procedure.extractPostPageUrls(url);
                postPageUrls.addAll(postPageUrlsInPage);
            });
            tasks.add(submittedTask);
        });
        //等待
        try {
            executorService.shutdown();
            boolean keepWaiting = true;
            while (keepWaiting) {
                //check tasks status
                if (executorService.awaitTermination(250, TimeUnit.MILLISECONDS)) {
                    //all finished
                    keepWaiting = false;
                }
                //remove finished task
                while (!tasks.isEmpty() && tasks.get(0).isDone()) {
                    tasks.remove(0);
                }
                //print status
                System.out.printf("\rProgress: %d/%d", previewPageUrls.size() - tasks.size(), previewPageUrls.size());
            }
            System.out.println(" ...Done!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //
        return postPageUrls;
    }

    public static List<String> fetchAllDownloadUrl(IProcedure procedure, List<String> postPageUrls) {
        List<String> downloadUrls = new ArrayList<>();
        //并行提取详情页
        logger.warn("Fetching downloading URLs from post pages...");
        ExecutorService executorService = Executors.newFixedThreadPool(config.urlFetchThreadAmount);
        List<Future<?>> tasks = new ArrayList<>();
        postPageUrls.forEach(url -> {
            Future<?> submittedTask = executorService.submit(() -> {
                downloadUrls.add(procedure.extractDownloadUrl(url, config.preferOriginal));
            });
            tasks.add(submittedTask);
        });
        //wait
        try {
            executorService.shutdown();
            boolean keepWaiting = true;
            while (keepWaiting) {
                //check tasks status
                if (executorService.awaitTermination(125, TimeUnit.MILLISECONDS)) {
                    //all finished
                    keepWaiting = false;
                }
                //remove finished task
                while (!tasks.isEmpty() && tasks.get(0).isDone()) {
                    tasks.remove(0);
                }
                //log status
                System.out.printf("\rProgress: %d/%d", postPageUrls.size() - tasks.size(), postPageUrls.size());
            }
            System.out.println(" ...Done!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //
        return downloadUrls;
    }

    public static void downloadPictures(List<String> downloadUrls) {
        //multithreading
        ExecutorService executorService = Executors.newFixedThreadPool(config.downloadThreadAmount);
        List<Future<?>> tasks = new ArrayList<>();
        for (String url : downloadUrls) {
            Future<?> submittedTask = executorService.submit(() -> {
                fileDownloader.download(url);
            });
            tasks.add(submittedTask);
        }
        //wait
        try {
            executorService.shutdown();
            boolean keepWaiting = true;
            while (keepWaiting) {
                //check tasks status
                if (executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    //all finished
                    keepWaiting = false;
                }
                //remove finished task
                while (!tasks.isEmpty() && tasks.get(0).isDone()) {
                    tasks.remove(0);
                }
                //log status
                System.out.printf("\rProgress: %d/%d", downloadUrls.size() - tasks.size(), downloadUrls.size());
            }
            System.out.println(" ...OHHHHHHHHH!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //
    }
}
