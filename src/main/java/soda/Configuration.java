package soda;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Configuration {

    //content
    ConfigurationContent content = new ConfigurationContent();

    public ConfigurationContent getContent() {
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
                ConfigurationContent tempDefault = new ConfigurationContent();
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
            content = new ObjectMapper().readValue(file, ConfigurationContent.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("加载失败");
            //use default
            content = new ConfigurationContent();
        }
    }
}

//配置的内容
class ConfigurationContent {

    private int urlFetchThreadAmount = 1;
    private int pictureDownloadThreadAmount = 1;
    private boolean enableProxy = false;
    private String proxyAddress = "127.0.0.1";
    private int proxyPort = 1080;

    public int getUrlFetchThreadAmount() {
        return urlFetchThreadAmount;
    }

    public void setUrlFetchThreadAmount(int urlFetchThreadAmount) {
        this.urlFetchThreadAmount = urlFetchThreadAmount;
    }

    public int getPictureDownloadThreadAmount() {
        return pictureDownloadThreadAmount;
    }

    public void setPictureDownloadThreadAmount(int pictureDownloadThreadAmount) {
        this.pictureDownloadThreadAmount = pictureDownloadThreadAmount;
    }

    public boolean isEnableProxy() {
        return enableProxy;
    }

    public void setEnableProxy(boolean enableProxy) {
        this.enableProxy = enableProxy;
    }

    public String getProxyAddress() {
        return proxyAddress;
    }

    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
