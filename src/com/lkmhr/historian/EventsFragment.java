package com.lkmhr.historian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.lkmhr.historian.utils.DatabaseManager;
import com.lkmhr.historian.utils.HistorianAdapter;
import com.lkmhr.historian.utils.HistoryEvent;
import com.lkmhr.historian.utils.DataProvider;


public class EventsFragment extends Fragment{
	
	private View rootView;
	
	private List<HistoryEvent> mEvents;
	private HistorianAdapter adapter;
	private ListView listView;
	
	private ProgressDialog progressDialog;
	private String html = "";
	private int index = 1;
	private String type = "event";
	private String today = "May_3";
	
	public EventsFragment() { }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_events, container, false);
		
		initView();
		
		Bundle bundle = this.getArguments();
		if(bundle!=null){
			index = bundle.getInt("INDEX");
			if(index!=4){
				type = DataProvider.getEventType(index);
			}
		}
		today = DataProvider.getDate();
		
		loadData();
		return rootView;
	}
	
	private void initView(){
		listView = (ListView) rootView.findViewById(R.id.listView);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				ViewFlipper flipper = (ViewFlipper) view.findViewById(R.id.flipper);
				flipper.setInAnimation(getActivity(), R.anim.in_left);
				flipper.setOutAnimation(getActivity(), R.anim.out_right);
				flipper.showNext();
				
				Button delButton = (Button) view.findViewById(R.id.del_button);
				delButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(index!=4) {
							mEvents.remove(position);
							adapter.notifyDataSetChanged();
							adapter.notifyDataSetInvalidated();
						} else {
							Toast.makeText(getActivity(),"NOO!!!",Toast.LENGTH_LONG).show();
						}
					}
				});
				return true;
			}
		});
		
		mEvents = new ArrayList<HistoryEvent>();
	}
	
	private void loadData(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());		
		String saved_date = preferences.getString("date", "not_set");		
		
		DatabaseManager db = new DatabaseManager(getActivity());
		if(index == 4){
			HistoryEvent event = new HistoryEvent();
			event.setEventId(0+"");
			event.setEventDate(today);
			event.setEventData("Not Yet implemented!");
			event.setEventType("fav");
			event.setFav(true);
			
			mEvents.add(event);
			
			adapter = new HistorianAdapter(getActivity(), mEvents);
			listView.setAdapter(adapter);
		}else if(saved_date.equals(today) && db.hasOfType(type)){
			setAdapterToList();
		} else {
			Editor edit = preferences.edit();
			edit.putString("date", today);
			edit.apply(); 
			
			new AsyncEventsPull().execute(DataProvider.getUrl(index));
		}
	}
	
	public void setAdapterToList(){
		DatabaseManager db = new DatabaseManager(getActivity());
		mEvents = db.getDailyEventsByType(type);
		adapter = new HistorianAdapter(getActivity(), mEvents);
		listView.setAdapter(adapter);
	}
	
	private class AsyncEventsPull extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() { 
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Wait.. Conquering History...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();
			
		}

		@Override
		protected String doInBackground(String... EVENTS_URL) {
			try {
				String url = EVENTS_URL[0];				
								
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				HttpResponse response = client.execute(request);

				html = "";
				
				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null)
				{
				    str.append(line);
				}
				in.close();
				html = str.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return html;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			List<HistoryEvent> events= new ArrayList<HistoryEvent>();

			Document document = Jsoup.parse(html);
			Element ulTag = document.getElementsByTag("ul").first();
			
			if(ulTag!=null){
				Elements lists = document.select("ul li");
				for (Element element : lists) {
//		            Log.v("TAG",element.text());
					HistoryEvent event = new HistoryEvent();
					
					event.setEventDate(today);
					event.setEventData(element.text());
					event.setEventType(type);
					event.setFav(false);
					
					events.add(event);
		        }
			}
			
//			Log.v("HTML", result);
			
			DatabaseManager db = new DatabaseManager(getActivity());
			db.insertTodaysEvents(events);
//			Log.v("DB", "INSERT " + type);
			
			setAdapterToList();		
			progressDialog.dismiss();
		}
	}
}
