package sarthak.bhatia.youtubemediaplayer;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;

public class SimpleYouTubeHelper {
    public static String getTitleQuietly(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                        youtubeUrl + "&format=json"
                );

                return new JSONObject(IOUtils.toString(embededURL)).getString("title");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
