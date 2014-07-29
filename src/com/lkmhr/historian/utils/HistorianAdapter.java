package com.lkmhr.historian.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lkmhr.historian.R;

public class HistorianAdapter extends ArrayAdapter<String> {

	private final List<String> feeds;
	private final Context context;

	public HistorianAdapter(Context context, List<String> feeds) {
		super(context, R.layout.list_row);
		this.context = context;
		this.feeds = feeds;
	}

	@Override
	public int getCount() {
		return feeds.size();
	}

	@Override
	public String getItem(int position) {
		return feeds.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PlaceHolder holder = new PlaceHolder();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.list_row, parent, false);

		holder.title = (TextView) rowView.findViewById(R.id.event_row);

		holder.title.setText(feeds.get(position));

		return rowView;
	}

	public class PlaceHolder {
		public TextView title;
	}
}
