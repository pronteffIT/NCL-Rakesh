/*import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigurableService;


public class CacheLoadUtils {

	// ----------------  read configurable service ---------------------- //
	public static Properties readConfigurableService(String configType, String configName) 
	{
		BrokerProxy bp = null;
		Properties props = null;
		
		try	{
			bp = BrokerProxy.getLocalInstance();
			ConfigurableService udcs = bp.getConfigurableService(configType, configName);
			
			if (udcs != null) {
				props = udcs.getProperties();
			}
		} catch(Exception e) {
			
		} finally {
			bp.disconnect();
		}
		
		return props;
	}

	public static String HTTPPostCall(String cacheUrl, String basicAuth, HashMap <String,String> dataMap, String connTimeout) {
		String retVal = "";
		
		try {		
			Request.Post(cacheUrl)
					.addHeader("Authorization", basicAuth)
					.connectTimeout(connTimeout == null ? 5000 : Integer.parseInt(connTimeout))
					.socketTimeout(connTimeout == null ? 5000 : Integer.parseInt(connTimeout))
					.bodyString(dataMap.toString(), ContentType.DEFAULT_TEXT)
					.execute().returnContent().asString();
		} catch (Exception e) {
			retVal = "ERROR: " + e.getMessage();
		}
		
		return retVal;
	}
	
	public static String HTTPPostCall(String cacheUrl, String basicAuth, String value, String connTimeout) {
		String retVal = "";
		
		try {		
			Request.Post(cacheUrl)
					.addHeader("Authorization", basicAuth)
					.connectTimeout(connTimeout == null ? 5000 : Integer.parseInt(connTimeout))
					.socketTimeout(connTimeout == null ? 5000 : Integer.parseInt(connTimeout))
					.bodyString(value, ContentType.DEFAULT_TEXT)
					.execute().returnContent().asString();
		} catch (Exception e) {
			retVal = "ERROR: " + e.getMessage();
		}
		
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	public static String NativeHTTPPostCall(String cacheUrl, String basicAuth, HashMap <String,String> dataMap, String connTimeout) throws IOException {
		URL url = new URL(cacheUrl);
		String retVal="";
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		try {
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/xml");			
			con.setRequestProperty("Authorization", basicAuth);
			con.setConnectTimeout((connTimeout == null ? 5000 : Integer.parseInt(connTimeout)));
			con.setDoOutput(true);
			con.connect();
			
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    
            osw.write(dataMap.toString());  
            osw.close();
			os.close();
			
			if(con.getResponseCode() != 200){				
				throw new Exception(Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage());
			}	
		} catch (Exception e) {
			retVal = "ERROR: " + e.getMessage();
		} finally {
			con.disconnect();
		}
		
		return retVal;
	}

	@SuppressWarnings("unchecked")
	public static String NativeHTTPPostCall(String cacheUrl, String basicAuth, String cacheVal, String connTimeout) throws IOException {
		URL url = new URL(cacheUrl);
		String retVal="";
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		try {
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/xml");			
			con.setRequestProperty("Authorization", basicAuth);
			con.setConnectTimeout((connTimeout == null ? 5000 : Integer.parseInt(connTimeout)));
			con.setDoOutput(true);
			con.connect();
			
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");    
            osw.write(cacheVal);
            osw.close();
			os.close();
			
			if(con.getResponseCode() != 200){				
				throw new Exception(Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage());
			}	
		} catch (Exception e) {
			retVal = "ERROR: " + e.getMessage();
		} finally {
			con.disconnect();
		}
		
		return retVal;
	}
}
*/