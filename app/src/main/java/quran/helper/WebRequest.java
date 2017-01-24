package quran.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WebRequest {
	static String response = null;
	public final static int GETRequest = 1;

	public WebRequest() {
		// TODO Auto-generated constructor stub
	}

	public String makeWebServiceCall(String urladdress) {
		URL url;
		String response = "";
		try
		{
			url = new URL(urladdress);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(15001);
			conn.setConnectTimeout(15001);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			int reqresponseCode = conn.getResponseCode();

			if(reqresponseCode == HttpsURLConnection.HTTP_OK)
			{
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = br.readLine()) != null)
				{
					response += line;
				}
			}
			else
			{
				response = "";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return response;
	}
}
