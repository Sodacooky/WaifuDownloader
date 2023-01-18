# WaifuDownloader

从贰刺螈图片爬虫网站上下载你的老婆！

目前支持：

* [Yande.re](https://yande.re/post)
* [Danbooru](https://danbooru.donmai.us/)

# 运行

运行需要JDK 11以上（这可是大势所趋！

Windows上的话，Java.com只能下到8版本，推荐从[这里](https://adoptium.net/)下载OpenJDK。

```
java -jar WaifuDownloader.jar
```

# 配置文件说明

初次运行后会生成默认配置文件，请修改后运行。

```json5
{
  //下载源，目前可选的有：Yandere, Danbooru
  //确保不要输入错误！
  "downloadSource": "Yandere",
  //下载网页并从其中提取出图片链接的线程数，建议4~8
  //如发现被网站BAN，请适当降低
  "urlFetchThreadAmount": 8,
  //下载图片的线程数，建议1~3
  //如发现被网站BAN，请适当降低
  "downloadThreadAmount": 2,
  //是否启用代理，设为true后，紧接的代理地址和端口才会生效
  "proxyEnable": true,
  //代理地址，只支持HTTP代理，但不要添加http://之类的头
  "proxyAddress": "127.0.0.1",
  //代理端口
  "proxyPort": 52338,
  //是否倾向于下载原图？
  //建议设置为false，因为原图和提供100%有损JPG相差不大，除非你需要！
  "preferOriginal": true
}
```

# 建议

如你所见，用Java写爬虫（准确来说其实是个下载器）十分难受，不如Python。

上一次是从C++跳到Java呢。