package edu.na1.assignment.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class StringUtils {


    public static Boolean hasText(String s) {
        if (s != null
                && s.trim().length() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Map<String, String> getResultMap(String results) {
        StringTokenizer stringTokenizer = new StringTokenizer(results, "#");
        Map<String, String> resultMap = new HashMap<>();

        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            StringTokenizer resultTokenizer = new StringTokenizer(token, "=");

            String key = null;
            String value = null;

            if (resultTokenizer.hasMoreTokens()) {
                key = resultTokenizer.nextToken();
            }

            if (resultTokenizer.hasMoreTokens()) {
                value = resultTokenizer.nextToken();
            }

            resultMap.put(key, value);
        }

        return resultMap;  //To change body of created methods use File | Settings | File Templates.
    }

    public static String getFileName(String request) {
        if (request.contains(Constants.QUERY)) {
            int tokenIndex = request.indexOf(Constants.RESULTS_TOKEN);
            return request.substring(tokenIndex+1, request.length());
        }
        return "";
    }
}
