package sarthak.bhatia.youtubemediaplayer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Context context = this;
    private List<Upload> mExaplelist;
    private RecyclerView mrecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mlayoutmanager;
    private ImageView Add_button;
    private EditText URL_text;
    private String id;
    private DatabaseReference mDatabaseRef;
    private RequestQueue mQueue;

    public static boolean isYoutubeUrl(String youTubeURl) {
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (!youTubeURl.isEmpty() && youTubeURl.matches(pattern)) {
            success = true;
        } else {
            // Not Valid youtube URL
            success = false;
        }
        return success;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("URL's");
        mExaplelist = new ArrayList<>();
        buildRecyclerView();
        mAdapter = new ExampleAdapter(this, mExaplelist);
        mrecyclerView.setAdapter(mAdapter);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mExaplelist.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mExaplelist.add(upload);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        setbuttons();

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, sarthak.bhatia.youtubemediaplayer.YoutubePlayer.class);
                id = mExaplelist.get(position).getmVideoUrl();
                Log.d("Sarthak", mExaplelist.get(position).getKey());
                intent.putExtra("URL_id", id);
                context.startActivity(intent);
            }

            @Override
            public void OnDeleteClick(int position) {
                Upload selecteditem = mExaplelist.get(position);
                String selectedkey = selecteditem.getKey();
                mDatabaseRef.child(selectedkey).removeValue();
                Toast.makeText(MainActivity.this, "Deleted " + selectedkey, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void openusingyoutube(int position) {
                id = getYoutubeID(mExaplelist.get(position).getmVideoUrl());
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + id));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }


        });
    }

    private void setbuttons() {
        Add_button = findViewById(R.id.ADD);
        URL_text = findViewById(R.id.URL);
//        mQueue = VolleySingleton.getInstance(this).getRequestQueue();
        Add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadURL(URL_text.getText().toString());
                URL_text.setText("");
                ;
            }
        });
    }

    private void UploadURL(String url) {
        if (url.trim().matches("")) {
            Toast.makeText(this, "Enter a valid URL", Toast.LENGTH_SHORT);
        } else {
            Upload upload = new Upload(url);
            String uploadID = mDatabaseRef.push().getKey();
            mDatabaseRef.child(uploadID).setValue(upload);
        }

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


    public void InsertItem(String url) {
        if (url.matches("")) {
            Toast.makeText(
                    this, "Enter a valid Youtube URL", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isYoutubeUrl(url)) {
            Toast.makeText(this, "Enter a valid Youtube URL", Toast.LENGTH_SHORT).show();
            return;
        } else {
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Added URL", Toast.LENGTH_SHORT).show();
        }
    }

    private void buildRecyclerView() {
        mrecyclerView = findViewById(R.id.recyclerview);
        mrecyclerView.setHasFixedSize(true);
        mlayoutmanager = new LinearLayoutManager(this);
        mrecyclerView.setLayoutManager(mlayoutmanager);
    }

}




