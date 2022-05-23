package waifudownloader;

import waifudownloader.core.HtmlDownloader;
import waifudownloader.procedure.Procedure;
import waifudownloader.procedure.YandereProcedure;
import waifudownloader.threadworker.PostPageParallelUrlExtractor;
import waifudownloader.threadworker.UrlExtractor;

import java.util.List;

public class Application {
    public static void main(String[] args) {

        HtmlDownloader htmlDownloader = new HtmlDownloader();
        htmlDownloader.setHttpProxyPort(52338);
        htmlDownloader.setHttpProxyAddr("127.0.0.1");

        Procedure procedure = new YandereProcedure(List.of("hakurei_reimu"), 99, 99, htmlDownloader);
        UrlExtractor extractor = new PostPageParallelUrlExtractor(procedure);

        List<String> run = extractor.run();
        run.forEach(System.out::println);
    }
}
