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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lkmhr.historian.utils.HistorianAdapter;
import com.lkmhr.historian.utils.UrlProvider;
//import android.util.Log;


public class EventsFragment extends Fragment{
	
	private List<String> feeds;
	private HistorianAdapter adapter;
	private ListView listView;
	
	private ProgressDialog progressDialog;
	private String html = "";
	
	public EventsFragment() { }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_events, container, false);
		
		listView = (ListView) rootView.findViewById(R.id.listView);
		feeds = new ArrayList<String>();
		
		Bundle bundle = this.getArguments();
		
		int index = 1;
		if(bundle!=null){
			index = bundle.getInt("INDEX");			
		}
		
		new AsyncEventsPull().execute(UrlProvider.getUrl(index));
		
		return rootView;
	}
	
	private class AsyncEventsPull extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() { 
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Loading content...");
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
			
			Document document = Jsoup.parse(html);
			Element ulTag = document.getElementsByTag("ul").first();
			
			if(ulTag!=null){
				Elements lists = document.select("ul li");
				for (Element element : lists) {
//		            Log.v("TAG",element.text());
					feeds.add(element.text());
		        }
			}
			
//			Log.v("HTML", result);
			
			adapter = new HistorianAdapter(getActivity(), feeds);
			listView.setAdapter(adapter);		
			
			progressDialog.dismiss();
		}
		
		
	}
}
