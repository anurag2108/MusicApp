package com.example.musicapp.Fragments;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.musicapp.Adapters.MainAdapter;
import com.example.musicapp.MainActivity;
import com.example.musicapp.Model.Song;
import com.example.musicapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Feed extends Fragment {
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private List<Song> msongs;
    private static final String url= "https://storage.googleapis.com/uamp/catalog.json";

    private BottomSheetDialog bottomSheetDialog;
    private TextView song_name;
    private ImageView profile,play_button;
    private int position;
    private MediaPlayer mediaPlayer;
    private Button close;
    private Handler handler=new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        msongs=new ArrayList<>();

        readSongs();
        return view;
    }

    private void readSongs() {
        Toast.makeText(getContext(), "Give it a second!", Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "Long press to play the song", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array=jsonObject.getJSONArray("music");
                    for(int i=0;i<array.length();i++){
                        JSONObject o=array.getJSONObject(i);
                        Song item=new Song(o.getString("id"),o.getString("title")
                                ,o.getString("artist"),o.getString("source")
                                ,o.getString("image"),o.getString("duration"));
                        msongs.add(item);
                    }
                    mainAdapter=new MainAdapter(getContext(),msongs);
                    mainAdapter.onLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            position=recyclerView.getChildAdapterPosition(v);
                            startDialog();
                            return true;
                        }
                    });
                    recyclerView.setAdapter(mainAdapter);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    public void startDialog() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        mediaPlayer=new MediaPlayer();
        View view=getActivity().getLayoutInflater().from(getContext()).inflate(R.layout.layout_dialog,null);

        song_name=view.findViewById(R.id.song1);
        profile=view.findViewById(R.id.song_image);
        play_button=view.findViewById(R.id.songplay);

        if(position!=-1){
            song_name.setText(msongs.get(position).getTitle());
            Glide.with(getContext()).load(msongs.get(position).getImage()).into(profile);
        }
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
                bottomSheetDialog.dismiss();
            }
        });
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    Glide.with(getContext()).load(R.drawable.ic_baseline_play_circle_filled_24).into(play_button);
                }
                else{
                    mediaPlayer.start();
                    Glide.with(getContext()).load(R.drawable.ic_baseline_pause_circle_filled_24).into(play_button);

                }
            }
        });
        try{
            mediaPlayer.setDataSource(msongs.get(position).getSource());
            mediaPlayer.prepare();
        }catch (Exception exception){
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        //bottomSheetDialog.dismiss();
    }
}