package com.favourite.collections.notification.api;

import com.favourite.collections.commons.useradmin.data.OtpMessageRequest;
import com.favourite.collections.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.favourite.collections.config.ApiConstants.NOTIFICATION_BASE_URL;

@Slf4j
@RestController
@RequestMapping(NOTIFICATION_BASE_URL)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> sendEmailTest(@RequestBody OtpMessageRequest otpMessageRequest) {
        notificationService.mailSentSuccessfully(otpMessageRequest);
        return ResponseEntity.ok("Mail Sent, I Guess!");

    }
}
