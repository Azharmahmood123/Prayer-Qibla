package places.webtask;

import java.util.ArrayList;

import places.models.PlacesModel;

public interface OnPlacesLoadedListner {

	public void onPlacesLoaded(ArrayList<PlacesModel> mPlacesList);

}
