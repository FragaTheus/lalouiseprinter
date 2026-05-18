package matheusfragadev.br.com.lalouise.printerservice.infra;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitConfig {

    @Value("${restaurant.id}")
    private String restaurantId;

    @Bean
    @ConditionalOnProperty(prefix = "rabbit", name = "declare-topology", havingValue = "true", matchIfMissing = true)
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public DirectExchange labelExchange() {
        return new DirectExchange("label.exchange", true, false);
    }

    @Bean(name = "printQueue")
    public Queue printQueue() {
        return new Queue("label.print." + restaurantId, true);
    }

    @Bean
    public Binding printBinding(
            @Qualifier("printQueue") Queue printQueue,
            DirectExchange labelExchange
    ) {
        return BindingBuilder
                .bind(printQueue)
                .to(labelExchange)
                .with("print." + restaurantId);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
