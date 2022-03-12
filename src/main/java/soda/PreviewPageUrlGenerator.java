package soda;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PreviewPageUrlGenerator {

    public static List<String> generate(int startPage, int endPage, List<String> tags) {
        final String urlHeader = "https://yande.re/post?page=";
        String urlTagsArgument = makeTagsArgument(tags);
        ArrayList<String> urls = new ArrayList<String>();
        for (int now = startPage; now <= endPage; now++) {
            urls.add(urlHeader + now + urlTagsArgument);
        }
        return urls;
    }

    //encode and make
    //return "&tags=xxx+xxx+..."
    private static String makeTagsArgument(List<String> tags) {
        StringBuilder stringBuilder = new StringBuilder("&tags=");
        for (String tag : tags) {
            try {
                stringBuilder.append(URLEncoder.encode(tag, "UTF-8"));
                stringBuilder.append("+");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
