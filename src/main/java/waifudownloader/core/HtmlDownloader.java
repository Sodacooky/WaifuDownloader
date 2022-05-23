package waifudownloader.core;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//下载网页HTML的工具类，也可以转为JSOUP
public class HtmlDownloader {

    //使用（可选）设定的代理，开始进行网页下载
    public Document download(String url) {
        Logger logger = LoggerFactory.getLogger(HtmlDownloader.class);
        try {
            //use jsoup connection helper
            Connection connection = HttpConnection.connect(url);
            connection.followRedirects(true);
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36 Edg/101.0.1210.39");
            //set connection proxy
            if (httpProxyAddr != null && httpProxyAddr.length() > 1) {
                if (httpProxyPort != 0) {
                    connection.proxy(httpProxyAddr, httpProxyPort);
                }
            }
            //get document and return it
            return connection.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HtmlDownloader(String httpProxyAddr, int httpProxyPort) {
        this.httpProxyAddr = httpProxyAddr;
        this.httpProxyPort = httpProxyPort;
    }

    public HtmlDownloader() {
    }

    public String getHttpProxyAddr() {
        return httpProxyAddr;
    }

    /**
     * 不需要协议头！
     *
     * @param httpProxyAddr 如127.0.0.1的地址
     */
    public void setHttpProxyAddr(String httpProxyAddr) {
        //如果有协议头，那么去掉
        if (httpProxyAddr.contains("://")) {
            this.httpProxyAddr = httpProxyAddr.substring(httpProxyAddr.indexOf("://") + "://".length());
        } else {
            this.httpProxyAddr = httpProxyAddr;
        }
    }

    public int getHttpProxyPort() {
        return httpProxyPort;
    }

    public void setHttpProxyPort(int httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
    }

    private String httpProxyAddr;
    private int httpProxyPort;

}
