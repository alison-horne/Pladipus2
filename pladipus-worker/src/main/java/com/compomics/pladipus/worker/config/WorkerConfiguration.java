package com.compomics.pladipus.worker.config;

import java.util.UUID;

import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.compomics.pladipus.model.queue.MessageSelector;
import com.compomics.pladipus.model.queue.Prerequisite;
import com.compomics.pladipus.tools.config.ToolsConfiguration;
import com.compomics.pladipus.worker.MessageProducer;
import com.compomics.pladipus.worker.QueueProcessor;
import com.compomics.pladipus.worker.impl.WorkerDirectListener;
import com.compomics.pladipus.worker.impl.WorkerProducer;
import com.compomics.pladipus.worker.impl.WorkerTaskListener;
import com.compomics.pladipus.worker.impl.WorkerTaskQueueProcessor;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Import(ToolsConfiguration.class)
@PropertySource({"classpath:application.properties", "classpath:queue.properties"})
public class WorkerConfiguration {
	
	@Autowired
	private Environment env;

	@Bean
	public ActiveMQConnectionFactory amqConnectionFactory() {
		ActiveMQConnectionFactory factory = 
				new ActiveMQConnectionFactory("tcp://" + env.getRequiredProperty("amq.host") + ":" + env.getRequiredProperty("amq.queue.port"));
		ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
		prefetchPolicy.setAll(1);
		factory.setPrefetchPolicy(prefetchPolicy);
		return factory;
	}

	@Bean
	public CachingConnectionFactory cacheConnectionFactory() {
		return new CachingConnectionFactory(amqConnectionFactory());
	}

	@Bean
	public JmsTemplate jmsTemplate() {
	    JmsTemplate temp = new JmsTemplate();
		temp.setConnectionFactory(cacheConnectionFactory());
		temp.setDefaultDestination(new ActiveMQQueue(env.getRequiredProperty("queue.fromWorkers")));
		return temp;
	}
	
	@Bean
	public DefaultMessageListenerContainer taskListenerContainer() {
		DefaultMessageListenerContainer cont = new DefaultMessageListenerContainer();
		cont.setMessageListener(workerTaskListener());
		cont.setConnectionFactory(amqConnectionFactory());
		cont.setDestination(new ActiveMQQueue(env.getRequiredProperty("queue.toWorkers")));
		cont.setMessageSelector(getOsType().name() + "=true AND " + 
								MessageSelector.FAILED_WORKERS.name() + " NOT LIKE '%" + workerId() + "%'");
		return cont;
	}
	
	@Bean
	public DefaultMessageListenerContainer directListenerContainer() {
		DefaultMessageListenerContainer cont = new DefaultMessageListenerContainer();
		cont.setMessageListener(workerDirectListener());
		cont.setConnectionFactory(amqConnectionFactory());
		cont.setDestination(new ActiveMQQueue(env.getRequiredProperty("queue.toWorkerDirect")));
		cont.setMessageSelector(MessageSelector.WORKER_ID.name() + "='" + workerId() + "'");
		return cont;
	}
	
	@Bean
	public ObjectMapper jsonMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper;
	}
	
	@Bean
	public String workerId() {
		return UUID.randomUUID().toString();
	}
	
	@Bean
	public MessageListener workerTaskListener() {
		return new WorkerTaskListener();
	}
	
	@Bean
	public MessageListener workerDirectListener() {
		return new WorkerDirectListener();
	}
	
	@Bean
	public MessageProducer workerProducer() {
		return new WorkerProducer();
	}
	
	@Bean
	@Lazy
	public QueueProcessor workerTaskQueueProcessor() {
		return new WorkerTaskQueueProcessor();
	}
	
	private Prerequisite getOsType() {
		if (SystemUtils.IS_OS_WINDOWS) return Prerequisite.WINDOWS;
		if (SystemUtils.IS_OS_LINUX) return Prerequisite.LINUX;
		if (SystemUtils.IS_OS_MAC) return Prerequisite.MAC;
		return Prerequisite.OTHER;
	}
}
