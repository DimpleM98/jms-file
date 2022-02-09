package org.example;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import static java.util.Collections.singletonList;

public final class CamelJmsToFileExample {
    public static void main(String args[])throws Exception {
        try (CamelContext context = new DefaultCamelContext()) {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");

            context.addComponent("test-jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("test-jms:queue:test.queue").to("file://test");
                }
            });
            try (ProducerTemplate template = context.createProducerTemplate()) {
                context.start();
                for (int i = 0; i < 10; i++) {
                    template.sendBody("test-jms:queue:test.queue", "Test Message: " + i);
                }
            }
            Thread.sleep(1000);
            context.stop();

            }
        }
    }
