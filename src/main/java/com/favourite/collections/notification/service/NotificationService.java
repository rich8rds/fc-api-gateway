package com.favourite.collections.notification.service;


import com.favourite.collections.commons.useradmin.data.OtpMessageRequest;

public interface NotificationService {

    void mailSentSuccessfully(OtpMessageRequest otpMessageRequest);
}
