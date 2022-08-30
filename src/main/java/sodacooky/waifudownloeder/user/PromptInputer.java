package sodacooky.waifudownloeder.user;

import sodacooky.waifudownloeder.downloader.HtmlDownloader;
import sodacooky.waifudownloeder.procedure.IProcedure;
import sodacooky.waifudownloeder.procedure.ProcedureBuilder;
import sodacooky.waifudownloeder.procedure.ProcedureType;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * 用户交互输入，设置tags与page range，最后获得生成的Procedure
 */
public class PromptInputer {

    private final ProcedureBuilder procedureBuilder;
    private String dirNameForCurrentTask;//为当前任务创建合适的文件夹名称

    public PromptInputer(ProcedureType procedureType, HtmlDownloader htmlDownloader) {
        procedureBuilder = new ProcedureBuilder(procedureType);
        procedureBuilder.setHtmlDownloader(htmlDownloader);
        System.out.println("Now using: " + procedureType.toString());
    }

    public void inputTags() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Input tags separating by space:");
            String line = scanner.nextLine();
            String[] tagsArray = line.split(" ");
            //check procedure
            if (procedureBuilder.setTags(List.of(tagsArray))) {
                //make suitable dir name
                dirNameForCurrentTask = tagsArray[0] + new Random(Calendar.getInstance().getTimeInMillis()).nextInt(32767);
                break;
            } else {
                System.out.println("Unavailable tags amount!\n");
            }
        }
    }

    public void inputPageRange() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Input start page and end page separating by space:");
            int startPage = scanner.nextInt();
            int endPage = scanner.nextInt();
            //check procedure
            if (procedureBuilder.setPageRange(startPage, endPage)) {
                break;
            } else {
                System.out.println("Unavailable page range!\n");
            }
        }
    }

    public IProcedure buildProcedure() {
        return procedureBuilder.build();
    }

    public String getDirNameForCurrentTask() {
        return dirNameForCurrentTask;
    }

}
