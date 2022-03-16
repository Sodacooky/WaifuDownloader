# WaifuDownloader

图片下载器，目前仅支持yande.re

## 使用

```
java -jar WaifuDownloader.jar
```

## 配置文件说明

首次使用会在运行目录创建配置文件，如果你需要特殊的功能，请停止后更改

```json5
{
  //模仿浏览器获取图片下载链接的线程数，建议4-8
  "urlFetchThreadAmount": 4,
  //图片下载的线程数，建议1-4
  "pictureDownloadThreadAmount": 2,
  //是否启用代理
  "enableProxy": true,
  //HTTP代理服务器的地址，不需要写http://的头，只支持http
  "proxyAddress": "127.0.0.1",
  //HTTP代理服务器的端口
  "proxyPort": 10809,
  //是否优先下载原图，原图比大图大几倍，但大图并不会糊多少，而且会增加获取链接的时间
  "downloadOriginal": true
}
```
