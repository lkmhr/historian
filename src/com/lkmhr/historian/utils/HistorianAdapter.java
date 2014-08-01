package com.lkmhr.historian.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.lkmhr.historian.R;

public class HistorianAdapter extends ArrayAdapter<String> {

	private final List<HistoryEvent> events;
	private final Context context;

	public HistorianAdapter(Context context, List<HistoryEvent> events) {
		super(context, R.layout.list_row);
		this.context = context;
		this.events = events;
	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder = new ViewHolder();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.list_row, parent, false);
//		final DatabaseManager db = new DatabaseManager(context);

		holder.flipper = (ViewFlipper) rowView.findViewById(R.id.flipper);
		
		holder.title = (TextView) rowView.findViewById(R.id.list_row);
		holder.favButton = (ToggleButton) rowView.findViewById(R.id.fav_button);
		
		holder.shareButton = (Button) rowView.findViewById(R.id.share_button);
		holder.delButton = (Button) rowView.findViewById(R.id.del_button);
		holder.cancelButton = (Button) rowView.findViewById(R.id.cancel_button);
		
		holder.cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				holder.flipper.setInAnimation(context, R.anim.in_right);
				holder.flipper.setOutAnimation(context, R.anim.out_left);
				holder.flipper.showNext();
			}			
		});
		
		holder.favButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
//					db.addToFav(events.get(position));
					events.get(position).setFav(true);
					buttonView.setBackgroundResource(R.drawable.star_on);
				} else {
					buttonView.setBackgroundResource(R.drawable.star_off);
					events.get(position).setFav(false);
				}
			}
		});
		holder.delButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Event removed temporarily!", Toast.LENGTH_LONG).show();
			}
		});
		holder.shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Not worth Sharing. :/", Toast.LENGTH_LONG).show();
			}
		});
		
		if(events.get(position).isFav()){
			holder.favButton.setBackgroundResource(R.drawable.star_on);
		}else {
			holder.favButton.setBackgroundResource(R.drawable.star_off);
		}

//		holder.title.setText(feeds.get(position));
		holder.title.setText(events.get(position).getEventData());
		
		return rowView;
	}

	public class ViewHolder {
		
		public ViewFlipper flipper;
		
		public TextView title;
		
		public ToggleButton favButton;
		public Button shareButton;
		public Button delButton;
		public Button cancelButton;
		
	}
}
