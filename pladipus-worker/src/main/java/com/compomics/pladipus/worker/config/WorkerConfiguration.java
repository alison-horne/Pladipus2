package com.compomics.pladipus.worker.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.compomics.pladipus.worker.WorkerListener;
import com.compomics.pladipus.worker.WorkerProducer;

@Configuration
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
	public DefaultMessageListenerContainer listenerContainer() {
		DefaultMessageListenerContainer cont = new DefaultMessageListenerContainer();
		cont.setMessageListener(workerListener());
		cont.setConnectionFactory(amqConnectionFactory());
		cont.setDestination(new ActiveMQQueue(env.getRequiredProperty("queue.toWorkers")));
		return cont;
	}
	
	@Bean
	public WorkerListener workerListener() {
		return new WorkerListener();
	}
	
	@Bean
	public WorkerProducer workerProducer() {
		return new WorkerProducer();
	}
}
