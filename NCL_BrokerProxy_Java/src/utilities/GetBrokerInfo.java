package utilities;

import java.util.Properties;

import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;

public class GetBrokerInfo {
	static BrokerProxy bp = null;
	
	public static String readBrokerPort() {
		String port = "";
		
		try	{
			if (bp == null) {
				bp = BrokerProxy.getLocalInstance();
			}
			
			Properties p = bp.getProperties();
			port = p.getProperty("BrokerRegistryProperty/BrokerRegistry/WebAdmin/HTTPConnector/port");
		
			return (port == null ? "" : port);
		} catch(ConfigManagerProxyLoggedException le) {
			return "ERROR: ".concat(le.getMessage());
		} catch(Exception e) {
			return "ERROR: ".concat(e.getMessage());
		}
	}
}
