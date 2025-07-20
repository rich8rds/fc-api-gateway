package com.favourite.collections.config;

import org.springframework.http.MediaType;

public class ApiConstants {

    public static final String MEDIA_TYPE_OCTET_STREAM = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    public static final String MEDIA_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;
    public static final String MEDIA_TYPE_FORM_DATA = MediaType.MULTIPART_FORM_DATA_VALUE;
    public static final String MEDIA_TYPE_TEXT_PLAIN = MediaType.TEXT_PLAIN_VALUE;
    public static final String MEDIA_TYPE_TEXT_EXCEL = "application/vnd.ms-excel";
    public static final String MEDIA_TYPE_TEXT_CSV = "text/csv";


    private static final String BASE_URL = "/api/v1";
    public static final String AUTH_BASE_URL = BASE_URL + "/auth";
    public static final String NOTIFICATION_BASE_URL = BASE_URL + "/notification";
}
