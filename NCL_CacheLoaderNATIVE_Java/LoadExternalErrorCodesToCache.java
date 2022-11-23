import java.util.HashMap;

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
		MbGlobalMap xc10Map = null;
		String mapValue = "";
		
		MbElement globalEnv = inAssembly.getGlobalEnvironment().getRootElement();
		MbElement advCode = globalEnv.getFirstElementByPath("./Variables/Temp/DB/AdvisoryCode"); 
		String swCacheKey = globalEnv.getFirstElementByPath("./Variables/Temp/CacheKey").getValue().toString();
		String extCacheKey = globalEnv.getFirstElementByPath("./Variables/Temp/CacheKey").getValue().toString();
		String extCode, swCode, desc, xrefDtlId, xrefId, longDesc = "";

		// with SW as cache key suffix
		swCacheKey = swCacheKey.concat("#SW");
		// with EXT as cache key suffix
		extCacheKey = extCacheKey.concat("#EXT");

		//MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, new MbMessage(inAssembly.getMessage()));
	
		try {
			//while(advCode != null && advCode.getNextSibling() != null) {
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
					mapValue = extCode + "#1#" + desc + "#2#" + xrefDtlId + "#3#" + xrefId + "#4#" + longDesc;
					swCodeMap.put(swCode, mapValue);
				}
				
				// creating EXT code map
				if(extCode != " ") {
					mapValue = swCode + "#1#" + desc + "#2#" + xrefDtlId + "#3#" + xrefId + "#4#" + longDesc;
					extCodeMap.put(extCode, mapValue);
				}
				
				// moving to next row in the resultset
				advCode = advCode.getNextSibling(); 
			} 
		   
		   String xc10ConfigService = (String) getUserDefinedAttribute("CacheConfigService");
		   String cacheMap = (String) getUserDefinedAttribute("CacheMap");
		   
		   xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConfigService);
		   
		   // insert SW map to cache
		   try {
			   if(xc10Map.containsKey(swCacheKey)) {			   
				   xc10Map.update(swCacheKey, swCodeMap);
			   } else {
				   xc10Map.put(swCacheKey, swCodeMap);
			   }
		   } catch (MbException e) {
				// Re-throw to allow Broker handling of MbException
				if(e.getMessage().contains("A key was not found while interacting with map")) {
					xc10Map.put(swCacheKey, swCodeMap);
				}
			} 

		   // insert EXT map to cache
		   try {
			   if(xc10Map.containsKey(extCacheKey)) {			   
				   xc10Map.update(extCacheKey, extCodeMap);
			   } else {
				   xc10Map.put(extCacheKey, extCodeMap);
			   }
		   } catch (MbException e) {
				// Re-throw to allow Broker handling of MbException
				if(e.getMessage().contains("A key was not found while interacting with map")) {
					xc10Map.put(extCacheKey, extCodeMap);
				}
			} 

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
