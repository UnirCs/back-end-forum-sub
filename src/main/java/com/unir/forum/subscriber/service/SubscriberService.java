package com.unir.forum.subscriber.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberService {

    private final JmsTemplate topicJmsTemplate;

    /**
     * Publica un mensaje en un topic.
     * @param topic - nombre del topic.
     * @param message - mensaje a enviar.
     */
    public void publishToTopic(String topic, String message){
        topicJmsTemplate.convertAndSend(topic, message);
    }

    /**
     * Escucha los mensajes que llegan a un topic.
     * @param message - mensaje recibido.
     */
    @JmsListener( destination = "tema1", containerFactory = "jmsFactoryTopic")
    public void listenTopic1(String message) {
        log.info("Mensaje del tema 1 publicado: " + message);
    }

    @JmsListener( destination = "tema2", containerFactory = "jmsFactoryTopic")
    public void listenTopic2(String message) {
        log.info("Mensaje del tema 2 publicado:: " + message);
    }

    @JmsListener( destination = "tema3", containerFactory = "jmsFactoryTopic")
    public void listenTopic3(String message) {
        log.info("Mensaje del tema 3 publicado:: " + message);
    }

    @JmsListener( destination = "tema4", containerFactory = "jmsFactoryTopic")
    public void listenTopic4(String message) {
        log.info("Mensaje del tema 4 publicado:: " + message);
    }

    /**
     * Escucha los mensajes que llegan a una cola.
     * @param message - mensaje recibido.
     */
    @JmsListener( destination = "${forum.userId}")
    public void listenDirectMessage(String message) {
        log.info("Se ha recibido un mensaje privado: " + message);
    }

}
