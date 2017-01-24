package com.quranreading.helper;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class MyService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		startAsyncTask();
	}

	private void startAsyncTask() {
		AsyncTaskHandler task = new AsyncTaskHandler();
		task.execute("s");
	}

	private class AsyncTaskHandler extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... values) {
			String response = "";
			try
			{
				initializeDB();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {

			stopSelf();
		}
	}

	public void initializeDB() {
		DBManager dbObj = new DBManager(getApplicationContext());
		try
		{
			dbObj.createDataBase();
			dbObj.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
