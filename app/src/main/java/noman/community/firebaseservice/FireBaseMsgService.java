package noman.community.firebaseservice;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.quranreading.qibladirection.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import noman.CommunityGlobalClass;
import noman.community.pushnotification.NotificationUtils;

public class FireBaseMsgService extends FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            CommunityGlobalClass.mMainActivityNew.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        handleDataMessage(new JSONObject(remoteMessage.getData().toString()));
                    } catch (JSONException e) {
                        Log.e(TAG, "exception message service fcm: " + e);
                    }
                }
            });
        }
    }
    private void handleDataMessage(final JSONObject data) {
        String keys[] = getResources().getStringArray(R.array.keys_fcm);
        Iterator<String> iterator = data.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            for (int i = 0; i < keys.length; i++) {
                    if (key.equals(keys[i])) {
                        Log.e("TAG","key:"+key +"  --  Value::"+data.optString(key));
                       // CommunityGlobalClass.mMainActivityNew.handleNotificaiton(data.optString(key));
                    }
                }
        }
    }



}
