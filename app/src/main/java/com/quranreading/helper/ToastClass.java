package com.quranreading.helper;

import com.quranreading.qibladirection.R;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastClass {

	public static void showToast(String msg, Context mContext) {
		Toast toast = Toast.makeText(mContext.getApplicationContext(), msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void showShortToast(Context mContext, String message, int milliesTime, int gravity) {

		if(mContext.getString(R.string.device).equals("large"))
		{
			final Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			final Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
			toast.show();

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					toast.cancel();
				}
			}, milliesTime);
		}
	}
}
