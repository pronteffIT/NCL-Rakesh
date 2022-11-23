package utilities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

//import com.ibm.broker.config.proxy.BrokerProxy;
//import com.ibm.broker.config.proxy.ConfigurableService;
import com.ibm.broker.plugin.*;

public class CacheUtil {
	final static String CACHE_CONFIG_TYPE = "UserDefined";
	final static String CACHE_CONFIG_SERVICE = "CacheServerConnection";
//	static BrokerProxy bp = null;
	static Header[] httpHdr = null;

	// ---------------- read configurable service ---------------------- //
	public static Properties readConfigurableService() {
		// BrokerProxy bp = null;
//		Properties props = null;
		/*
		 * try { if (bp == null) { bp = BrokerProxy.getLocalInstance(); }
		 * 
		 * ConfigurableService udcs = bp.getConfigurableService(CACHE_CONFIG_TYPE,
		 * CACHE_CONFIG_SERVICE);
		 * 
		 * if (udcs != null) { props = udcs.getProperties(); } } catch(Exception e) {
		 * 
		 * }
		 */
		Set<String> propertyNames = null;
		Properties props = new Properties();
		try {
			MbPolicy myPol = MbPolicy.getPolicy(CACHE_CONFIG_TYPE, CACHE_CONFIG_SERVICE);

			if (myPol != null) {
				myPol.getProperties().forEach((mbp) -> {
					try {
						props.setProperty(mbp.name(), mbp.valueAsString());

					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				// props = myPol.getProperties();
			}
		} catch (Exception e) {

		}

		return props;
	}

	// ---------------- read configurable service ---------------------- //
	public static Properties readConfigurableService(String configServiceType, String configService) {
//		Properties props = null;

//		try	{
//			if (bp == null) {
//				bp = BrokerProxy.getLocalInstance();
//			}
//			
//			ConfigurableService udcs = bp.getConfigurableService(configServiceType, configService);
//			
//			if (udcs != null) {
//				props = udcs.getProperties();
//			}
//		} catch(Exception e) {
//			
//		}

//		return props;

		Properties props = new Properties();
		try {
			MbPolicy myPol = MbPolicy.getPolicy(CACHE_CONFIG_TYPE, CACHE_CONFIG_SERVICE);

			if (myPol != null) {
				myPol.getProperties().forEach((mbp) -> {
					try {
						props.setProperty(mbp.name(), mbp.valueAsString());

					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				// props = myPol.getProperties();
			}
		} catch (Exception e) {

		}

		return props;
	}

	// ---------------- read configurable service ---------------------- //
	public static String getPriceBookingCacheRefresh(String configService) {
		Properties props = null;

		if (configService == null || configService == "") {
			return "";
		}

		try {
			props = readConfigurableService("UserDefined", configService);
		} catch (Exception e) {
			return "ERROR: " + e.getMessage();
		}

		return (props == null) ? "" : props.getProperty("CacheRefresh").toUpperCase();
	}

	// ---------------- parse name value pairs ---------------------- //
	public static Object parseNamevalue(String cacheVal, String name) {
		Object val = null;

		if (!cacheVal.equals("") && !cacheVal.startsWith("ERROR")) {
			cacheVal = cacheVal.substring(1, cacheVal.length() - 1);
			String[] keyValuePairs = cacheVal.split(",");

			for (String pair : keyValuePairs) {
				String[] keyVal = pair.split("=");

				if (keyVal.length == 2) {
					if (name.equals(keyVal[0].trim())) {
						val = keyVal[1].trim();

						return val;
					}
				}
			}
		} else {
			val = cacheVal;
		}

		return val;
	}

	// ---------------- parse name value pairs ---------------------- //
	public static Object parseExtCodeNamevalue(String cacheVal, String name) {
		Object val = null;
		String key = "";

		if (!cacheVal.equals("") && !cacheVal.startsWith("ERROR")) {
			cacheVal = cacheVal.substring(1, cacheVal.length() - 1);
			String[] keyValuePairs = cacheVal.split("__");

			for (String pair : keyValuePairs) {
				String[] keyVal = pair.split("=");

				if (keyVal.length == 2) {
					key = keyVal[0].replace(",", "").trim();

					if (name.equals(key)) {
						val = keyVal[1].trim();

						return val;
					}
				}
			}
		} else {
			val = cacheVal;
		}

		return val;
	}

	// ---------------- get data from XC10 ---------------------- //
	public static String getDataFromXC10Cache(String key, String cacheMap, String xc10ConnConfig) {
		String value, URL = "";

		if (key == null || key == "") {
			return "";
		}

		try {
			Properties props = readConfigurableService();
			URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = URL.concat(cacheMap).concat("/").concat(key);

			value = (String) HTTPGetCall(URL, props);
		} catch (Exception e) {
			value = "ERROR: " + e.getMessage();
		}

		return (value == null) ? "" : value.toString();
	}

	// ---------------- save data in XC10 ---------------------- //
	public static String saveDataToXC10Cache(String key, String value, String cacheExpiry, String cacheMap,
			String xc10ConnConfig) {
		String retVal, URL, ttl = "";

		if (key == null || key == "") {
			return "";
		}

		try {
			Properties props = readConfigurableService();
			ttl = props.getProperty(cacheMap.concat("_TTL"));
			URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = (ttl == null ? URL.concat(cacheMap).concat("/").concat(key)
					: URL.concat(cacheMap).concat("/").concat(key).concat("?ttl=").concat(ttl));

			retVal = HTTPPostCall(URL, value, props);

			if (retVal != "") {
				throw new Exception(retVal);
			}
		} catch (Exception e) {
			retVal = "ERROR: " + e.getMessage();
		}

		return retVal;
	}

	// ---------------- delete data from cache ---------------------- //
	public static String deleteEntriesFromCache(String key, String cacheMap, String xc10ConnConfig) {
		String value, URL = "";

		if (key == null || key == "") {
			return "";
		}

		try {
			Properties props = readConfigurableService();
			URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = URL.concat(cacheMap).concat("/").concat(key);

			value = (String) HTTPDelete(URL, props);
		} catch (Exception e) {
			value = "ERROR: " + e.getMessage();
		}

		return (value == null) ? "" : value.toString();
	}

	// ---------------- get external error code from XC10 ---------------------- //
	@SuppressWarnings("unchecked")
	public static String getExternalErrorCodeFromXC10(String key, String errorCode, String cacheMap,
			String xc10ConnConfig) {
		Object value = "";

		if (key == null || errorCode == null) {
			return "";
		}

		try {
			Properties props = readConfigurableService();
			String URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = URL.concat(cacheMap).concat("/").concat(key.replace("#", "_"));

			String cacheVal = (String) HTTPGetCall(URL, props);
			value = parseExtCodeNamevalue(cacheVal, errorCode);
		} catch (Exception e) {
			value = "ERROR: " + e.getMessage();
		}

		return (value == null) ? "" : value.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getExternalCodeFromXC10(String key, String swCode, String cacheMap, String xc10ConnConfig) {
		if (key == null || swCode == null) {
			return "";
		}

		Object extCode = "";
		String cacheKey = key.replace("#", "_").concat("_SW");

		try {
			Properties props = readConfigurableService();
			String URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = URL.concat(cacheMap).concat("/").concat(cacheKey);

			String cacheVal = (String) HTTPGetCall(URL, props);

			if (cacheVal != null) {
				extCode = parseExtCodeNamevalue(cacheVal, swCode);
			}
		} catch (Exception e) {
			extCode = "ERROR: " + e.getMessage();
		}

		return (extCode == null) ? "" : extCode.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getSWCodeFromXC10(String key, String extCode, String cacheMap, String xc10ConnConfig) {
		if (key == null || extCode == null) {
			return "";
		}

		Object swCode = "";
		String cacheKey = key.replace("#", "_").concat("_EXT");

		try {
			Properties props = readConfigurableService();
			String URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = URL.concat(cacheMap).concat("/").concat(cacheKey);

			String cacheVal = (String) HTTPGetCall(URL, props);

			if (cacheVal != null) {
				swCode = parseExtCodeNamevalue(cacheVal, extCode);
			}
		} catch (Exception e) {
			swCode = "ERROR: " + e.getMessage();
		}

		return (swCode == null) ? "" : swCode.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getSubExternalCodeFromXC10(String key, String swCode, String cacheMap, String xc10ConnConfig) {
		if (key == null || swCode == null) {
			return "";
		}

		Object extCode = "";
		String cacheKey = key.replace("#", "_").concat("_SUB_SW");

		try {
			Properties props = readConfigurableService();
			String URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = URL.concat(cacheMap).concat("/").concat(cacheKey);

			String cacheVal = (String) HTTPGetCall(URL, props);

			if (cacheVal != null) {
				extCode = parseExtCodeNamevalue(cacheVal, swCode);
			}
		} catch (Exception e) {
			extCode = "ERROR: " + e.getMessage();
		}

		return (extCode == null) ? "" : extCode.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getSubSWCodeFromXC10(String key, String extCode, String cacheMap, String xc10ConnConfig) {
		if (key == null || extCode == null) {
			return "";
		}

		Object swCode = "";
		String cacheKey = key.replace("#", "_").concat("_SUB_EXT");

		try {
			Properties props = readConfigurableService();
			String URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = URL.concat(cacheMap).concat("/").concat(cacheKey);

			String cacheVal = (String) HTTPGetCall(URL, props);

			if (cacheVal != null) {
				swCode = parseExtCodeNamevalue(cacheVal, extCode);
			}
		} catch (Exception e) {
			swCode = "ERROR: " + e.getMessage();
		}

		return (swCode == null) ? "" : swCode.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getPortInfoFromXC10(String portCode, String cacheMap, String xc10ConnConfig) {
		if (portCode == null || portCode == "") {
			return "";
		}

		Object port = "";
		String cacheKey = "PORT";

		try {
			Properties props = readConfigurableService();
			String URL = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl")
					: props.getProperty("ServerUrl").concat("/");
			URL = URL.concat(cacheMap).concat("/").concat(cacheKey);

			String cacheVal = (String) HTTPGetCall(URL, props);

			if (cacheVal != null) {
				port = parseNamevalue(cacheVal, portCode);
			}
		} catch (Exception e) {
			port = "ERROR: " + e.getMessage();
		}

		return (port == null) ? "" : port.toString();
	}

	public static Object HTTPGetCall(String cacheUrl, Properties props) throws ParseException {
		String basicAuth = props.getProperty("BasicAuth");
		String connTimeout = props.getProperty("ConnectionTimeout");

		Object value = null;

		RequestConfig connConfig = RequestConfig.custom().setConnectTimeout(Integer.parseInt(connTimeout))
				.setSocketTimeout(Integer.parseInt(connTimeout)).build();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(connConfig).build();

		HttpGet httpGet = new HttpGet(cacheUrl);
		httpGet.addHeader("Authorization", basicAuth);
		httpGet.addHeader("Content-Type", "application/xml");

		if (httpHdr != null) {
			for (Header hdr : httpHdr) {
				if (hdr.getValue().toString().startsWith("zsessionid")) {
					httpGet.addHeader("Cookie", hdr.getValue().toString().split(";")[0]);
				}
			}
		}

		CloseableHttpResponse httpResp = null;

		try {
			httpResp = httpClient.execute(httpGet);

			if (httpResp.getHeaders("Set-Cookie").length > 0) {
				httpHdr = httpResp.getHeaders("Set-Cookie");
			}

			HttpEntity entity = httpResp.getEntity();

			if (httpResp.getStatusLine().getStatusCode() == 200) {
				if (entity != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String temp;

					while ((temp = br.readLine()) != null) {
						sb.append(temp);
					}

					value = sb.toString();
				}
			} else if (httpResp.getStatusLine().getStatusCode() == 404) {
				value = null;
			} else {
				throw new Exception(Integer.toString(httpResp.getStatusLine().getStatusCode()) + " "
						+ httpResp.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			value = ("ERROR: " + e.getMessage());
		} finally {
			try {
				if (httpResp != null) {
					httpResp.close();
				}

				httpClient.close();
			} catch (IOException e) {
				value = ("ERROR: " + e.getMessage());
			}
		}

		return value;
	}

	public static String HTTPPostCall(String cacheUrl, String value, Properties props) throws ParseException {
		String basicAuth = props.getProperty("BasicAuth");
		String connTimeout = props.getProperty("ConnectionTimeout");

		String retVal = "";

		RequestConfig connConfig = RequestConfig.custom().setConnectTimeout(Integer.parseInt(connTimeout))
				.setSocketTimeout(Integer.parseInt(connTimeout)).build();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(connConfig).build();

		HttpPost httpPost = new HttpPost(cacheUrl);
		httpPost.addHeader("Authorization", basicAuth);
		httpPost.addHeader("Content-Type", "application/xml");

		if (httpHdr != null) {
			for (Header hdr : httpHdr) {
				if (hdr.getValue().toString().startsWith("zsessionid")) {
					httpPost.addHeader("Cookie", hdr.getValue().toString().split(";")[0]);
				}
			}
		}

		CloseableHttpResponse httpResp = null;

		try {
			httpPost.setEntity(new StringEntity(value));

			httpResp = httpClient.execute(httpPost);

			if (httpResp.getHeaders("Set-Cookie").length > 0) {
				httpHdr = httpResp.getHeaders("Set-Cookie");
			}

			if (httpResp.getStatusLine().getStatusCode() != 200) {
				throw new Exception(Integer.toString(httpResp.getStatusLine().getStatusCode()) + " "
						+ httpResp.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			retVal = ("ERROR: " + e.getMessage());
		} finally {
			try {
				if (httpResp != null) {
					httpResp.close();
				}

				httpClient.close();
			} catch (IOException e) {
				retVal = ("ERROR: " + e.getMessage());
			}
		}

		return retVal;
	}

	// HTTP post function overloading
	public static String HTTPPostCall(String cacheUrl, HashMap<String, String> dataMap, Properties props)
			throws ParseException {
		String basicAuth = props.getProperty("BasicAuth");
		String connTimeout = props.getProperty("ConnectionTimeout");

		String retVal = "";

		RequestConfig connConfig = RequestConfig.custom().setConnectTimeout(Integer.parseInt(connTimeout))
				.setSocketTimeout(Integer.parseInt(connTimeout)).build();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(connConfig).build();

		HttpPost httpPost = new HttpPost(cacheUrl);
		httpPost.addHeader("Authorization", basicAuth);
		httpPost.addHeader("Content-Type", "application/xml");

		if (httpHdr != null) {
			for (Header hdr : httpHdr) {
				if (hdr.getValue().toString().startsWith("zsessionid")) {
					httpPost.addHeader("Cookie", hdr.getValue().toString().split(";")[0]);
				}
			}
		}

		CloseableHttpResponse httpResp = null;

		try {
			httpPost.setEntity(new StringEntity(dataMap.toString()));

			httpResp = httpClient.execute(httpPost);

			if (httpResp.getHeaders("Set-Cookie").length > 0) {
				httpHdr = httpResp.getHeaders("Set-Cookie");
			}

			if (httpResp.getStatusLine().getStatusCode() != 200) {
				throw new Exception(Integer.toString(httpResp.getStatusLine().getStatusCode()) + " "
						+ httpResp.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			retVal = ("ERROR: " + e.getMessage());
		} finally {
			try {
				if (httpResp != null) {
					httpResp.close();
				}

				httpClient.close();
			} catch (IOException e) {
				retVal = ("ERROR: " + e.getMessage());
			}
		}

		return retVal;
	}

	// HTTP delete
	public static String HTTPDelete(String cacheUrl, Properties props) throws ParseException {
		String basicAuth = props.getProperty("BasicAuth");
		String connTimeout = props.getProperty("ConnectionTimeout");

		String retVal = "";

		RequestConfig connConfig = RequestConfig.custom().setConnectTimeout(Integer.parseInt(connTimeout))
				.setSocketTimeout(Integer.parseInt(connTimeout)).build();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(connConfig).build();

		HttpDelete httpDel = new HttpDelete(cacheUrl);
		httpDel.addHeader("Authorization", basicAuth);
		httpDel.addHeader("Content-Type", "application/xml");

		if (httpHdr != null) {
			for (Header hdr : httpHdr) {
				if (hdr.getValue().toString().startsWith("zsessionid")) {
					httpDel.addHeader("Cookie", hdr.getValue().toString().split(";")[0]);
				}
			}
		}

		CloseableHttpResponse httpResp = null;

		try {
			httpResp = httpClient.execute(httpDel);

			if (httpResp.getHeaders("Set-Cookie").length > 0) {
				httpHdr = httpResp.getHeaders("Set-Cookie");
			}

			if (httpResp.getStatusLine().getStatusCode() != 200) {
				throw new Exception(Integer.toString(httpResp.getStatusLine().getStatusCode()) + " "
						+ httpResp.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			retVal = ("ERROR: " + e.getMessage());
		} finally {
			try {
				if (httpResp != null) {
					httpResp.close();
				}

				httpClient.close();
			} catch (IOException e) {
				retVal = ("ERROR: " + e.getMessage());
			}
		}

		return retVal;
	}

	public static Object FluentHTTPGetCall(String cacheUrl, String basicAuth, String connTimeout) {
		Object value;

		try {
			Response response = Request.Get(cacheUrl).addHeader("Authorization", basicAuth)
					.connectTimeout(connTimeout == null ? 5000 : Integer.parseInt(connTimeout))
					.socketTimeout(connTimeout == null ? 5000 : Integer.parseInt(connTimeout)).execute();

			HttpResponse httpResp = response.returnResponse();

			if (httpResp.getStatusLine().getStatusCode() == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader((httpResp.getEntity().getContent())));
				StringBuilder sb = new StringBuilder();
				String temp;

				while ((temp = br.readLine()) != null) {
					sb.append(temp);
				}

				value = sb.toString();
				br.close();
			} else if (httpResp.getStatusLine().getStatusCode() == 404) {
				value = null;
			} else {
				throw new Exception(Integer.toString(httpResp.getStatusLine().getStatusCode()) + " "
						+ httpResp.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			value = "ERROR: " + e.getMessage();
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public static Object NativeHTTPGetCall(String cacheUrl, String basicAuth, String connTimeout) throws IOException {
		URL url = new URL(cacheUrl);
		Object value;
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		try {
			con.setRequestMethod("GET");
			// con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("Authorization", basicAuth);
			con.setConnectTimeout((connTimeout == null ? 5000 : Integer.parseInt(connTimeout)));
			con.connect();

			if (con.getResponseCode() == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
				StringBuilder sb = new StringBuilder();
				String temp;

				while ((temp = br.readLine()) != null) {
					sb.append(temp);
				}

				value = sb.toString();
			} else if (con.getResponseCode() == 404) {
				value = null;
			} else {
				throw new Exception(Integer.toString(con.getResponseCode()) + " " + con.getResponseMessage());
			}
		} catch (Exception e) {
			value = "ERROR: " + e.getMessage();
		} finally {
			con.disconnect();
		}

		return value;
	}

	@SuppressWarnings("unchecked")
	public static String NativeHTTPPostCall(String cacheUrl, String basicAuth, String value, String connTimeout)
			throws IOException {
		// append "?ttl=<integer>" for setting cache expiry as shown in below url
		// http://myxc10.ibm.com/resources/datacaches/MyDataGrid/MyMap.LUT/a.key?ttl=600
		URL url = new URL(cacheUrl);
		String retVal = "";
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
			osw.write(value);
			osw.close();
			os.close();

			if (con.getResponseCode() != 200) {
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
