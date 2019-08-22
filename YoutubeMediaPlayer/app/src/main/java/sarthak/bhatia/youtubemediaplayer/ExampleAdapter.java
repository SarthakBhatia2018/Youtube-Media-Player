package sarthak.bhatia.youtubemediaplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    public List<Upload> mexamplelist;
    public OnItemClickListener mlistener;
    public Context mcontext;

    public ExampleAdapter(Context context, List<Upload> exampleitems) {
        mcontext = context;
        mexamplelist = exampleitems;
    }

    private String JSONparse(String url) {
        final String[] title = new String[1];
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jo = response.getJSONObject("snippet");
                            title[0] = jo.getString("title");
                            Log.d("Sarthak", title[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        return title[0];
    }

    public static String getYoutubeID(String youtubeUrl) {

        if (TextUtils.isEmpty(youtubeUrl)) {
            return "";
        }
        String video_id = "";

        String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
        CharSequence input = youtubeUrl;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            String groupIndex1 = matcher.group(7);
            if (groupIndex1 != null && groupIndex1.length() == 11)
                video_id = groupIndex1;
        }
        if (TextUtils.isEmpty(video_id)) {
            if (youtubeUrl.contains("youtu.be/")) {
                String spl = youtubeUrl.split("youtu.be/")[1];
                if (spl.contains("\\?")) {
                    video_id = spl.split("\\?")[0];
                } else {
                    video_id = spl;
                }

            }
        }

        return video_id;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.datacard, viewGroup, false);
        ExampleViewHolder evs = new ExampleViewHolder(v, mlistener);
        return evs;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder exampleViewHolder, final int i) {
        Upload currentItem = mexamplelist.get(i);
        String id = getYoutubeID(currentItem.getmVideoUrl());

        exampleViewHolder.mTitle.setText(JSONparse(currentItem.getmVideoUrl()));
        Picasso.with(mcontext)
                .load("https://img.youtube.com/vi/" + id + "/0.jpg")
                .resize(500, 400)
                .into(exampleViewHolder.mimage);
        //setImageResource();
//        Log.d("Sarthak",currentItem.getURL());
//        Log.d("Sarthak",getTitleQuietly(currentItem.getURL()));

//        exampleViewHolder.mTitle.setText("Title :" + i);
//        exampleViewHolder.mimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mcontext, YouTubePlayer.class);
//                intent.putExtra("URL_id", mexamplelist.get(i).getURL());
//                Log.d("Sarthak", mexamplelist.get(i).getURL());
//                mcontext.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mexamplelist.size();
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);

        void OnDeleteClick(int position);

        void openusingyoutube(int position);
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public ImageView mimage;
        public TextView mTitle;

        public ExampleViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mimage = itemView.findViewById(R.id.mimage);
            mTitle = itemView.findViewById(R.id.mTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnItemClick(position);
                        }
                    }
                }
            });
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//                    contextMenu.setHeaderTitle("Select Option");
                    MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Delete");
                    MenuItem openYoutube = contextMenu.add(Menu.NONE, 2, 2, "Open using Youtube");
                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (listener != null) {
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    listener.OnDeleteClick(position);
                                    return true;
                                }
                            }

                            return false;
                        }
                    });
                    openYoutube.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (listener != null) {
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    listener.openusingyoutube(position);
                                    return true;
                                }
                            }

                            return false;
                        }
                    });

                }
            });

        }
    }

}
