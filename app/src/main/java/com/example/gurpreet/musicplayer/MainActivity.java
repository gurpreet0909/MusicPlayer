package com.example.gurpreet.musicplayer;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.support.v4.app.Fragment;

import android.media.audiofx.*;

public class MainActivity extends Fragment
        implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {

    TextView bass_boost_label = null;
    SeekBar bass_boost = null;
    CheckBox enabled = null;
    Button flat = null;

    Equalizer eq = null;
    BassBoost bb = null;

    int min_level = 0;
    int max_level = 100;

    static final int MAX_SLIDERS = 6;
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.equalizer, container, false);

        enabled = (CheckBox) rootView.findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener(this);

        bass_boost = (SeekBar) rootView.findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        bass_boost_label = (TextView) rootView.findViewById(R.id.bass_boost_label);

        sliders[0] = (SeekBar) rootView.findViewById(R.id.slider_1);
        slider_labels[0] = (TextView) rootView.findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar) rootView.findViewById(R.id.slider_2);
        slider_labels[1] = (TextView) rootView.findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar) rootView.findViewById(R.id.slider_3);
        slider_labels[2] = (TextView) rootView.findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar) rootView.findViewById(R.id.slider_4);
        slider_labels[3] = (TextView) rootView.findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar) rootView.findViewById(R.id.slider_5);
        slider_labels[4] = (TextView) rootView.findViewById(R.id.slider_label_5);
        sliders[5] = (SeekBar) rootView.findViewById(R.id.slider_6);
        slider_labels[5] = (TextView) rootView.findViewById(R.id.slider_label_6);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        eq = new Equalizer(0, 0);
        if (eq != null) {
            eq.setEnabled(true);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++) {
                int[] freq_range = eq.getBandFreqRange((short) i);
                sliders[i].setOnSeekBarChangeListener(this);
                slider_labels[i].setText(formatBandLabel(freq_range));
            }
        }
        for (int i = num_sliders; i < MAX_SLIDERS; i++) {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost(0, 0);
        if (bb != null) {
        } else {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }

        updateUI();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int level,
                                  boolean fromTouch) {
        if (seekBar == bass_boost) {
            bb.setEnabled(level > 0 ? true : false);
            bb.setStrength((short) level); // Already in the right range 0-1000
        } else if (eq != null) {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++) {
                if (sliders[i] == seekBar) {
                    eq.setBandLevel((short) i, (short) new_level);
                    break;
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    public String formatBandLabel(int[] band) {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }


    public String milliHzToString(int milliHz) {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    public void updateSliders() {
        for (int i = 0; i < num_sliders; i++) {
            int level;
            if (eq != null)
                level = eq.getBandLevel((short) i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress(pos);
        }
    }

    public void updateBassBoost() {
        if (bb != null)
            bass_boost.setProgress(bb.getRoundedStrength());
        else
            bass_boost.setProgress(0);
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if (view == (View) enabled) {
            eq.setEnabled(isChecked);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == (View) flat) {
            setFlat();
        }
    }


    public void updateUI() {
        updateSliders();
        updateBassBoost();
        enabled.setChecked(eq.getEnabled());
    }

    public void setFlat() {
        if (eq != null) {
            for (int i = 0; i < num_sliders; i++) {
                eq.setBandLevel((short) i, (short) 0);
            }
        }

        if (bb != null) {
            bb.setEnabled(false);
            bb.setStrength((short) 0);
        }

        updateUI();
    }


    public void showAbout() {
      /*  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("About Simple EQ");
        alertDialogBuilder.setMessage(R.string.app_name);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton (R.string.ok,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
        AlertDialog ad = alertDialogBuilder.create();
        ad.show();  */

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }
}


