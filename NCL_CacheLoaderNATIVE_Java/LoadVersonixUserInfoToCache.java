import java.util.HashMap;

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
		
		MbGlobalMap xc10Map = null;
		
		MbElement globalEnv = inAssembly.getMessage().getRootElement();
		MbElement user = globalEnv.getFirstElementByPath("./XMLNSC/VXUserList/User"); 
		String channel, userId, pwd, xc10ConfigService, cacheMap, val = "";

		//MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, new MbMessage(inAssembly.getMessage()));
	
		try {
			xc10ConfigService = (String) getUserDefinedAttribute("CacheConfigService");
			cacheMap = (String) getUserDefinedAttribute("CacheMap");
		   
			xc10Map = MbGlobalMap.getGlobalMap(cacheMap, xc10ConfigService);

			//while(user != null && user.getNextSibling() != null) {
			while(user != null) {
				channel = (user.getFirstElementByPath("Channel").getValue() == null) ? "" : user.getFirstElementByPath("Channel").getValue().toString();
				userId = (user.getFirstElementByPath("UserName").getValue() == null) ? " " : user.getFirstElementByPath("UserName").getValue().toString();
				pwd = (user.getFirstElementByPath("Password").getValue() == null) ? " " : user.getFirstElementByPath("Password").getValue().toString();
				val = userId + ',' + pwd;
				
			   // insert user info to cache
			   try {
				   if(xc10Map.containsKey(channel)) {			   
					   xc10Map.update(channel, val);
				   } else {
					   xc10Map.put(channel, val);
				   }
			   } catch (MbException e) {
					// Re-throw to allow Broker handling of MbException
				   if(e.getMessage().contains("A key was not found while interacting with map")) {
					xc10Map.put(channel, val);
				   }
			   } 

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
