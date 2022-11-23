import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbGlobalMap;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class LoadExternalErrorCodesToCache extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		
		HashMap <String,String> swCodeMap = new HashMap<String,String>();
		HashMap <String,String> extCodeMap = new HashMap<String,String>();
		String mapValue = "";
		String extCode, swCode, desc, xrefDtlId, xrefId, longDesc, url, retVal, cacheUrl, basicAuth, ttl = "";

		String cacheMap = (String) getUserDefinedAttribute("CacheMap");
		String cacheConfigService = (String) getUserDefinedAttribute("CacheConfigService");

		//Properties props = CacheLoadUtils.readConfigurableService("UserDefined", cacheConfigService);
		Properties props = utilities.CacheUtil.readConfigurableService();

		cacheUrl = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl") : props.getProperty("ServerUrl").concat("/");
		cacheUrl = cacheUrl.concat(cacheMap).concat("/");
		basicAuth = props.getProperty("BasicAuth");
		ttl = props.getProperty(cacheMap.concat("_TTL"));
		
		MbElement globalEnv = inAssembly.getGlobalEnvironment().getRootElement();
		MbElement advCode = globalEnv.getFirstElementByPath("./Variables/Temp/DB/AdvisoryCode");
		
		String swCacheKey = globalEnv.getFirstElementByPath("./Variables/Temp/CacheKey").getValue().toString();
		String extCacheKey = globalEnv.getFirstElementByPath("./Variables/Temp/CacheKey").getValue().toString();

		// with SW as cache key suffix
		swCacheKey = swCacheKey.concat("_SW");
		// with EXT as cache key suffix
		extCacheKey = extCacheKey.concat("_EXT");

		//MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, new MbMessage(inAssembly.getMessage()));
	
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
				
				// creating SW code map
				if(swCode != " ") {
					mapValue = extCode + "#1#" + desc + "#2#" + xrefDtlId + "#3#" + xrefId + "#4#" + "" + "__";
					swCodeMap.put(swCode, mapValue);
				}
				
				// creating EXT code map
				if(extCode != " ") {
					mapValue = swCode + "#1#" + desc + "#2#" + xrefDtlId + "#3#" + xrefId + "#4#" + "" + "__";
					extCodeMap.put(extCode, mapValue);
				}
				
				// moving to next row in the resultset
				advCode = advCode.getNextSibling(); 
			} 

			// insert SW map to cache
			url = (ttl == null ? cacheUrl.concat(swCacheKey) : cacheUrl.concat(swCacheKey).concat("?ttl=").concat(ttl));			
			//retVal = CacheLoadUtils.HTTPPostCall(url, basicAuth, swCodeMap, props.getProperty("ConnectionTimeout"));
			retVal = utilities.CacheUtil.HTTPPostCall(url, swCodeMap, props);

			//insert EXT map to cache
			url = (ttl == null ? cacheUrl.concat(extCacheKey) : cacheUrl.concat(extCacheKey).concat("?ttl=").concat(ttl));
			//retVal = CacheLoadUtils.HTTPPostCall(url, basicAuth, extCodeMap, props.getProperty("ConnectionTimeout"));
			retVal = utilities.CacheUtil.HTTPPostCall(url, extCodeMap, props);
			
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw new MbUserException(this, "evaluate()", "", "", e.getMessage(), null);
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw new MbUserException(this, "evaluate()", "", "", e.getMessage(), null);
		} catch (Exception e) {
			throw new MbUserException(this, "evaluate()", "", "", e.getMessage(), null);
		}

		//out.propagate(outAssembly);
	}
}
