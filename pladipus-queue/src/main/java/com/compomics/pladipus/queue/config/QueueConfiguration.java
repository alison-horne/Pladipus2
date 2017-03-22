package com.compomics.pladipus.queue.config;

import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.compomics.pladipus.base.config.BaseConfiguration;
import com.compomics.pladipus.queue.ClientTaskProcessor;
import com.compomics.pladipus.queue.ControlClientListener;
import com.compomics.pladipus.queue.ControlClientProducer;
import com.compomics.pladipus.queue.ControlWorkerListener;
import com.compomics.pladipus.queue.ControlWorkerProducer;
import com.compomics.pladipus.queue.ReadyTaskScheduler;
import com.compomics.pladipus.queue.WorkerTaskProcessor;

@Configuration
@Import(BaseConfiguration.class)
@PropertySource({"classpath:application.properties", "classpath:queue.properties"})
public class QueueConfiguration {
	
	@Autowired
	private Environment env;
	
	@Bean
	public String clientIdProperty() {
		return env.getRequiredProperty("queue.clientId");
	}

	@Bean
	public ActiveMQConnectionFactory amqConnectionFactory() {
		return new ActiveMQConnectionFactory("tcp://" + env.getRequiredProperty("amq.host") + ":" + env.getRequiredProperty("amq.queue.port"));
	}

	@Bean
	public CachingConnectionFactory cacheConnectionFactory() {
		return new CachingConnectionFactory(amqConnectionFactory());
	}

	@Bean
	public JmsTemplate toClientsTemplate() {
	    JmsTemplate temp = new JmsTemplate();
		temp.setConnectionFactory(cacheConnectionFactory());
		temp.setDefaultDestination(new ActiveMQQueue(env.getRequiredProperty("queue.toClients")));
		try {
			Long timeout = Long.parseLong(env.getProperty("queue.clientTimeout")) * 2;
			temp.setExplicitQosEnabled(true);
			temp.setTimeToLive(timeout);
		} catch (Exception e) {}
		return temp;
	}
	
	@Bean
	public DefaultMessageListenerContainer clientListenerContainer() {
		DefaultMessageListenerContainer cont = new DefaultMessageListenerContainer();
		cont.setMessageListener(clientListener());
		cont.setConnectionFactory(amqConnectionFactory());
		cont.setDestination(new ActiveMQQueue(env.getRequiredProperty("queue.fromClients")));
		return cont;
	}
	
	@Bean
	public ControlClientListener clientListener() {
		return new ControlClientListener();
	}
	
	@Bean
	public ControlClientProducer clientProducer() {
		return new ControlClientProducer();
	}
	
	@Bean
	public JmsTemplate toWorkersTemplate() {
	    JmsTemplate temp = new JmsTemplate();
		temp.setConnectionFactory(cacheConnectionFactory());
		temp.setDefaultDestination(new ActiveMQQueue(env.getRequiredProperty("queue.toWorkers")));
		return temp;
	}
	
	@Bean
	public DefaultMessageListenerContainer workerListenerContainer() {
		DefaultMessageListenerContainer cont = new DefaultMessageListenerContainer();
		cont.setMessageListener(workerListener());
		cont.setConnectionFactory(amqConnectionFactory());
		cont.setDestination(new ActiveMQQueue(env.getRequiredProperty("queue.fromWorkers")));
		return cont;
	}
	
	@Bean
	public ControlWorkerListener workerListener() {
		return new ControlWorkerListener();
	}
	
	@Bean
	public ControlWorkerProducer workerProducer() {
		return new ControlWorkerProducer();
	}
	
	@Bean
	public ReadyTaskScheduler readyTaskScheduler() {
		return new ReadyTaskScheduler();
	}
	
	@Bean
	@Scope(value = "prototype")
	@Lazy
	public ClientTaskProcessor clientTaskProcessor(TextMessage message) {
		return new ClientTaskProcessor(message);
	}
	
	@Bean
	@Scope(value = "prototype")
	@Lazy
	public WorkerTaskProcessor workerTaskProcessor(TextMessage message) {
		return new WorkerTaskProcessor(message);
	}
}
