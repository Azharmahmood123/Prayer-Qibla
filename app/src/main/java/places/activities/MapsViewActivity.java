package places.activities;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quranreading.qibladirection.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import places.models.PlacesModel;

public class MapsViewActivity extends AppCompatActivity implements OnMapReadyCallback {

	public static final String EXTRA_PLACE_DATA = "place_data";

	private GoogleMap mMap;
	private boolean isMapLoaded = false;
	PlacesModel mPlacesModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.halal_maps_activity);

		mPlacesModel = (PlacesModel) getIntent().getSerializableExtra(EXTRA_PLACE_DATA);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		isMapLoaded = false;
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		isMapLoaded = true;
		mMap = googleMap;

		LatLng destination = new LatLng(Double.parseDouble(mPlacesModel.getLat().trim()), Double.parseDouble(mPlacesModel.getLng().trim()));

		mMap.setMyLocationEnabled(true);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 20));

		mMap.addMarker(new MarkerOptions().title(mPlacesModel.getName()).snippet(mPlacesModel.getAddress()).position(destination));
	}
}
