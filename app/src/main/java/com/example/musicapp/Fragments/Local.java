package com.example.musicapp.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicapp.Adapters.OtherAdapter;
import com.example.musicapp.PlayerActivity;
import com.example.musicapp.R;
import com.example.musicapp.SmartPlayerActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Local extends Fragment {
    private static final int MY_PERMISSSION_REQUEST=1;
    ArrayList<String> arrayList;
    OtherAdapter otherAdapter;
    RecyclerView recyclerView;

    ListView allMusicList;
    ArrayAdapter<String> musicArrayAdapter;
    String songs[];
    ArrayList<File> musics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_local, container, false);
        allMusicList=view.findViewById(R.id.listView);
        Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    musics=findMusicFiles(Environment.getExternalStorageDirectory());
                    songs=new String[musics.size()];
                    for(int i=0;i<musics.size();i++){
                        songs[i]=musics.get(i).getName();
                    }
                    musicArrayAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,songs);

                    allMusicList.setAdapter(musicArrayAdapter);

                    allMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String songName=allMusicList.getItemAtPosition(position).toString();
                            Intent intent=new Intent(getContext(),SmartPlayerActivity.class);
                            intent.putExtra("song",musics);
                            intent.putExtra("name",songName);
                            intent.putExtra("position",position);
                            startActivity(intent);
                        }
                    });
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

        return view;
    }

    private ArrayList<File> findMusicFiles(File file){
        ArrayList<File> musicfileobject=new ArrayList<>();
        File[] files=file.listFiles();
        for(File currentFiles:files){
            if(currentFiles.isDirectory() && !currentFiles.isHidden()){
                musicfileobject.addAll(findMusicFiles(currentFiles));
            } else{
                if(currentFiles.getName().endsWith(".mp3") || currentFiles.getName().endsWith(".mp4a") || currentFiles.getName().endsWith(".m4a") || currentFiles.getName().endsWith(".wav") || currentFiles.getName().endsWith(".aac")|| currentFiles.getName().endsWith(".wma")){
                    musicfileobject.add(currentFiles);
                }
            }

        }
        return musicfileobject;
    }
    public void doStuff(){
        arrayList=new ArrayList<>();
        getMusic();
        otherAdapter=new OtherAdapter(getContext(),arrayList);
        recyclerView.setAdapter(otherAdapter);
    }
    public void getMusic(){
        ContentResolver contentResolver= getActivity().getContentResolver();
        Uri songuri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor=contentResolver.query(songuri,null,null,null,null);

        if(cursor!=null && cursor.moveToFirst()){
            int songTitle=cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation=cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                String currentTitle=cursor.getString(songTitle);
                String currentArtist=cursor.getString(songArtist);
                String currentLocation=cursor.getString(songLocation);
                if(currentArtist.equals("") || currentArtist.equals("<unknown>")){
                    arrayList.add("Title:"+currentTitle);
                }
                else {
                    arrayList.add("Title:" + currentTitle + '\n' + "Artist:" + currentArtist);
                }

            } while(cursor.moveToNext());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSSION_REQUEST:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getContext(), "Permisson granted", Toast.LENGTH_SHORT).show();
                        doStuff();
                    }
                }
                else{
                    Toast.makeText(getContext(), "No permission granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}