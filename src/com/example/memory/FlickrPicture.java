package com.example.memory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FlickrPicture {

	String farm_id;
	String server_id;
	String id;
	String secret;

	Bitmap bitmap;

	public FlickrPicture(String farm_id, String server_id, String id,
			String secret) {

		this.farm_id = farm_id;
		this.server_id = server_id;
		this.id = id;
		this.secret = secret;

		String string_url = "http://farm" + farm_id + ".static.flickr.com/"
				+ server_id + "/" + id + "_" + secret + "_m.jpg";

		try {
			URL pic_url = new URL(string_url);

			HttpURLConnection connection = (HttpURLConnection) pic_url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(input);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

}