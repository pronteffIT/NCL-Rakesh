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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadPortInfoToCache extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		
		HashMap <String,String> portMap = new HashMap<String,String>();
		String mapValue = "";
		
		MbElement globalEnv = inAssembly.getGlobalEnvironment().getRootElement();
		MbElement port = globalEnv.getFirstElementByPath("./Variables/Temp/DB/PortList"); 
		String portCode, portName, country, cacheUrl, basicAuth, ttl = "";

		String cacheMap = (String) getUserDefinedAttribute("CacheMap");
		String cacheConfigService = (String) getUserDefinedAttribute("CacheConfigService");
		
		//Properties props = CacheLoadUtils.readConfigurableService("UserDefined", cacheConfigService);
		Properties props = utilities.CacheUtil.readConfigurableService();
		
		ttl = props.getProperty(cacheMap.concat("_TTL"));
		cacheUrl = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl") : props.getProperty("ServerUrl").concat("/");
		cacheUrl = (ttl == null ? cacheUrl.concat(cacheMap).concat("/").concat("PORT") : cacheUrl.concat(cacheMap).concat("/").concat("PORT").concat("?ttl=").concat(ttl));
		basicAuth = props.getProperty("BasicAuth");

		//MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, new MbMessage(inAssembly.getMessage()));
	
		try {
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
		   
			//String retVal = CacheLoadUtils.HTTPPostCall(cacheUrl, basicAuth, portMap, props.getProperty("ConnectionTimeout"));
			String retVal = utilities.CacheUtil.HTTPPostCall(cacheUrl, portMap, props);
			
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
