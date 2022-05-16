package waifudownloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import waifudownloader.core.HtmlDownloader;
import waifudownloader.procedure.YandereProcedure;
import waifudownloader.threadworker.UrlExtractWorkerPool;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Application.class);

        HtmlDownloader htmlDownloader = new HtmlDownloader();
        htmlDownloader.setHttpProxyAddr("127.0.0.1");
        htmlDownloader.setHttpProxyPort(52338);

        YandereProcedure yandereProcedure = new YandereProcedure(List.of("hakurei_reimu"), htmlDownloader);

        UrlExtractWorkerPool urlExtractWorkerPool = new UrlExtractWorkerPool(yandereProcedure, 1, 5, true, 2);
        List<String> result = urlExtractWorkerPool.execute();

        result.forEach(System.out::println);
    }
}
