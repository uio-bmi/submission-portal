package no.uio.ifi.spring;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ConnectionFactory connectionFactory() throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("norway1");
        connectionFactory.setPassword(System.getenv("MQ_PASSWORD"));
        connectionFactory.setUri(new URI("amqps://hellgate.crg.eu:5271/norway1"));
        return connectionFactory;
    }

    @Bean
    public Channel channel() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, TimeoutException {
        return connectionFactory().newConnection().createChannel();
    }

}
