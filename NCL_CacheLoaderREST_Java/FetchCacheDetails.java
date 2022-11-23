import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Properties;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class FetchCacheDetails extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		
		HashMap <String,String> swCodeMap = new HashMap<String,String>();
		HashMap <String,String> extCodeMap = new HashMap<String,String>();
		String mapValue = "";
		String extCode, swCode, desc, xrefDtlId, xrefId, longDesc, url, retVal, cacheUrl, basicAuth,tableName,channelName,ttl = "";

		String cacheMap = (String) getUserDefinedAttribute("CacheMap");
		String cacheConfigService = (String) getUserDefinedAttribute("CacheConfigService");

		//Properties props = CacheLoadUtils.readConfigurableService("UserDefined", cacheConfigService);
		Properties props = utilities.CacheUtil.readConfigurableService();

		cacheUrl = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl") : props.getProperty("ServerUrl").concat("/");
		cacheUrl = cacheUrl.concat(cacheMap).concat("/");
		basicAuth = props.getProperty("BasicAuth");
		ttl = props.getProperty(cacheMap.concat("_TTL"));
		
		MbElement globalEnv = inAssembly.getGlobalEnvironment().getRootElement(); 
		MbElement advCode = globalEnv.getFirstElementByPath("./Variables/Temp/DB").getFirstChild();
		String CodeType = advCode.getName();
		//MbElement advCode = globalEnv.getFirstElementByPath("./Variables/Temp/DB/");//.getFirstChild();
		//String swCacheKey = globalEnv.getFirstElementByPath("./Variables/Temp/CacheKey").getValue().toString();
		//String extCacheKey = globalEnv.getFirstElementByPath("./Variables/Temp/CacheKey").getValue().toString();

		// with SW as cache key suffix
		//swCacheKey = swCacheKey.concat("_SW");
		// with EXT as cache key suffix
		//extCacheKey = extCacheKey.concat("_EXT");

		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, new MbMessage(inAssembly.getMessage()));
	
		try {
			while(advCode != null) {
				swCode = (advCode.getFirstElementByPath("SW_CODE").getValue() == null) ? " " : 
								advCode.getFirstElementByPath("SW_CODE").getValue().toString();
				extCode = (advCode.getFirstElementByPath("EXTERNAL_CODE").getValue() == null) ? " " : 
								advCode.getFirstElementByPath("EXTERNAL_CODE").getValue().toString();
				desc = (advCode.getFirstElementByPath("DESCRIPTION").getValue() == null) ? " " : 
								advCode.getFirstElementByPath("DESCRIPTION").getValue().toString();
				xrefDtlId = (advCode.getFirstElementByPath("EXTERNAL_XREF_DETAIL_ID").getValue() == null) ? " " : 
								advCode.getFirstElementByPath("EXTERNAL_XREF_DETAIL_ID").getValue().toString();
				xrefId = (advCode.getFirstElementByPath("EXTERNAL_XREF_ID").getValue() == null) ? " " : 
								advCode.getFirstElementByPath("EXTERNAL_XREF_ID").getValue().toString();
				longDesc = (advCode.getFirstElementByPath("LONG_DESCRIPTION").getValue() == null) ? " " : 
					advCode.getFirstElementByPath("LONG_DESCRIPTION").getValue().toString();
				tableName = (advCode.getFirstElementByPath("EXTERNAL_TABLE").getValue() == null) ? " " : 
					advCode.getFirstElementByPath("EXTERNAL_TABLE").getValue().toString();
				channelName = (advCode.getFirstElementByPath("EXTERNAL_NAME").getValue() == null) ? " " : 
					advCode.getFirstElementByPath("EXTERNAL_NAME").getValue().toString();
				
				
				// with EXT as cache key suffix
				
				if(swCode != " ") {
					//mapValue = extCode + "," + desc + ","  + xrefDtlId + ","  + xrefId + ","  + longDesc;
					String KeyName = CodeType.contentEquals("SubCodes")? "NCLIIB_" +channelName+"_"+tableName+"_SUB_SW_"+swCode
																				:"NCLIIB_" +channelName+"_"+tableName+"_SW_"+swCode ;
					mapValue = extCode + "#1#" + desc + "#2#" + xrefDtlId + "#3#" + xrefId + "#4#" + "";
					//swCodeMap.put(swCode, mapValue);
					// insert SW map to cache
					url = (ttl == null ? cacheUrl.concat(KeyName) : cacheUrl.concat(encodeValue(KeyName)).concat("?ttl=").concat(ttl));	
					globalEnv.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "url", url);
					globalEnv.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "cacheValue", mapValue);
					out.propagate(outAssembly);
					
/*					//retVal = CacheLoadUtils.HTTPPostCall(url, basicAuth, swCodeMap, props.getProperty("ConnectionTimeout"));
					try 
					{
					retVal = utilities.CacheUtil.HTTPPostCall(url, mapValue, props);
					}
					catch(Exception e)
					{?
						
					}*/
					// creating SW code map
				}
				
				if(extCode != " ") {
					//mapValue = extCode + "," + desc + ","  + xrefDtlId + ","  + xrefId + ","  + longDesc;
					String KeyName = CodeType.contentEquals("SubCodes")? "NCLIIB_" +channelName+"_"+tableName+"_SUB_EXT_"+extCode
							:"NCLIIB_" +channelName+"_"+tableName+"_EXT_"+extCode ;
					mapValue = swCode + "#1#" + desc + "#2#" + xrefDtlId + "#3#" + xrefId + "#4#" + "";
					//swCodeMap.put(swCode, mapValue);
					// insert SW map to cache
					url = (ttl == null ? cacheUrl.concat(KeyName) : cacheUrl.concat(encodeValue(KeyName)).concat("?ttl=").concat(ttl));	
					globalEnv.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "url", url);
					globalEnv.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "cacheValue", mapValue);
					out.propagate(outAssembly);
					
