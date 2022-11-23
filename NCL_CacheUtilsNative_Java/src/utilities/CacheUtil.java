package utilities;

import java.util.HashMap;

import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbGlobalMap;
import com.ibm.broker.plugin.MbGlobalMapSessionPolicy;

public class CacheUtil {
	// ----------------  get data from XC10 ---------------------- //
	public static String getDataFromXC10Cache(String key, String cacheMap, String xc10ConnConfig) {

		Object value = "";
		MbGlobalMap xc10Map = null;

		try
		{
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConnConfig);
			value = (Object)xc10Map.get(key);
		} catch(MbException mbe) {
			value = "ERROR: ".concat(mbe.getMessage());
		} catch(Exception e) {
			value = "ERROR: ".concat(e.getMessage());
		}

		return (value==null) ? "" : value.toString();
	}

	// ----------------  save data in XC10 ---------------------- //
	public static String saveDataToXC10Cache(String key, String value, String cacheExpiry, String cacheMap, String xc10ConnConfig) {
		String retVal = "";
		MbGlobalMap xc10Map = null;

		try
		{
			//xc10Map = MbGlobalMap.getGlobalMap(httpMqStateInfoMap, xc10ConnConfig, new MbGlobalMapSessionPolicy(Integer.parseInt(cacheExpiry)));
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConnConfig);
			if(xc10Map.containsKey(key)) {
				xc10Map.update(key, value);
			} else {
				xc10Map.put(key, value);
			}
		}  catch(MbException mbe) {
			value = "ERROR: ".concat(mbe.getMessage());
		} catch(Exception e) {
			value = "ERROR: ".concat(e.getMessage());
		}

		return retVal;
	}

	// ----------------  get external error code from XC10 ---------------------- //
	@SuppressWarnings("unchecked")
	public static String getExternalErrorCodeFromXC10(String key, String errorCode, String cacheMap, String xc10ConnConfig) {
		Object value = "";
		MbGlobalMap xc10Map = null;
		HashMap <String,String> externalCode = new HashMap<String,String>();
		
		try
		{
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConnConfig);
			
			if(xc10Map != null) {
				externalCode = (HashMap<String, String>)xc10Map.get(key);
			}
			
			if(externalCode != null) {
				value = externalCode.get(errorCode);
			}
		} catch(MbException mbe) {
			value = "ERROR: ".concat(mbe.getMessage());
		} catch(Exception e) {
			value = "ERROR: ".concat(e.getMessage());
		}

		return (value==null) ? "" : value.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getExternalCodeFromXC10(String key, String swCode, String cacheMap, String xc10ConnConfig) {
		Object extCode = "";
		MbGlobalMap xc10Map = null;
		HashMap <String,String> swCodeMap = new HashMap<String,String>();
		String cacheKey = key.concat("#SW");
		
		try
		{
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConnConfig);
			
			if(xc10Map != null) {
				swCodeMap = (HashMap<String, String>)xc10Map.get(cacheKey);
			}
			
			if(swCodeMap != null) {
				extCode = swCodeMap.get(swCode);
			}
		} catch(MbException mbe) {
			extCode = "ERROR: ".concat(mbe.getMessage());
		} catch(Exception e) {
			extCode = "ERROR: ".concat(e.getMessage());
		}

		return (extCode == null) ? "" : extCode.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getSWCodeFromXC10(String key, String extCode, String cacheMap, String xc10ConnConfig) {
		Object swCode = "";
		MbGlobalMap xc10Map = null;
		HashMap <String,String> extCodeMap = new HashMap<String,String>();
		String cacheKey = key.concat("#EXT");
		
		try
		{
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConnConfig);
			
			if(xc10Map != null) {
				extCodeMap = (HashMap<String, String>)xc10Map.get(cacheKey);
			}
			
			if(extCodeMap != null) {
				swCode = extCodeMap.get(extCode);
			}
		} catch(MbException mbe) {
			swCode = "ERROR: ".concat(mbe.getMessage());
		} catch(Exception e) {
			swCode = "ERROR: ".concat(e.getMessage());
		}

		return (swCode == null) ? "" : swCode.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getSubExternalCodeFromXC10(String key, String swCode, String cacheMap, String xc10ConnConfig) {
		Object extCode = "";
		MbGlobalMap xc10Map = null;
		HashMap <String,String> swCodeMap = new HashMap<String,String>();
		String cacheKey = key.concat("#SUB#SW");
		
		try
		{
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConnConfig);
			
			if(xc10Map != null) {
				swCodeMap = (HashMap<String, String>)xc10Map.get(cacheKey);
			}
			
			if(swCodeMap != null) {
				extCode = swCodeMap.get(swCode);
			}
		} catch(MbException mbe) {
			extCode = "ERROR: ".concat(mbe.getMessage());
		} catch(Exception e) {
			extCode = "ERROR: ".concat(e.getMessage());
		}

		return (extCode == null) ? "" : extCode.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getSubSWCodeFromXC10(String key, String extCode, String cacheMap, String xc10ConnConfig) {
		Object swCode = "";
		MbGlobalMap xc10Map = null;
		HashMap <String,String> extCodeMap = new HashMap<String,String>();
		String cacheKey = key.concat("#SUB#EXT");
		
		try
		{
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConnConfig);
			
			if(xc10Map != null) {
				extCodeMap = (HashMap<String, String>)xc10Map.get(cacheKey);
			}
			
			if(extCodeMap != null) {
				swCode = extCodeMap.get(extCode);
			}
		} catch(MbException mbe) {
			swCode = "ERROR: ".concat(mbe.getMessage());
		} catch(Exception e) {
			swCode = "ERROR: ".concat(e.getMessage());
		}

		return (swCode == null) ? "" : swCode.toString();
	}

	@SuppressWarnings("unchecked")
	public static String getPortInfoFromXC10(String portCode, String cacheMap, String xc10ConnConfig) {
		Object port = "";
		MbGlobalMap xc10Map = null;
		HashMap <String,String> portMap = new HashMap<String,String>();
		String cacheKey = "PORT";
		
		try
		{
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConnConfig);
			
			if(xc10Map != null) {
				portMap = (HashMap<String, String>)xc10Map.get(cacheKey);
			}
			
			if(portMap != null) {
				port = portMap.get(portCode);
			}
		} catch(MbException mbe) {
			port = "ERROR: ".concat(mbe.getMessage());
		} catch(Exception e) {
			port = "ERROR: ".concat(e.getMessage());
		}

		return (port == null) ? "" : port.toString();
	}
}
