package com.compomics.pladipus.queue.impl;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.model.queue.MessageSelector;
import com.compomics.pladipus.queue.QueueMessageController;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class QueueMessageControllerImpl implements QueueMessageController {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private JMXConnector connection;
	private ObjectName objectName;
	private String url;
	private String queueName;
	private final String queueObjectName = "org.apache.activemq:type=Broker,brokerName=%s,destinationType=Queue,destinationName=%s";
	
	public QueueMessageControllerImpl(String url, String brokerName, String queueName) throws MalformedObjectNameException {
		this.url = url;
		this.queueName = queueName;
		objectName = new ObjectName(String.format(queueObjectName, brokerName, queueName));
	}

	@Override
	public void removeMessagesByIdentifier(String identifier, long timestamp) throws PladipusReportableException {
		QueueViewMBean queueViewMBean = getMBean();
		if (queueViewMBean != null)	{
			try {
				String selector = MessageSelector.JMX_IDENTIFIER.name() + "='" + identifier + "'";
				if (timestamp > 0) {
					selector += " AND " + MessageSelector.STATUS_TIMESTAMP + " < " + timestamp;
				}
				queueViewMBean.removeMatchingMessages(selector);
			} catch (Exception e) {
				// TODO on exception try to restart ActiveMQ.  Need keep-alive check on connection?  Regular log of queue sizes perhaps?
				throw new PladipusReportableException(exceptionMessages.getMessage("queue.removeFail", queueName, identifier));
			}
		} else {
			throw new PladipusReportableException(exceptionMessages.getMessage("queue.removeFail", queueName, identifier));
		}
	}
	
	private QueueViewMBean getMBean() {
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<Boolean> future = es.submit(new Connection());
		boolean connected = false;
		try {
			connected = future.get(10000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {

		} finally {
			es.shutdown();
		}
		
		if (connected) {
			try {
				return JMX.newMBeanProxy(connection.getMBeanServerConnection(), objectName, QueueViewMBean.class);
			} catch (IOException e) {
				
			}
		}
		return null;
	}
	
	class Connection implements Callable<Boolean> {

		@Override
		public Boolean call() throws Exception {
			if (!checkConnection()) {
				connection = JMXConnectorFactory.connect(new JMXServiceURL(url));
			}
			return true;
		}
		
		private boolean checkConnection() {
			if (connection != null) {
				try {
					// Check connection hasn't been closed
					connection.getConnectionId();
					return true;
				} catch (IOException e) {}
			}
			return false;
		}
	}
	

}