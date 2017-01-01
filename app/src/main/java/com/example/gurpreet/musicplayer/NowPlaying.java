package com.example.gurpreet.musicplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;

public class NowPlaying extends Fragment implements View.OnClickListener {

    static MediaPlayer mediaPlayer;
    ArrayList<File> mysongs = null;
    Button playbutton, forwordButton, backwordButton, nextButton, previousButton;
    SeekBar seekBar;
    int position;
    Uri uri;
    Thread runseekbar;
    Bundle b;
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    MediaMetadataRetriever mediaMetadataRetriever;
    ImageView imageView;
    TextView songText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nowplaying, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Playing Now...");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_drawer);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        playbutton = (Button) rootView.findViewById(R.id.play_button);
        forwordButton = (Button) rootView.findViewById(R.id.forward);
        backwordButton = (Button) rootView.findViewById(R.id.backward);
        nextButton = (Button) rootView.findViewById(R.id.next_song);
        previousButton = (Button) rootView.findViewById(R.id.previous_song);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        imageView = (ImageView) rootView.findViewById(R.id.playlist_imageView);
        songText = (TextView) rootView.findViewById(R.id.song_name);

        playbutton.setOnClickListener(this);
        forwordButton.setOnClickListener(this);
        backwordButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mediaMetadataRetriever = new MediaMetadataRetriever();
        byte[] art;


        runseekbar = new Thread() {
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        try {
                            currentPosition = mediaPlayer.getCurrentPosition();
                        } catch (IllegalStateException e) {
                        }
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
            }
        }

        position = 0;
        b = this.getArguments();

        try {
            mysongs = (ArrayList) b.getSerializable("list");
            position = b.getInt("pos", 0);
            uri = Uri.parse(mysongs.get(position).toString());
            mediaMetadataRetriever.setDataSource(getContext(), uri);

            try {
                art = mediaMetadataRetriever.getEmbeddedPicture();
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                imageView.setImageBitmap(songImage);
                songText.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

            } catch (Exception e) {
                imageView.setBackgroundResource(R.drawable.cd);
                songText.setText("Unknown Name");

            }
            mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), uri);
            mediaPlayer.start();
            runseekbar.start();
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (Exception e) {
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();


        switch (id) {
            case R.id.play_button:
                if (mediaPlayer.isPlaying()) {
                    playbutton.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    playbutton.setBackgroundResource(R.drawable.pause);
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }

                }
                break;
            case R.id.forward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                break;
            case R.id.backward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                break;
            case R.id.next_song:
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position + 1) % mysongs.size();
                uri = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                break;
            case R.id.previous_song:
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position - 1 < 0) ? mysongs.size() - 1 : position - 1;
             /*   if(position-1<0){
                    position=mySongs.size()-1;
                }
                else {
                    position=position-1;
                }  */
                position = (position - 1) % mysongs.size();
                uri = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                break;
        }


    }
}



