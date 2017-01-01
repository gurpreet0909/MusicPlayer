package com.example.gurpreet.musicplayer;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Playlist extends Fragment implements SearchView.OnQueryTextListener {

    ListView listView;
    Toolbar toolbar;
    ArrayList<File> mySongs = null;
    String[] songs;
    MediaMetadataRetriever retriever;
    SongAdapter songAdapter;


      @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.playlist, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("PlayList");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_drawer);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        setHasOptionsMenu(true);


        listView = (ListView) rootView.findViewById(R.id.play_listview);
        retriever = new MediaMetadataRetriever();
        songAdapter = new SongAdapter(getContext(), R.layout.list_row);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mySongs = findSongs(Environment.getExternalStorageDirectory());
        songs = new String[mySongs.size()];

        new Thread(new Runnable() {
            @Override
            public void run() {

                String duration;
                String artist;
                String title;


                for (int i = 0; i < mySongs.size(); i++) {
                    songs[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");

                    Uri uri = Uri.parse(mySongs.get(i).toString());
                    retriever.setDataSource(getContext(), uri);
                    try {
                        String out = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long dur = Long.parseLong(out);
                        String seconds = String.valueOf((dur % 60000) / 1000);
                        String minutes = String.valueOf(dur / 60000);
                        duration = minutes + ":" + seconds;

                        artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        SongData songData = new SongData(artist, title, duration);

                       songAdapter.add(songData);
                    } catch (Exception e) {

                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(songAdapter);
                    }
                });


            }
        }).start();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NowPlaying nowPlaying = new NowPlaying();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putSerializable("list", mySongs);
                arguments.putInt("pos", position);
                nowPlaying.setArguments(arguments);

                fragmentTransaction.replace(R.id.main_content, nowPlaying)
                        .addToBackStack(null)
                        .commit();
            }
        });


    }


    private ArrayList<File> findSongs(File root) {
        ArrayList<File> songList = new ArrayList<File>();
        File[] files = root.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                songList.addAll(findSongs(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith("wav")) {
                    songList.add(singleFile);
                }
            }
        }

        return songList;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        //   super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        songAdapter.getFilter().filter(newText);


        return true;
    }
}