/*					//retVal = CacheLoadUtils.HTTPPostCall(url, basicAuth, swCodeMap, props.getProperty("ConnectionTimeout"));
					try 
					{
					retVal = utilities.CacheUtil.HTTPPostCall(url, mapValue, props);
					}
					catch(Exception e)
					{
						
					}*/
					// creating SW code map
				}
				
				
				// creating EXT code map
			/*	if(extCode != " ") {
					mapValue = swCode + "#1#" + desc + "#2#" + xrefDtlId + "#3#" + xrefId + "#4#" + longDesc + "__";
					extCodeMap.put(extCode, mapValue);
				}*/
				
				// moving to next row in the resultset
				advCode = advCode.getNextSibling(); 
			} 

			// insert SW map to cache
			//url = (ttl == null ? cacheUrl.concat(swCacheKey) : cacheUrl.concat(swCacheKey).concat("?ttl=").concat(ttl));			
			//retVal = CacheLoadUtils.HTTPPostCall(url, basicAuth, swCodeMap, props.getProperty("ConnectionTimeout"));
			//retVal = utilities.CacheUtil.HTTPPostCall(url, swCodeMap, props);

			//insert EXT map to cache
			//url = (ttl == null ? cacheUrl.concat(extCacheKey) : cacheUrl.concat(extCacheKey).concat("?ttl=").concat(ttl));
			//retVal = CacheLoadUtils.HTTPPostCall(url, basicAuth, extCodeMap, props.getProperty("ConnectionTimeout"));
			//retVal = utilities.CacheUtil.HTTPPostCall(url, extCodeMap, props);
			
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw new MbUserException(this, "evaluate()", "", "", e.getMessage(), null);
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw new MbUserException(this, "evaluate()", "", "", e.getMessage(), null);
		} catch (Exception e) {
			throw new MbUserException(this, "evaluate()", "", "", e.getMessage(), null);
		}

	
	}
	
	private static String encodeValue(String value) {
		String retVal="";
		try 
		{
		  retVal = URLEncoder.encode(
	    	    value,
	    	    java.nio.charset.StandardCharsets.UTF_8.toString());
		}
		catch (Exception e) {
			retVal = "";
	}
	
		return retVal;
	}

}
