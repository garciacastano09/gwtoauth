package jgc.asai.gwtoauth.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServerUtils {
    private static Map<String,String> parseQueryString(String qs)
    {
        String[] ps = qs.split("&");
        Map<String,String> map = new HashMap<String,String>();

        for (String p: ps )
        {
            String k = p.split("=")[0];
            String v = p.split("=")[1];
            map.put(k,v);
        }
        return map;
    }

    public static String getQueryStringValue(String qs,String name)
    {
        Map<String,String> map = parseQueryString(qs);
        return map.get(name);

    }
    public static String getQueryStringValueFromUrl(String urlString, String qsName) throws MalformedURLException
    {
        URL url = new URL(urlString);
        String qs = url.getQuery();
        return getQueryStringValue(qs,qsName);
    }

}
