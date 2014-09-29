package com.example.memory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;

public class NewGameActivity extends Activity {

	FlickrPicture[] array_pictures;

	String flickr_query = "http://api.flickr.com/services/rest/?method=flickr.photos.search"
			+ "&api_key=35bc47a856702efbaf5133aefcc3da7e"
			+ "&format=json"
			+ "&nojsoncallback=1"
			+ "&per_page=8"
			+ "&tags=kitten";

	final String DEFAULT_SEARCH = "new_york";
	
	Map<Integer, Integer> cards_to_pics;

	
	/*
	 * UI elements
	 */
	ImageButton pic1, pic2, pic3, pic4, pic5, pic6, pic7, pic8;

	Bitmap bmFlickr;

	ProgressDialog loading;

	/*
	 * Memory logic
	 */
	int first_card = -1, second_card = -1;
	
	/*
	 * Lock
	 */
	private static Object lock = new Object();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);
		// Show the Up button in the action bar.
		
		cards_to_pics = new HashMap<Integer, Integer>();

		pic1 = (ImageButton) findViewById(R.id.ImageButton00);
		pic2 = (ImageButton) findViewById(R.id.ImageButton01);
		pic3 = (ImageButton) findViewById(R.id.ImageButton02);
		pic4 = (ImageButton) findViewById(R.id.ImageButton03);
		pic5 = (ImageButton) findViewById(R.id.ImageButton10);
		pic6 = (ImageButton) findViewById(R.id.ImageButton11);
		pic7 = (ImageButton) findViewById(R.id.ImageButton12);
		pic8 = (ImageButton) findViewById(R.id.ImageButton13);
/*
		Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotation);
	    LayoutAnimationController animController = new LayoutAnimationController(rotateAnim, 0);
	    FrameLayout layout = (FrameLayout)findViewById(R.id.myFramLayout);
	    layout.setLayoutAnimation(animController);
*/
		new LoadingPicturesTask().execute();
		
		new Thread(new MapCardsToPictures()).run();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	public void flipCard(View view) {
		
		// Two cards already flipped
		if (first_card != -1 && second_card != -1) {
			return;
		}
		
		// Flip the card
		ImageButton button = (ImageButton)view;
		FlickrPicture pic = array_pictures[cards_to_pics.get(button.getId())];
		button.setImageBitmap((pic.getBitmap()));
		
		if (first_card == -1) {
			first_card = button.getId();
		}
		else {
			if (button.getId() == first_card)
				// Pressed twice the same card
				return;
			
			second_card = button.getId();
			
			// Verify if cards are equal
			
			ImageButton b1 = (ImageButton)findViewById(first_card);
			ImageButton b2 = (ImageButton)findViewById(second_card);
			
			if (cards_to_pics.get(first_card) == cards_to_pics.get(second_card)) {
				b1.setVisibility(View.INVISIBLE);
				b2.setVisibility(View.INVISIBLE);
			}
			else {
				b1.setImageResource(R.drawable.back_card);
				b2.setImageResource(R.drawable.back_card);
			}
			first_card = -1;
			second_card = -1;

		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private class LoadingPicturesTask extends AsyncTask<Void, Void, Void> {

		
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			String result = getPicsJSON(flickr_query);
			array_pictures = JSONtoBitmap(result);
			
			//publishProgress();

			return null;
		}

		
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			Bitmap bitmap;

			bitmap = array_pictures[0].getBitmap();
			if (bitmap != null) {
				pic1.setImageBitmap(bitmap);
				pic1.setScaleType(ScaleType.FIT_XY);
			}
			
			bitmap = array_pictures[1].getBitmap();
			if (bitmap != null) {
				pic2.setImageBitmap(bitmap);
			}

			bitmap = array_pictures[2].getBitmap();
			if (bitmap != null) {
				pic3.setImageBitmap(bitmap);
			}

			bitmap = array_pictures[3].getBitmap();
			if (bitmap != null) {
				pic4.setImageBitmap(bitmap);
			}

			bitmap = array_pictures[4].getBitmap();
			if (bitmap != null) {
				pic5.setImageBitmap(bitmap);
			}
			
			bitmap = array_pictures[5].getBitmap();
			if (bitmap != null) {
				pic6.setImageBitmap(bitmap);
			}
			
			bitmap = array_pictures[6].getBitmap();
			if (bitmap != null) {
				pic7.setImageBitmap(bitmap);
			}
			
			bitmap = array_pictures[7].getBitmap();
			if (bitmap != null) {
				pic8.setImageBitmap(bitmap);
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loading.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loading = ProgressDialog.show(NewGameActivity.this, "",
					"Downloading pictures from Flickr. Please wait...", true);
		}

		
		
		
		
		
		
		
		
		
		
		
		private String getPicsJSON(String query) {

			String pics_json = null;

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(flickr_query);

			try {
				HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

				if (httpEntity != null) {
					InputStream inputStream = httpEntity.getContent();
					Reader in = new InputStreamReader(inputStream);
					BufferedReader bufferedreader = new BufferedReader(in);
					StringBuilder stringBuilder = new StringBuilder();

					String stringReadLine = null;

					while ((stringReadLine = bufferedreader.readLine()) != null) {
						stringBuilder.append(stringReadLine + "\n");
					}

					pics_json = stringBuilder.toString();
					inputStream.close();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return pics_json;
		}

		private FlickrPicture[] JSONtoBitmap(String json) {

			FlickrPicture[] pics_array = null;

			bmFlickr = null;

			String farm_id;
			String server_id;
			String id;
			String secret;


			try {
				JSONObject json_obj = new JSONObject(json);
				JSONObject json_pics = json_obj.getJSONObject("photos");
				JSONArray json_picarray = json_pics.getJSONArray("photo");

				pics_array = new FlickrPicture[json_picarray.length()];

				for (int i = 0; i < json_picarray.length(); i++) {
					JSONObject flickr_pic = json_picarray.getJSONObject(i);

					farm_id = flickr_pic.getString("farm");
					server_id = flickr_pic.getString("server");
					id = flickr_pic.getString("id");
					secret = flickr_pic.getString("secret");

					pics_array[i] = new FlickrPicture(farm_id, server_id, id, secret);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return pics_array;
		}

	}
	
	
	
	private class MapCardsToPictures implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			List<Integer> cards = new ArrayList<Integer>();
			ImageButton button = (ImageButton)findViewById(R.id.ImageButton00);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton01);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton02);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton03);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton10);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton11);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton12);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton13);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton20);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton21);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton22);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton23);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton30);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton31);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton32);
			cards.add(new Integer(button.getId()));
			button = (ImageButton)findViewById(R.id.ImageButton33);
			cards.add(new Integer(button.getId()));
			
			Random r = new Random();
			int size = 16;
			int cards_left = size;
			
			for (int i=0; i<size/2; i++) {
				Integer a = cards.remove(r.nextInt(cards_left));
				cards_left--;
				Integer b = cards.remove(r.nextInt(cards_left));
				cards_left--;
				cards_to_pics.put(a, i);
				cards_to_pics.put(b, i);
			}
			
			
		}
		
	}
	

}
