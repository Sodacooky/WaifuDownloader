package sodacooky.waifudownloeder.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 用于读取和创建默认配置文件
 */
public class WDConfigFileReader {

    /**
     * 判断配置文件是否存在
     *
     * @return 是否存在
     */
    public static boolean isExist() {
        File configFile = new File("config.json");
        return configFile.exists();
    }

    /**
     * 从默认路径，即程序目录，读取config.json，并反序列化为WDConfig
     *
     * @return 读取到的config
     */
    public static WDConfig read() {
        File configFile = new File("config.json");
        //check existence
        if (!configFile.exists() || !configFile.isFile() || !configFile.canRead()) {
            return null;
        }
        //read it and convert to configuration instance
        ObjectMapper objectMapper = new ObjectMapper();
        WDConfig config = null;
        try {
            config = objectMapper.readValue(configFile, WDConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //print config content
        try {
            Logger logger = LoggerFactory.getLogger(WDConfigFileReader.class);
            logger.info("Successfully read the config:\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //
        return config;
    }

    /**
     * 在默认路径，即程序目录，创建默认的配置的config.json，并返回默认的WDConfig
     *
     * @return 返回默认的WDConfig
     */
    public static WDConfig createDefault() {
        WDConfig defaultConfig = new WDConfig();
        ObjectMapper objectMapper = new ObjectMapper();
        //write
        try {
            //try to create file
            File file = new File("config.json");
            if (file.exists()) throw new RuntimeException("There should not be file!");
            file.createNewFile();
            //warn
            LoggerFactory.getLogger(WDConfigFileReader.class)
                    .warn("Going to write default configuration:\n{}\n",
                            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(defaultConfig));
            //write default settings to file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(objectMapper.writeValueAsString(defaultConfig));//default
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return defaultConfig;
    }
}
