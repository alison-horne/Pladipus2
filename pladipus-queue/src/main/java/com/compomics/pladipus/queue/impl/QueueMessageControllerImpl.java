package com.compomics.pladipus.queue.impl;

import javax.management.JMX;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;

import com.compomics.pladipus.model.queue.MessageSelector;
import com.compomics.pladipus.queue.QueueMessageController;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public class QueueMessageControllerImpl implements QueueMessageController {
	
	@Autowired
	MBeanServerConnectionFactoryBean jmxConnection;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private ObjectName objectName;
	private String queueName;
	private final String queueObjectName = "org.apache.activemq:type=Broker,brokerName=%s,destinationType=Queue,destinationName=%s";
	
	public QueueMessageControllerImpl(String brokerName, String queueName) throws MalformedObjectNameException {
		this.queueName = queueName;
		objectName = new ObjectName(String.format(queueObjectName, brokerName, queueName));
	}

	@Override
	public void removeMessagesByIdentifier(String identifier) throws PladipusReportableException {
		QueueViewMBean queueViewMBean = JMX.newMBeanProxy(jmxConnection.getObject(), objectName, QueueViewMBean.class);
		try {
			queueViewMBean.removeMatchingMessages(MessageSelector.JMX_IDENTIFIER.name() + "='" + identifier + "'");
		} catch (Exception e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("queue.removeFail", queueName, identifier));
		}
	}
}