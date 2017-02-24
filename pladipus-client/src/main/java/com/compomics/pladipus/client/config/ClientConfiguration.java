package com.compomics.pladipus.client.config;

import java.util.ResourceBundle;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.compomics.pladipus.client.CliTaskProcessor;
import com.compomics.pladipus.client.CliTaskProcessorImpl;
import com.compomics.pladipus.client.CommandLineIO;
import com.compomics.pladipus.client.CommandLineImpl;
import com.compomics.pladipus.client.MainCLI;
import com.compomics.pladipus.client.MainGUI;
import com.compomics.pladipus.client.queue.ClientListener;
import com.compomics.pladipus.client.queue.ClientMessageProducer;
import com.compomics.pladipus.client.queue.MessageMap;
import com.compomics.pladipus.client.queue.MessageTask;
import com.compomics.pladipus.client.queue.UuidGenerator;

@Configuration
@PropertySource({"classpath:application.properties", "classpath:queue.properties"})
public class ClientConfiguration {
	
	@Autowired
	private Environment env;
	 
	private final static long DEFAULT_TIMEOUT = 10000L;
	
	@Bean
	public String clientIdProperty() {
		return env.getRequiredProperty("queue.clientId");
	}
	
	@Bean
	public long clientTimeout() {
		try {
			return Long.parseLong(env.getProperty("queue.clientTimeout"));
		} catch (Exception e) {
			return DEFAULT_TIMEOUT;
		}
	}
	
	@Bean
	public CliTaskProcessor cliTaskProcessor() {
		return new CliTaskProcessorImpl();
	}
	
	@Lazy
	@Bean
	public ResourceBundle cmdLine() {
		return ResourceBundle.getBundle("cli_options");
	}
	
	@Lazy
	@Bean
	public MainCLI cli() {
		return new MainCLI();
	}
	
	@Lazy
	@Bean
	public MainGUI gui() {
		return new MainGUI();
	}
	
	@Bean
	public CommandLineIO cmdLineIO() {
		return new CommandLineImpl();
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
	public JmsTemplate jmsTemplate() {
	    JmsTemplate temp = new JmsTemplate();
		temp.setConnectionFactory(cacheConnectionFactory());
		temp.setDefaultDestination(new ActiveMQQueue(env.getRequiredProperty("queue.fromClients")));
		return temp;
	}
	
	@Bean
	public DefaultMessageListenerContainer listenerContainer() {
		DefaultMessageListenerContainer cont = new DefaultMessageListenerContainer();
		cont.setMessageListener(clientListener());
		cont.setConnectionFactory(amqConnectionFactory());
		cont.setDestination(new ActiveMQQueue(env.getRequiredProperty("queue.toClients")));
		cont.setMessageSelector(clientIdProperty() + "='" + uuidGen().getClientID() + "'");
		return cont;
	}
	
	@Bean
	public ClientListener clientListener() {
		return new ClientListener();
	}
	
	@Bean
	public ClientMessageProducer clientMessageProducer() {
		return new ClientMessageProducer();
	}
	
	@Bean
	public UuidGenerator uuidGen() {
		return new UuidGenerator();
	}
	
	@Bean
	public MessageMap messageMap() {
		return new MessageMap();
	}
	
	@Bean
	@Scope(value = "prototype")
	@Lazy
	public MessageTask messageTask(String text) {
		return new MessageTask(text);
	}
}
