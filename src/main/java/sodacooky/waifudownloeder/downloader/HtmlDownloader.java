package sodacooky.waifudownloeder.downloader;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Proxy;

public class HtmlDownloader {

    //HTTP proxy
    private Proxy proxy = null;

    /**
     * 使用（可选）设定的代理，开始进行网页下载
     *
     * @param url 下载网页的URL
     * @return JSOUP Document
     */
    public Document download(String url) {
        Logger logger = LoggerFactory.getLogger(HtmlDownloader.class);
        try {
            //use jsoup connection helper
            Connection connection = HttpConnection.connect(url);
            connection.followRedirects(true);
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36 Edg/101.0.1210.39");
            //set connection proxy
            if (proxy != null) connection.proxy(proxy);
            //get document and return it
            return connection.get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
