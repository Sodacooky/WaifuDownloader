package sodacooky.waifudownloeder.user;

import sodacooky.waifudownloeder.procedure.ProcedureType;

/**
 * 配置文件配置内容，包括：
 * 下载源，yandere或danbooru等；
 * 提取下载链接线程数；
 * 下载线程数；
 * 代理设置；
 * 是否下载原图；
 * 等
 */
public class WDConfig {
    //下载源网站
    public ProcedureType downloadSource = ProcedureType.Yandere;
    //提取链接线程数
    public int urlFetchThreadAmount = 1;
    //下载线程数
    public int downloadThreadAmount = 1;
    //代理设置
    //启用代理
    public boolean proxyEnable = false;
    //代理地址，无需协议头如http://，仅支持http代理
    public String proxyAddress = "127.0.0.1";
    //代理端口
    public int proxyPort = 1080;
}
