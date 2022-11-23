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

public class LoadVersonixUserInfoToCache extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		
		MbElement globalEnv = inAssembly.getMessage().getRootElement();
		MbElement user = globalEnv.getFirstElementByPath("./XMLNSC/VXUserList/User"); 
		String channel, userId, pwd, val, cacheUrl, basicAuth, ttl = "";

		String cacheMap = (String) getUserDefinedAttribute("CacheMap");
		String cacheConfigService = (String) getUserDefinedAttribute("CacheConfigService");

		//Properties props = CacheLoadUtils.readConfigurableService("UserDefined", cacheConfigService);
		Properties props = utilities.CacheUtil.readConfigurableService();

		ttl = props.getProperty(cacheMap.concat("_TTL"));
		cacheUrl = props.getProperty("ServerUrl").endsWith("/") ? props.getProperty("ServerUrl") : props.getProperty("ServerUrl").concat("/");
		cacheUrl = cacheUrl.concat(cacheMap).concat("/");
		basicAuth = props.getProperty("BasicAuth");

		//MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, new MbMessage(inAssembly.getMessage()));

		try {
			while(user != null) {
				channel = (user.getFirstElementByPath("Channel").getValue() == null) ? "" : user.getFirstElementByPath("Channel").getValue().toString();
				userId = (user.getFirstElementByPath("UserName").getValue() == null) ? " " : user.getFirstElementByPath("UserName").getValue().toString();
				pwd = (user.getFirstElementByPath("Password").getValue() == null) ? " " : user.getFirstElementByPath("Password").getValue().toString();
				val = userId + ',' + pwd;

				String url = (ttl == null ? cacheUrl.concat(channel) : cacheUrl.concat(channel).concat("?ttl=").concat(ttl));
				//String retVal = CacheLoadUtils.HTTPPostCall(url, basicAuth, val, props.getProperty("ConnectionTimeout"));
				String retVal = utilities.CacheUtil.HTTPPostCall(url, val, props);

				// moving to next row in the resultset
				user = user.getNextSibling();
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
