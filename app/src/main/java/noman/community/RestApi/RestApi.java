package noman.community.RestApi;


import noman.community.model.AllPrayerResponse;
import noman.community.model.CountryModel;
import noman.community.model.DeletePrayerRequest;
import noman.community.model.GetAllPrayerRequest;
import noman.community.model.MoveToTopRequest;
import noman.community.model.PostPrayerRequest;
import noman.community.model.PostResponse;
import noman.community.model.PrayingRequest;
import noman.community.model.PrayingResponse;
import noman.community.model.SignInRequest;
import noman.community.model.SignUpResponse;
import noman.qurantrack.model.QuranTrackerModel;
import noman.qurantrack.model.QuranTrackerResponse;
import noman.salattrack.model.SalatModel;
import noman.salattrack.model.SalatResponse;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface RestApi {
    //For blank ip
    @GET("json")
    Call<CountryModel> getCountryName();

    /*@POST("webservice.php?action=userLogIn")
    Call<SignUpResponse> signInUser(
            @Body SignInRequest signInRequest);*/



   /* @POST("webservice.php?action=addUserPrayer")
    Call<PostResponse> postPrayer(
            @Body PostPrayerRequest mPostPrayerRequest);*/

   /* @POST("webservice.php?action=getAllPrayers")
    Call<AllPrayerResponse> getPrayers(
            @Body GetAllPrayerRequest mGetAllPrayerRequest);*/

   /* @POST("webservice.php?action=updateUserPrayerCounters")
    Call<PrayingResponse> prayForUser(
            @Body PrayingRequest mPrayingRequest);*/

   /* @POST("webservice.php?action=deleteUserPrayer")
    Call<PrayingResponse> deletePray(
            @Body DeletePrayerRequest mDeletePrayerRequest);
    @POST("webservice.php?action=updateUserPrayerDateTime")
    Call<PrayingResponse> moveToTop(
            @Body MoveToTopRequest mDeletePrayerRequest);*/

   @POST("/")
   Call<SignUpResponse> signInUser(
           @Body SignInRequest signInRequest);
    @POST("/")
    Call<AllPrayerResponse> getPrayers(
            @Body GetAllPrayerRequest mGetAllPrayerRequest);
    @POST("/")
    Call<PostResponse> postPrayer(
            @Body PostPrayerRequest mPostPrayerRequest);
    @POST("/")
    Call<PrayingResponse> prayForUser(
            @Body PrayingRequest mPrayingRequest);

    @POST("/")
    Call<PrayingResponse> deletePray(
            @Body DeletePrayerRequest mDeletePrayerRequest);
    @POST("/")
    Call<PrayingResponse> moveToTop(
            @Body MoveToTopRequest mDeletePrayerRequest);


    @POST("/")
    Call<SalatResponse> saveSalatData(
            @Body SalatModel signInRequest);


    @POST("/")
    Call<QuranTrackerResponse> saveQuranTrackerData(
            @Body QuranTrackerModel signInRequest);

}

