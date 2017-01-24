package places.webtask;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.os.Handler;
import places.helper.DBManagerPlaces;
import places.models.PlacesModel;

public class TaskFetchPlaces {

	public static final String GEOMETRY_OBJECT = "geometry";
	public static final String LOCATION_OBJECT = "location";
	public static final String RESULTS_ARRAY = "results";

	public static final String PLACE_ID = "place_id";
	public static final String NAME = "name";
	public static final String VICINITY = "vicinity";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String RATING = "rating";

	private final String TAG = "PLACES";
	private final int REQUEST_MAX_LENGTH = 8000;
	Context mContext;
	RequestQueue queue;
	OnPlacesLoadedListner listener;
	private boolean running = false;
	JsonObjectRequest req;
	ArrayList<PlacesModel> mPlacesList = new ArrayList<PlacesModel>();

	private Handler mHandler = new Handler();

	double mLatitude, mLongitude;
	String type;

	public TaskFetchPlaces(Context context, String lat, String lng, String type) {
		mContext = context;
		mLatitude = Double.parseDouble(lat.trim());
		mLongitude = Double.parseDouble(lng.trim());
		this.type = type;

	}

	public void setListener(OnPlacesLoadedListner listener) {
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

	public void getPlacesData(String url) {
		if(running)
			return;

		cancelRequest();

		queue = Volley.newRequestQueue(mContext);

		req = new JsonObjectRequest(url, new JSONObject(), new Response.Listener<JSONObject>() {
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
				listener.onPlacesLoaded(null);

			}
		});

		req.setTag(TAG);
		queue.add(req);
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, REQUEST_MAX_LENGTH);
	}

	private void handleCoordinatesJsonResponse(JSONObject googleMapResponse) {

		String id;
		String name;
		String address;
		String lat;
		String lng;
		String rating = null;

		mPlacesList.clear();

		try
		{
			JSONArray results = (JSONArray) googleMapResponse.get(RESULTS_ARRAY);
			if(results != null && results.length() > 0)
			{
				DBManagerPlaces db = new DBManagerPlaces(mContext);
				db.open();
				db.deleteData(type);
				for (int i = 0; i < results.length(); i++)
				{
					// loop among all addresses within this result
					JSONObject result = results.getJSONObject(i);
					if(result.has(RATING))
					{
						rating = result.getString(RATING);
					}
					id = result.getString(PLACE_ID);
					name = result.getString(NAME);
					address = result.getString(VICINITY);
					JSONObject objGeo = result.getJSONObject(GEOMETRY_OBJECT);
					JSONObject objLocation = objGeo.getJSONObject(LOCATION_OBJECT);

					lat = objLocation.getDouble(LAT) + "";
					lng = objLocation.getDouble(LNG) + "";

					PlacesModel data = new PlacesModel(id, name, address, lat, lng, rating);
					data.setDistance(getDistance(Double.parseDouble(lat), Double.parseDouble(lng)));
					db.saveData(data, type);
					mPlacesList.add(data);
				}

				db.close();
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			mPlacesList.clear();
			e.printStackTrace();
		}

		if(mPlacesList.size() > 0)
		{
			Collections.sort(mPlacesList, new Comparator<PlacesModel>() {

				@Override
				public int compare(PlacesModel lhs, PlacesModel rhs) {
					// TODO Auto-generated method stub

					if((Float.parseFloat(lhs.getDistance())) > Float.parseFloat(rhs.getDistance()))
					{
						return 1;
					}
					if((Float.parseFloat(lhs.getDistance())) < Float.parseFloat(rhs.getDistance()))
					{
						return -1;
					}
					return 0;
				}
			});
		}

		listener.onPlacesLoaded(mPlacesList);
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {

			cancelRequest();
			mHandler.removeCallbacks(mRunnable);
			running = false;
			listener.onPlacesLoaded(null);
		}
	};

	private void cancelRequest() {
		if(req != null && queue != null)
		{
			queue.cancelAll(TAG);
		}
	}

	private String getDistance(double latitude, double longitude) {

		double pi = 4 * Math.atan2(1, 1);
		double DtoR = pi / 180;
		double RtoD = 180 / pi;

		double L1 = latitude * DtoR;

		double L2 = mLatitude * DtoR;

		double I1 = longitude * DtoR;

		double I2 = mLongitude * DtoR;

		// Distance

		double distance = Math.acos(Math.cos(L1 - L2) - (1 - Math.cos(I1 - I2)) * Math.cos(L1) * Math.cos(L2));

		// To convert this distance to nautical miles multiply by 60;

		// Then by 1.852 to convert to Kms.

		double distancKms = distance * 60 * 1.852 * RtoD;

		String dis = distancKms + "";

		String[] d = dis.split("\\.");
		if(d.length > 1)
		{
			dis = d[0].trim() + "." + d[1].trim().substring(0, 1);
		}
		else
		{
			dis = d[0] + ".0";
		}

		return dis;

	}
}
