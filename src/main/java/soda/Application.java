package soda;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Application {

    public static void main(String[] args) {
        //args processing
        argsProcessing(Arrays.asList(args));

        //user prompt input
        List<String> tags = userInputTags();
        List<Integer> pageRange = userInputPageRange();
        int startPage = pageRange.get(0);
        int endPage = pageRange.get(1);

        //check tags
        if (tags.size() == 0) {
            System.out.println("以外的0个tags输入");
            System.exit(-1);
        }

        //get picture urls
        System.out.println("正在获取从页" + startPage + "到页" + endPage + "共" + (endPage - startPage + 1));
        List<String> urls = fetchAllPictureUrls(startPage, endPage, tags);

        //create directory
        Downloader.setSaveDirectory("." + File.separator + tags.get(0));
        File dir = new File(Downloader.getSaveDirectory());
        if (!dir.exists()) dir.mkdir();

        //download pictures
        downloadAllPicture(urls);
    }

    private static void argsProcessing(List<String> args) {
        if (args.contains("--help")) {
            System.out.println("--enableProxy 启用代理，默认127.0.0.1:1080");
            System.out.println("--proxyAddr <addr> 设置代理地址，addr地址无需协议前缀切只支持http代理");
            System.out.println("--proxyPort <port> 设置代理的端口");
            System.exit(0);
        }
        if (args.contains("--enableProxy")) Downloader.setEnableProxy(true);
        if (args.contains("--proxyAddr")) {
            int index = args.indexOf("--proxyAddr");
            Downloader.setProxyAddress(args.get(index + 1));
        }
        if (args.contains("--proxyPort")) {
            int index = args.indexOf("--proxyPort");
            Downloader.setProxyPort(Integer.parseInt(args.get(index + 1)));
        }
    }

    private static void downloadAllPicture(List<String> urls) {
        System.out.println("开始下载图片");

        //thread pool
        ExecutorService pictureDownloadService = Executors.newFixedThreadPool(4);
        for (String url : urls) {
            //submit task
            pictureDownloadService.submit(() -> {
                //download
                if (!Downloader.downloadToFile(url)) {
                    System.out.println("失败 " + url);
                }
            });
        }

        //wait
        pictureDownloadService.shutdown();
        try {
            boolean keepWaiting = true;
            while (keepWaiting) {
                Thread.sleep(1000);
                if (pictureDownloadService.isTerminated()) keepWaiting = false;
                for (int i = 0; i != 32; i++) System.out.print('\b');
                System.out.printf("%d / %d", ((ThreadPoolExecutor) pictureDownloadService).getCompletedTaskCount(), urls.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //finished
        System.out.println();
        System.out.println("图片下载结束");
    }

    private static List<String> fetchAllPictureUrls(int startPage, int endPage, List<String> tags) {
        System.out.println("开始获取图片链接");

        //get preview page url
        List<String> pageUrls = PreviewPageUrlGenerator.generate(startPage, endPage, tags);
        //result urls
        List<String> downloadUrls = new ArrayList<>();

        //downloading control
        final boolean[] keepDownloading = {true};
        //thread pool
        ExecutorService pageDownloadService = Executors.newFixedThreadPool(16);
        for (String url : pageUrls) {
            //task submit
            pageDownloadService.submit(() -> {
                //try control
                synchronized (keepDownloading) {
                    if (!keepDownloading[0]) return;
                }
                //fetch
                List<String> urls = DownloadUrlExtractor.extract(Downloader.downloadHTML(url));
                //check whether it should keep downloading
                synchronized (keepDownloading) {
                    //if no url exists, reached the end
                    if (urls.size() == 0) {
                        keepDownloading[0] = false;
                        return;
                    }
                    //or append fetched urls
                    downloadUrls.addAll(urls);
                }
            });
        }
        //wait
        pageDownloadService.shutdown();
        try {
            boolean keepWaiting = true;
            while (keepWaiting) {
                Thread.sleep(250);
                if (pageDownloadService.isTerminated()) keepWaiting = false;
                for (int i = 0; i != 32; i++) System.out.print('\b');
                System.out.printf("%d / %d", ((ThreadPoolExecutor) pageDownloadService).getCompletedTaskCount(), pageUrls.size());
            }
            for (int i = 0; i != 32; i++) System.out.print('\b');
            System.out.printf("成功下载 %d 页的图片链接\n", (int) Math.ceil(downloadUrls.size() / 40.));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //finished
        System.out.println();
        System.out.println("图片链接获取完成");
        return downloadUrls;
    }

    private static List<String> userInputTags() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入tags，以空格间隔，如：hakurei_reimu miko");
        String userinput = scanner.nextLine();
        return Arrays.asList(userinput.split(" "));
    }

    private static List<Integer> userInputPageRange() {
        List<Integer> result = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("输入页面范围<开始页> <结束页>，如： 1 999");
            int start = scanner.nextInt();
            int end = scanner.nextInt();
            if (end < start || start <= 0) {
                System.out.println("不正确的输入");
            } else {
                result.add(start);
                result.add(end);
                break;
            }
        }
        scanner.close();
        return result;
    }

}
