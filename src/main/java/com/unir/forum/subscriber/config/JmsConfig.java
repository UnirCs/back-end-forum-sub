package com.unir.forum.subscriber.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${spring.active-mq.broker-url}")
    private String brokerUrl;

    /**
     * Este bean es el que se encarga de establecer la conexión con el broker de ActiveMQ
     * Dado que estamos usando el starter de activeMQ, no es necesario configurar el usuario y la contraseña
     * Estos valores están indicados en el application.yml
     *
     * @return ConnectionFactory
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        return activeMQConnectionFactory;
    }


    /**
     * Este bean devuelve un objeto JMSTemplate que se encarga de enviar los mensajes a los topicos.
     * Esto lo sabemos porque hemos configurado la propiedad setPubSubDomain a true.
     * JMS son las siglas de Java Message Service. Es una API de Java que permite la comunicación asíncrona entre procesos o aplicaciones.
     * Es utilizado para absorber la complejidad de la comunicación entre aplicaciones. Obviamos las complejidades específicas de la plataforma y nos centramos en la lógica de negocio.
     *
     * @return JmsTemplate
     */
    @Bean(name = "topicJmsTemplate")
    public JmsTemplate topicJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setPubSubDomain(true); // Esta propiedad es para poder mandar mensajes a topicos (varios subscriptores)
        return jmsTemplate;
    }


    /**
     * Este bean devuelve un objeto JmsListenerContainerFactory que se encarga de recibir los mensajes de los topicos.
     * Sabemos que es para los topicos porque hemos configurado la propiedad setPubSubDomain a true.
     * Además, setSubscriptionDurable a true indica que el subscriptor es duradero.
     * El ClientId es el identificador del subscriptor.
     * Gracias a este Bean podemos usar la anotación @JmsListener en los métodos de la clase SubscriberService.
     *
     * @param connectionFactory - ConnectionFactory que se encarga de establecer la conexión con el broker de ActiveMQ
     * @param configurer - Bean gestionado por Spring. Se encarga de configurar el JmsListenerContainerFactory
     * @return JmsListenerContainerFactory
     */
    @Bean(name = "jmsFactoryTopic")
    public JmsListenerContainerFactory jmsFactoryTopic(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer,
            @Value("${forum.userId}") String clientId) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }


    /**
     * Este bean devuelve un objeto JmsListenerContainerFactory que se encarga de recibir los mensajes de las colas.
     * Sabemos que es para las colas porque hemos configurado la propiedad setPubSubDomain a false (por defecto es false).
     * Además, setSubscriptionDurable a true indica que el subscriptor es duradero.
     * El ClientId es el identificador del subscriptor.
     * Gracias a este Bean podemos usar la anotación @JmsListener en los métodos de la clase SubscriberService.
     *
     * @param connectionFactory - ConnectionFactory que se encarga de establecer la conexión con el broker de ActiveMQ
     * @param configurer - Bean gestionado por Spring. Se encarga de configurar el JmsListenerContainerFactory
     * @return JmsListenerContainerFactory
     */
    @Bean(name = "jmsFactoryQueue")
    public JmsListenerContainerFactory jmsFactoryQueue(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer,
            @Value("${forum.userId}") String clientId) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setClientId(clientId);
        return factory;
    }
}
