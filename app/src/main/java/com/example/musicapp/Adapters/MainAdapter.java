package com.example.musicapp.Adapters;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.Model.Song;
import com.example.musicapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> implements View.OnLongClickListener{
    private Context mcontext;
    private List<Song> mSongs;
    private List<String> mdownloaded=new ArrayList<>();
    private long id;
    View.OnLongClickListener onLongClickListener;

    public MainAdapter(Context mcontext, List<Song> mSongs) {
        this.mcontext = mcontext;
        this.mSongs = mSongs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
        view.setOnLongClickListener(this);
        return new MainAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Song song=mSongs.get(position);
        holder.songname.setText(song.getTitle());
        Glide.with(mcontext).load(song.getImage()).into(holder.profile_image);

        holder.download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=song.getSource();
                final String fileName=song.getTitle();
                downloadFile(fileName,url);
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(mcontext);
                alertDialog.setTitle("Alert!");
                alertDialog.setMessage("Do you want to Stop the download?");
                alertDialog.setIcon(R.drawable.ic_baseline_cloud_download_24);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadManager manager=(DownloadManager) mcontext.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.remove(id);
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mdownloaded.add(song.getTitle());
                                holder.download_button.setImageResource(R.drawable.ic_baseline_check_24);
                            }
                });
                alertDialog.show();
            }
        });
    }

    private void downloadFile(String fileName, String url) {

        Uri downloadURL=Uri.parse(url);
        DownloadManager manager=(DownloadManager) mcontext.getSystemService(Context.DOWNLOAD_SERVICE);
        try{
            if(manager!=null){
                DownloadManager.Request request=new DownloadManager.Request(downloadURL);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                        .setTitle(fileName)
                        .setDescription("Downloading..."+fileName)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName)
                        .setMimeType(getMImeType(downloadURL));
                id=manager.enqueue(request);
                Toast.makeText(mcontext, "Download Started!", Toast.LENGTH_SHORT).show();

            }else{
                Intent intent=new Intent(Intent.ACTION_VIEW,downloadURL);
                mcontext.startActivity(intent);
            }

        }catch (Exception e){
            Toast.makeText(mcontext, "Something went Wrong", Toast.LENGTH_SHORT).show();
            Log.e("ERROR:MAIN",e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public boolean onLongClickListener(View.OnLongClickListener onLongClickListener){
        this.onLongClickListener=onLongClickListener;
        return true;
    }


    @Override
    public boolean onLongClick(View v) {
        onLongClickListener.onLongClick(v);
        return false;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView songname;
        public ImageView profile_image,download_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songname=itemView.findViewById(R.id.song_name);
            profile_image=itemView.findViewById(R.id.profile_image);
            download_button=itemView.findViewById(R.id.download_button);
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private String getMImeType(Uri uri){
        ContentResolver resolver=mcontext.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

}
