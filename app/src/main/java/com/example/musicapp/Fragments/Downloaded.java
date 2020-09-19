package com.example.musicapp.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.musicapp.Adapters.MainAdapter;
import com.example.musicapp.Adapters.OtherAdapter;
import com.example.musicapp.Model.Song;
import com.example.musicapp.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class Downloaded extends Fragment {

    private static final int MY_PERMISSSION_REQUEST=1;
    List<String> arrayList=new ArrayList<>();
    OtherAdapter otherAdapter;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_downloaded, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //MainAdapter mainAdapter=new MainAdapter(getContext(),null);
        //arrayList=mainAdapter.returnArray();
        otherAdapter=new OtherAdapter(getContext(),arrayList);
        recyclerView.setAdapter(otherAdapter);
        return view;
    }

}