package soda;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        //multithreading
        final int[] downloadedCount = {0};
        ExecutorService pictureDownloadService = Executors.newFixedThreadPool(8);
        for (String url : urls) {
            pictureDownloadService.submit(() -> {
                Downloader.downloadToFile(url);
                //progress
                synchronized (downloadedCount) {
                    downloadedCount[0]++;
                    for (int i = 0; i != 32; i++) System.out.print("\b");
                    System.out.printf("%d / %d", downloadedCount[0], urls.size());
                }
            });
        }
        //wait
        pictureDownloadService.shutdown();
        try {
            pictureDownloadService.awaitTermination(99, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //finished
        System.out.println();
        System.out.println("图片下载结束");
    }

    private static List<String> fetchAllPictureUrls(int startPage, int endPage, List<String> tags) {
        //get preview page url
        List<String> pageUrls = PreviewPageUrlGenerator.generate(startPage, endPage, tags);
        //result
        List<String> downloadUrls = new ArrayList<>();
        //get preview page content
        //multithreading
        final Boolean[] keepDownloading = {true};
        final int[] downloadedPage = {0};
        System.out.println("开始获取图片链接");
        ExecutorService pageDownloadService = Executors.newFixedThreadPool(32);
        for (String pageUrl : pageUrls) {
            //当到达了没有图片的页时，尽快结束
            if (!keepDownloading[0]) break;
            //task
            pageDownloadService.submit(new Runnable() {
                @Override
                public void run() {
                    List<String> urls = DownloadUrlExtractor.extract(Downloader.downloadHTML(pageUrl));
                    if (urls.size() == 0) keepDownloading[0] = false;
                    else {
                        downloadUrls.addAll(urls);
                        //progress
                        synchronized (downloadedPage) {
                            downloadedPage[0]++;
                            for (int i = 0; i != 32; i++) System.out.print("\b");
                            System.out.printf("%d / %d", downloadedPage[0], endPage - startPage + 1);
                        }
                    }
                }
            });
        }
        //wait
        pageDownloadService.shutdown();
        try {
            pageDownloadService.awaitTermination(1, TimeUnit.DAYS);
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
