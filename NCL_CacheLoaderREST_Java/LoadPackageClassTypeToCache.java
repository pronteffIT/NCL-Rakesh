
import java.util.HashMap;
import java.util.Properties;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class LoadPackageClassTypeToCache extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		
		HashMap <String,String> PackageClassMap = new HashMap<String,String>();
		
		MbElement globalEnv = inAssembly.getGlobalEnvironment().getRootElement();
		MbElement packageinfo = globalEnv.getFirstElementByPath("./Variables/Temp/DB/PackageList"); 
		String packageid, packageclass, cacheUrl, basicAuth, ttl = "";

		String cacheMap = (String) getUserDefinedAttribute("CacheMap");
	//	String cacheConfigService = (String) getUserDefinedAttribute("CacheConfigService");
		
		//Properties props = CacheLoadUtils.readConfigurableService("UserDefined", cacheConfigService);
		Properties props = utilities.CacheUtil.readConfigurableService();
		
		ttl = props.getProperty(cacheMap.concat("_TTL"));
		cacheUrl = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl") : props.getProperty("ServerUrl").concat("/");
		cacheUrl = cacheUrl.concat(cacheMap).concat("/");
		basicAuth = props.getProperty("BasicAuth");

		//MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, new MbMessage(inAssembly.getMessage()));
	
		try {
			while(packageinfo != null) {
				packageid =  packageinfo.getFirstElementByPath("PACKAGE_ID").getValue().toString();
				packageclass = (packageinfo.getFirstElementByPath("PACKAGE_CLASS_TYPE").getValue() == null) ? " " : packageinfo.getFirstElementByPath("PACKAGE_CLASS_TYPE").getValue().toString();

							
				String url = (ttl == null ? cacheUrl.concat(packageid) : cacheUrl.concat(packageid).concat("?ttl=").concat(ttl));
				String retVal = utilities.CacheUtil.HTTPPostCall(url, packageclass, props);

				// moving to next row in the result set
				packageinfo = packageinfo.getNextSibling(); 
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
