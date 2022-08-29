package sodacooky.waifudownloeder;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sodacooky.waifudownloeder.downloader.FileDownloader;
import sodacooky.waifudownloeder.downloader.HtmlDownloader;
import sodacooky.waifudownloeder.procedure.IProcedure;
import sodacooky.waifudownloeder.user.PromptInputer;
import sodacooky.waifudownloeder.user.WDConfig;
import sodacooky.waifudownloeder.user.WDConfigFileReader;

import java.net.InetSocketAddress;
import java.net.Proxy;

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
        //todo
        //download picture to tag-associated directory
        //todo
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

}
