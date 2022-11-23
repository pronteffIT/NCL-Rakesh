import java.util.HashMap;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbGlobalMap;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class LoadPortInfoToCache extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		
		HashMap <String,String> portMap = new HashMap<String,String>();
		MbGlobalMap xc10Map = null;
		String mapValue = "";
		
		MbElement globalEnv = inAssembly.getGlobalEnvironment().getRootElement();
		MbElement port = globalEnv.getFirstElementByPath("./Variables/Temp/DB/PortList"); 
		String portCode, portName, country = "";

		//MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, new MbMessage(inAssembly.getMessage()));
	
		try {
			//while(port != null && port.getNextSibling() != null) {
			while(port != null) {
				portCode = (port.getFirstElementByPath("PORT_CODE").getValue() == null) ? " " : port.getFirstElementByPath("PORT_CODE").getValue().toString();
				portName = (port.getFirstElementByPath("PORT_NAME").getValue() == null) ? " " : port.getFirstElementByPath("PORT_NAME").getValue().toString();
				country = (port.getFirstElementByPath("COUNTRY_CODE").getValue() == null) ? " " : port.getFirstElementByPath("COUNTRY_CODE").getValue().toString();
								
				// creating SW code map
				if(portCode != " ") {
					mapValue = portName + "#" + country;
					portMap.put(portCode, mapValue);
				}
				
				// moving to next row in the resultset
				port = port.getNextSibling(); 
			} 
		   
		   String xc10ConfigService = (String) getUserDefinedAttribute("CacheConfigService");
		   String cacheMap = (String) getUserDefinedAttribute("CacheMap");
		   
		   xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConfigService);
		   
		   // insert SW map to cache
		   try {
			   if(xc10Map.containsKey("PORT")) {			   
				   xc10Map.update("PORT", portMap);
			   } else {
				   xc10Map.put("PORT", portMap);
			   }
		   } catch (MbException e) {
				// Re-throw to allow Broker handling of MbException
				if(e.getMessage().contains("A key was not found while interacting with map")) {
					xc10Map.put("PORT", portMap);
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
