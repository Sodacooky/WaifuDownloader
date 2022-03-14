package soda;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;

public class Downloader {

    public static String getProxyAddress() {
        return proxyAddress;
    }

    //不需要协议前缀，直接写上"127.0.0.1"或"www.my-proxy.com"
    //HTTP only
    public static void setProxyAddress(String proxyAddress) {
        Downloader.proxyAddress = proxyAddress;
    }

    public static int getProxyPort() {
        return proxyPort;
    }

    public static void setProxyPort(int proxyPort) {
        Downloader.proxyPort = proxyPort;
    }

    public static boolean isEnableProxy() {
        return enableProxy;
    }

    public static void setEnableProxy(boolean enableProxy) {
        Downloader.enableProxy = enableProxy;
    }

    private static String proxyAddress = "127.0.0.1";
    private static int proxyPort = 1080;
    private static boolean enableProxy = false;

    public static String getSaveDirectory() {
        return saveDirectory;
    }

    //以separator杠结尾
    public static void setSaveDirectory(String saveDirectory) {
        Downloader.saveDirectory = saveDirectory;
        if (!Downloader.saveDirectory.endsWith(File.separator)) {
            Downloader.saveDirectory += File.separator;
        }
    }

    private static String saveDirectory = "." + File.separator;

    public static Document downloadHTML(String url) {
        //create connection
        Connection connect = Jsoup.connect(url);
        //set proxy
        if (enableProxy) connect.proxy(proxyAddress, proxyPort);
        //get content
        try {
            return connect.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean downloadToFile(String url) {
        //create connection
        Connection connect = Jsoup.connect(url);
        //get
        try {
            URL urlObject = new URL(url);
            //set proxy, get stream
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, proxyPort));
            InputStream inputStream = urlObject.openConnection(proxy).getInputStream();
            //make full filename
            String fullPath = saveDirectory
                    + FilenameUtils
                    .getName(URLDecoder.decode(urlObject.getPath(), "UTF-8"))
                    .replaceAll("[\\\\/:*\"!?<>|]", " ");
            //save
            FileOutputStream fileOutputStream = new FileOutputStream(fullPath);
            IOUtils.copy(inputStream, fileOutputStream);
            fileOutputStream.close();
            inputStream.close();
            //
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
