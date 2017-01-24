package noman.community.utility;

import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.Logger;


/**
 * Created by Erum Abid Awan on 28/03/16.
 * Van Tibolli Corp.
 * erum.abid@vantibolli.com/erumawan.21@gmail.com
 */
@SuppressWarnings("ALL")
public class DebugInfo {

    public enum Tags {
        TAG ("Progresses"),
        EXCEPTION ("Exception ::\n"),
        REQUEST ("Request ::\n"),
        RESPONSE ("Response ::\n"),
        ACTIVITY ("Activity ::\n");

        private final String name;

        private Tags(String s) {
            name = s;
        }

        public boolean equalsName(String otherName){
            return (otherName != null) && name.equals(otherName);
        }

        public String toString(){
            return name;
        }

    }

    public DebugInfo() {

    }

    /**
     * logger debug info messages
     * @param message
     */
    public static void loggerInfo(String message) {
        if (BuildConfig.DEBUG)
            Logger.i("%s %s", Tags.TAG, message);
    }
    /**
     * logger exception messages
     * @param message
     */
    public static void loggerException(String message) {
        if (BuildConfig.DEBUG)
            Logger.e("%s %s", Tags.EXCEPTION, message);
    }
    /**
     * logger REST API Request messages
     * @param message
     */
    public static void loggerApiRequest(String message) {
        if (BuildConfig.DEBUG)
            Logger.d("%s %s", Tags.REQUEST, message);
    }
    /**
     * logger REST API Response messages
     * @param message
     */
    public static void loggerApiResponse(String message) {
        if (BuildConfig.DEBUG)
            Logger.d("%s %s", Tags.REQUEST, message);
    }
    /**
     * logger REST API JSON Response messages
     * @param message
     */
    public static void loggerApiJSONResponse(String message) {
        if (BuildConfig.DEBUG)
            Logger.d("%s %s", Tags.RESPONSE, message);
    }
    /**
     * logger debug info activity messages
     * @param message
     */
    public static void loggerInfoActivity(String message) {
        if (BuildConfig.DEBUG)
            Logger.i("%s %s", Tags.ACTIVITY, message);
    }

}
