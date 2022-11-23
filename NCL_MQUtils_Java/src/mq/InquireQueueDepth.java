package mq;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class InquireQueueDepth {
    @SuppressWarnings({ "deprecation", "unchecked" })
	public static String getQueueDepth(String host, String port, String channel, String qm, String qName) {
        MQEnvironment.channel = channel;
        MQEnvironment.port = Integer.parseInt(port);
        MQEnvironment.hostname = host;
        MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);

        try {
	        MQQueueManager qmgr = new MQQueueManager(qm);
	        
	        MQQueue queue = qmgr.accessQueue(qName, MQC.MQOO_INQUIRE | MQC.MQOO_INPUT_AS_Q_DEF, null, null, null);
	        
	        return (queue == null ? "" : Integer.toString(queue.getCurrentDepth()));
        } catch(MQException mqe) {
        	return "ERROR: ".concat(mqe.getMessage());
        } catch(Exception e) {
        	return "ERROR: ".concat(e.getMessage());
        }
    }
}
