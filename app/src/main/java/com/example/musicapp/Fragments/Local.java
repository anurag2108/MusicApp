package com.example.musicapp.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicapp.Adapters.OtherAdapter;
import com.example.musicapp.R;

import java.util.ArrayList;

public class Local extends Fragment {
    private static final int MY_PERMISSSION_REQUEST=1;
    ArrayList<String> arrayList;
    OtherAdapter otherAdapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_local, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(ContextCompat.checkSelfPermission(getContext()
                , Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity()
                    ,Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSSION_REQUEST);
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSSION_REQUEST);
            }

        }
        else{
            doStuff();
        }
        return view;
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