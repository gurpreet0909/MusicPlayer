package com.example.gurpreet.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends ArrayAdapter implements Filterable {

    List<SongData> list = new ArrayList<>();
    List<SongData> filterList;
    ValueFilter valueFilter;

    public SongAdapter(Context context, int resource) {

        super(context, resource);
    }


    public void add(SongData object) {
        super.add(object);
        list.add(object);
        filterList.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }



    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        row = convertView;

        SongHolder songHolder = new SongHolder();
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_row, parent, false);

            songHolder.art_album = (TextView) row.findViewById(R.id.artist_album);
            songHolder.dur_album = (TextView) row.findViewById(R.id.dur_album);
            songHolder.tit_album = (TextView) row.findViewById(R.id.title_album);
            row.setTag(songHolder);
        } else {
            songHolder = (SongHolder) row.getTag();
        }

        SongData songData = (SongData) this.getItem(position);
        songHolder.tit_album.setText(songData.getTitle());
        songHolder.art_album.setText(songData.getArtists());
        songHolder.dur_album.setText(songData.getDuration());

        return row;
    }

    static class SongHolder {
        TextView art_album, dur_album, tit_album;
    }


    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List filterLists = new ArrayList();
                for (int i = 0; i < filterList.size(); i++) {
                    if ((filterList.get(i).toString().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterLists.add(filterList.get(i));
                    }
                }
                results.count = filterLists.size();
                results.values = filterLists;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            list = (List) results.values;
            notifyDataSetChanged();
        }
    }
}



