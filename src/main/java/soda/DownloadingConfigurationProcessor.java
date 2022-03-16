package soda;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class DownloadingConfigurationProcessor {

    //content
    private DownloadingConfiguration content = new DownloadingConfiguration();

    //get the content, can get set
    public DownloadingConfiguration getContent() {
        return content;
    }

    //尝试从默认路径读取json文件，若不存在，创建
    public void TryReadFromFile() {
        //default path
        final String filename = "." + File.separator + "configuration.json";
        //check file exist, if not, create it
        File file = new File(filename);
        try {
            if (!file.exists()) {
                //not exist, create
                file.createNewFile();
                //and write default value
                DownloadingConfiguration tempDefault = new DownloadingConfiguration();
                new ObjectMapper().writeValue(file, tempDefault);
                //set it to current configuration
                content = tempDefault;
                //work is done
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建文件失败");
            return;
        }
        //load content
        try {
            content = new ObjectMapper().readValue(file, DownloadingConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("加载失败");
            //use default
            content = new DownloadingConfiguration();
        }
    }
}


