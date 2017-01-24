package com.quranreading.helper;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.quranreading.listeners.OnCurrentLocationFoundListner;
import com.quranreading.sharedPreference.LocationPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class GeoCoderVolley {

	private final String TAG = "LOCATION";
	private final int REQUEST_MAX_LENGTH = 7000;

	String city_Name = "";
	double latd = -2, longit = -2;
	RequestQueue queue;
	OnCurrentLocationFoundListner listener;
	private boolean running = false;
	JsonObjectRequest req;
	Context mContext;

	private Handler mHandler = new Handler();

	public GeoCoderVolley(Context context)
	{
		mContext = context;
	}

	public void setListener(OnCurrentLocationFoundListner listener) {
		this.listener = listener;
	}

	public static String EncodeURL(String url) {
		try
		{
			url = URLEncoder.encode(url, "utf-8");
			return url;
		}
		catch (Exception exp)
		{
			return url;
		}
	}

	public void fetchCityFromCoordinates(final Context contex, final Location location) {
		if(running)
			return;

		cancelRequest();

		queue = Volley.newRequestQueue(contex);

		latd = location.getLatitude();
		longit = location.getLongitude();

		String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude() + "&sensor=false&language=fr";

		req = new JsonObjectRequest(googleMapUrl, new JSONObject(), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mHandler.removeCallbacks(mRunnable);
				handleCityNameJsonResponse(response);
				running = false;
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mHandler.removeCallbacks(mRunnable);
				running = false;
				if(city_Name == null)
				{
					city_Name = "";
				}
				listener.onCurrentLocationFoundListner(city_Name.trim(), null, -2, -2);

			}
		});

		req.setTag(TAG);
		queue.add(req);
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, REQUEST_MAX_LENGTH);
	}

	private void handleCityNameJsonResponse(JSONObject googleMapResponse) {

		String s = "";
		String code = "";
		try
		{
			JSONArray results = (JSONArray) googleMapResponse.get("results");
			for (int i = 0; i < results.length(); i++)
			{
				// loop among all addresses within this result
				JSONObject result = results.getJSONObject(i);
				if(result.has("formatted_address"))
				{
					if(result.has("address_components"))
					{
						JSONArray jArray = result.getJSONArray("address_components");
						for (int j = 0; j < jArray.length(); j++)
						{
							JSONObject objType = jArray.getJSONObject(j);
							if(objType.has("types"))
							{
								JSONArray jArrayTypes = objType.getJSONArray("types");
								for (int k = 0; k < jArrayTypes.length(); k++)
								{
									if(jArrayTypes.getString(k).equals("country"))
									{
										if(objType.has("short_name"))
										{
											if(objType.getString("short_name").length() == 2)
												code = objType.getString("short_name").toLowerCase();
										}
									}

								}
							}
						}
					}

					s = result.getString("formatted_address");
					break;
				}
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(!code.isEmpty())
		{
			LocationPref locationPref = new LocationPref(mContext);
			locationPref.setCountryCode(code);
		}
		listener.onCurrentLocationFoundListner(s, code, latd, longit);
	}

	public void fetchAddressFromCoordinates(final Context contex, final Location location) {
		if(running)
			return;

		cancelRequest();

		queue = Volley.newRequestQueue(contex);

		latd = location.getLatitude();
		longit = location.getLongitude();

		String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude() + "&sensor=false&language=fr";

		req = new JsonObjectRequest(googleMapUrl, new JSONObject(), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mHandler.removeCallbacks(mRunnable);
				handleCoordinatesJsonResponse(response);
				running = false;
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mHandler.removeCallbacks(mRunnable);
				running = false;
				if(city_Name == null)
				{
					city_Name = "";
				}
				listener.onCurrentLocationFoundListner(city_Name.trim(), null, -2, -2);

			}
		});

		req.setTag(TAG);
		queue.add(req);
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, REQUEST_MAX_LENGTH);
	}

	private void handleCoordinatesJsonResponse(JSONObject googleMapResponse) {

		String subAddress = null, cityName = null, countryName = null;

		String s = "";
		try
		{
			JSONArray results = (JSONArray) googleMapResponse.get("results");
			for (int i = 0; i < results.length(); i++)
			{
				int count = 0;
				// loop among all addresses within this result
				JSONObject result = results.getJSONObject(i);
				if(result.has("address_components"))
				{
					JSONArray addressComponents = result.getJSONArray("address_components");
					// loop among all address component to find a 'locality' or 'sublocality'
					for (int j = 0; j < addressComponents.length(); j++)
					{
						JSONObject addressComponent = addressComponents.getJSONObject(j);
						if(result.has("types"))
						{
							JSONArray types = addressComponent.getJSONArray("types");

							// search for locality and sublocality

							for (int k = 0; k < types.length(); k++)
							{
								if("sublocality".equals(types.getString(k)))
								{
									if(addressComponent.has("long_name"))
									{
										subAddress = addressComponent.getString("long_name");
										count++;
									}
									else if(addressComponent.has("short_name"))
									{
										subAddress = addressComponent.getString("short_name");
										count++;
									}
								}

								if("locality".equals(types.getString(k)))
								{
									if(addressComponent.has("long_name"))
									{
										cityName = addressComponent.getString("long_name");
										count++;
									}
									else if(addressComponent.has("short_name"))
									{
										cityName = addressComponent.getString("short_name");
										count++;
									}
								}

								if("country".equals(types.getString(k)))
								{
									if(addressComponent.has("long_name"))
									{
										countryName = addressComponent.getString("long_name");
										count++;
									}
									else if(addressComponent.has("short_name"))
									{
										countryName = addressComponent.getString("short_name");
										count++;
									}
								}
							}
						}

						if(count == 3)
						{
							break;
						}
					}
				}

				if(count == 3)
				{
					break;
				}
			}

			s = "";

			if(subAddress != null)
			{
				s = subAddress;
			}

			if(cityName != null)
			{
				city_Name = cityName;
				if(s.equals(""))
				{
					s = cityName;
				}
				else
				{
					s = s + ", " + cityName;
				}
			}

			if(countryName != null)
			{
				if(s.equals(""))
				{
					s = countryName;
				}
				else
				{
					s = s + ", " + countryName;
				}
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(city_Name == null)
			city_Name = "";

		if(city_Name.equals(""))
			city_Name = s;



		listener.onCurrentLocationFoundListner(city_Name, null, latd, longit);
	}

	public void fetchCoordinatesFromAddress(final Context contex, final String Address) {
		if(running)
			return;

		cancelRequest();

		queue = Volley.newRequestQueue(contex);
		String addrssUrl = EncodeURL(Address);
		String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=" + addrssUrl + "&sensor=false&language=fr";

		Log.i("URL", googleMapUrl);

		req = new JsonObjectRequest(googleMapUrl, new JSONObject(), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mHandler.removeCallbacks(mRunnable);
				handleAddressJsonResponse(response);
				running = false;
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				mHandler.removeCallbacks(mRunnable);
				running = false;
				if(city_Name == null)
				{
					city_Name = "";
				}
				listener.onCurrentLocationFoundListner(city_Name.trim(), null, -2, -2);
			}
		});

		req.setTag(TAG);
		queue.add(req);
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, REQUEST_MAX_LENGTH);
	}

	private void handleAddressJsonResponse(JSONObject googleMapResponse) {
		try
		{
			JSONArray results = (JSONArray) googleMapResponse.get("results");
			for (int i = 0; i < results.length(); i++)
			{
				JSONObject result = results.getJSONObject(i);

				if(result.has("address_components"))
				{
					JSONArray jb = result.getJSONArray("address_components");

					JSONObject jr;

					try
					{
						int max = jb.length();

						if(max <= 0)
						{
							city_Name = "";
						}
						else if(max == 1)
						{
							jr = jb.getJSONObject(0);
							city_Name = jr.getString("long_name");
						}
						else if(max > 2)
						{
							jr = jb.getJSONObject(2);
							city_Name = jr.getString("long_name");
						}
						else
						{
							jr = jb.getJSONObject(1);
							city_Name = jr.getString("long_name");
						}

					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if(result.has("geometry"))
				{
					JSONObject jb = result.getJSONObject("geometry");

					if(jb.has("location"))
					{
						JSONObject jb1 = jb.getJSONObject("location");

						latd = Double.parseDouble(jb1.getString("lat"));
						longit = Double.parseDouble(jb1.getString("lng"));
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			city_Name = null;
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			city_Name = null;
			e.printStackTrace();
		}

		if(city_Name == null)

		{
			city_Name = "";
		}

		listener.onCurrentLocationFoundListner(city_Name, null, latd, longit);
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {

			cancelRequest();
			running = false;
			if(city_Name == null)
			{
				city_Name = "";
			}
			listener.onCurrentLocationFoundListner(city_Name.trim(), null, -2, -2);
		}
	};

	private void cancelRequest() {
		if(req != null && queue != null)
		{
			queue.cancelAll(TAG);
		}
	}
}