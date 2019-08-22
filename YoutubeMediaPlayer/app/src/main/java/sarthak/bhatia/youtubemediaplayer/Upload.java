package sarthak.bhatia.youtubemediaplayer;

import com.google.firebase.database.Exclude;

public class Upload {
    //        private String mName;
    private String mVideoUrl;
    private String mKey;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String VideoUrl) {
//            if (name.trim().equals("")) {
//                name = "No Name";
//            }

//            mName = name;
        mVideoUrl = VideoUrl;
    }

//        public String getName() {
//            return mName;
//        }

    public String getmVideoUrl() {
        return mVideoUrl;
    }

//    public void setName(String name) {
//        mName = name;
//    }

    public void setmVideoUrl(String imageUrl) {
        mVideoUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }



}
