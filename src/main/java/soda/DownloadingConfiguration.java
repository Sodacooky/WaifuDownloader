package soda;

public class DownloadingConfiguration {

    private int urlFetchThreadAmount = 4;
    private int pictureDownloadThreadAmount = 2;
    private boolean enableProxy = false;
    private String proxyAddress = "127.0.0.1";
    private int proxyPort = 1080;
    private boolean downloadOriginal = false;

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

    public boolean isDownloadOriginal() {
        return downloadOriginal;
    }

    public void setDownloadOriginal(boolean downloadOriginal) {
        this.downloadOriginal = downloadOriginal;
    }
}
