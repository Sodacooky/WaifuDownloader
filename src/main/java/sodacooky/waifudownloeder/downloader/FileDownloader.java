package sodacooky.waifudownloeder.downloader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Proxy;

/**
 * 下载URL文件到本地工具
 */
public class FileDownloader {

    private Proxy proxy;
    private File saveDirectory;//目标保存路径


    /**
     * @return 下载的文件的大小
     */
    public long download(String url) {
        Logger logger = LoggerFactory.getLogger(FileDownloader.class);
        try {
            //use jsoup build the connection
            Connection connection = HttpConnection.connect(url);
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36 Edg/101.0.1210.39")
                    .ignoreContentType(true)
                    .maxBodySize(0);
            //set connection proxy
            if (proxy != null) connection.proxy(proxy);
            //try to remove url params from some websites
            String baseName = FilenameUtils.getBaseName(url);
            String extension = FilenameUtils.getExtension(url);
            extension = extension.substring(0, extension.indexOf("?"));
            String fullPath = baseName + "." + extension;
            //create file
            File toSaveFile = new File(saveDirectory, fullPath.replaceAll("[\\\\/:*\"!?<>|]", " "));
            if (!toSaveFile.createNewFile()) {
                logger.warn("Covering a existing file: " + toSaveFile.getName());
            }
            //do download
            FileOutputStream fileOutputStream = new FileOutputStream(toSaveFile);
            connection.execute();
            IOUtils.write(connection.execute().bodyAsBytes(), fileOutputStream);
            fileOutputStream.close();
            //report file size
            return toSaveFile.length();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
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

    public void setSaveDirectory(File saveDirectory) {
        Logger logger = LoggerFactory.getLogger(FileDownloader.class);
        //不存在则创建
        if (saveDirectory.exists() && !saveDirectory.isDirectory()) {
            //存在但不是文件夹，要改名
            saveDirectory = new File(saveDirectory.getName() + "_dir");
            saveDirectory.mkdir();
        } else if (!saveDirectory.exists()) {
            //不存在，那么创建
            saveDirectory.mkdir();
        }
        //如果不是文件夹，则为同级目录
        if (!saveDirectory.isDirectory()) {
            if (saveDirectory.getParentFile().isDirectory()) {
                logger.warn(saveDirectory.toString() + " is not a directory!");
                //设置为同级目录
                this.saveDirectory = saveDirectory.getParentFile();
            }
        } else {
            this.saveDirectory = saveDirectory;
        }
        logger.info("saving file to " + this.saveDirectory.toString());
    }

}
