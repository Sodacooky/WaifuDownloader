package sodacooky.waifudownloeder.downloader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 下载URL文件到本地工具
 */
public class FileDownloader {

    private Proxy proxy;
    private File saveDirectory;//目标保存路径
    private final Logger logger = LoggerFactory.getLogger(FileDownloader.class);
    private HttpClient httpClient = null;

    /**
     * 下载文件到制定目录
     *
     * @param url 下载的url
     */
    public void download(String url) {
        // -- lazy load http client -- //
        if (httpClient == null) {
            //使用Java自带的下载工具而不是Jsoup的
            HttpClient.Builder builder = HttpClient.newBuilder();
            builder.followRedirects(HttpClient.Redirect.ALWAYS);
            builder.connectTimeout(Duration.ofMinutes(4));
            //apply proxy
            if (proxy != null) builder.proxy(ProxySelector.of((InetSocketAddress) proxy.address()));
            //get client
            httpClient = builder.build();
        }
        try {
            //download bytes
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body());
            //transfer
            IOUtils.write(bufferedInputStream.readAllBytes(), new FileOutputStream(new File(saveDirectory, extractFilename(url))));
            //finish
        } catch (Exception e) {
            logger.error("\nFailed: {}", url);
            logger.error("ErrorMessage: {}", e.getMessage());
        }
    }

    public String extractFilename(String url) {
        String baseName = FilenameUtils.getBaseName(url);
        baseName = URLDecoder.decode(baseName, StandardCharsets.UTF_8);
        String extension = FilenameUtils.getExtension(url);
        //extension = extension.substring(0, extension.indexOf("?"));
        return baseName + "." + extension;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public File getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(File newSaveDirectory) {
        //不存在则创建
        if (newSaveDirectory.exists() && !newSaveDirectory.isDirectory()) {
            //存在但不是文件夹，要改名
            newSaveDirectory = new File(newSaveDirectory.getName() + "_dir");
            newSaveDirectory.mkdir();
        } else if (!newSaveDirectory.exists()) {
            //不存在，那么创建
            newSaveDirectory.mkdir();
        }
        //如果不是文件夹，则为同级目录
        if (!newSaveDirectory.isDirectory()) {
            if (newSaveDirectory.getParentFile().isDirectory()) {
                logger.warn("{} is not a directory!", newSaveDirectory.toString());
                //设置为同级目录
                saveDirectory = newSaveDirectory.getParentFile();
            }
        } else {
            saveDirectory = newSaveDirectory;
        }
        logger.warn("Saving files to directory: {}", saveDirectory.toString());
    }

}
