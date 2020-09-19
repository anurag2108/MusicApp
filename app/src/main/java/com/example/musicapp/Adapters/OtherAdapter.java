package com.example.musicapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.Model.Song;
import com.example.musicapp.R;

import java.util.List;

public class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.ViewHolder> {
    private Context mcontext;
    private List<String> mSongs;
    public OtherAdapter(Context mcontext, List<String> mSongs) {
        this.mcontext = mcontext;
        this.mSongs = mSongs;
    }
    @NonNull
    @Override
    public OtherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.song_item,parent,false);
        return new OtherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String song=mSongs.get(position);
        holder.songname.setText(song);
        holder.profile_image.setImageResource(R.drawable.ic_baseline_music_note_24);
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView songname;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songname=itemView.findViewById(R.id.song_name);
            profile_image=itemView.findViewById(R.id.profile_image);
        }
    }

}
