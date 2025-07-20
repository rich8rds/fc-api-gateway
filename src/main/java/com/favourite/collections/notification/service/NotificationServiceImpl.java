package com.favourite.collections.notification.service;

import com.favourite.collections.commons.core.config.RabbitMQConfig;
import com.favourite.collections.commons.useradmin.data.OtpMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void mailSentSuccessfully(OtpMessageRequest otpMessageRequest) {
        //log.info("Sending mail from Core Service to rabbitmq : {}", otpMessageRequest);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.OTP_ROUTING_KEY, otpMessageRequest);
    }
}
