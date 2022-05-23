package waifudownloader.threadworker;

import waifudownloader.procedure.Procedure;

import java.util.ArrayList;
import java.util.List;

public abstract class UrlExtractor {

    abstract public List<String> run();

    UrlExtractor(Procedure procedure) {
        this.procedure = procedure;
        this.pictureUrls = new ArrayList<>();
        //
        procedure.printDownloadInfo();
    }

    //procedure
    protected Procedure procedure;
    //result picture url
    protected List<String> pictureUrls;

}
