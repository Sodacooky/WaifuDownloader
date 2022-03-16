package soda;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PictureUrlsFetcher {

    private DownloadingConfiguration m_configuration;
    private String m_previewPageUrl;

    //给定要搜索的预览页链接，以及配置文件
    public PictureUrlsFetcher(String previewPageUrl, DownloadingConfiguration configuration) {
        m_previewPageUrl = previewPageUrl;
        m_configuration = configuration;
    }

    //返回图片链接
    public List<String> doFetch() {
        if (m_configuration.isDownloadOriginal()) {
            //try to get original picture
            return doFetchOriginal();
        } else {
            //get the lossy larger picture
            return doFetchLarger();
        }
    }

    private List<String> doFetchLarger() {
        return UrlExtractor.extractLargerUrls(Downloader.downloadHTML(m_previewPageUrl));
    }

    private List<String> doFetchOriginal() {
        //
        ArrayList<String> resultUrls = new ArrayList<>();
        //get preview page
        Document previewPageHtml = Downloader.downloadHTML(m_previewPageUrl);
        //get post pages
        List<String> postPageUrls = UrlExtractor.extractPostPage(previewPageHtml);
        //multithreading
        ExecutorService postPageDownloadService = Executors.newFixedThreadPool(m_configuration.getUrlFetchThreadAmount());
        for (String postPageUrl : postPageUrls) {
            postPageDownloadService.submit(() -> {
                Document postPageHtml = Downloader.downloadHTML(postPageUrl);
                String tempResult = UrlExtractor.extractOriginalFromPost(postPageHtml);
                if (tempResult.length() < 1) {
                    //original doesn't exist, get larger
                    tempResult = UrlExtractor.extractLargerFromPost(postPageHtml);
                }
                resultUrls.add(tempResult);
            });
        }
        postPageDownloadService.shutdown();
        try {
            postPageDownloadService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultUrls;
    }
}
