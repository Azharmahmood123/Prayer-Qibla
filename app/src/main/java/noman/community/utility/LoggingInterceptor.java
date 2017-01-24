package noman.community.utility;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import okio.Buffer;

/**
 * Created by Erum Abid Awan on 28/03/16.
 * Van Tibolli Corp.
 * erum.abid@vantibolli.com/erumawan.21@gmail.com
 */
public class LoggingInterceptor implements Interceptor {
    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            DebugInfo.loggerInfo(copy.headers().toString());

            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            String exe = "IOException failed ::" + e.getMessage();
            DebugInfo.loggerException(exe);
            return exe;
        }
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        String requestLog = String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers());
        if(request.method().compareToIgnoreCase("post")==0){
            requestLog = "\n" + requestLog + "\n";
            DebugInfo.loggerApiRequest(requestLog);
            DebugInfo.loggerApiRequest(bodyToString(request));
        }
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        String responseLog = String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers());

        DebugInfo.loggerInfo(responseLog);

        String bodyString = response.body().string();

        DebugInfo.loggerApiResponse(responseLog);
        DebugInfo.loggerApiJSONResponse(requestLog);

        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), bodyString))
                .build();
    }
}
