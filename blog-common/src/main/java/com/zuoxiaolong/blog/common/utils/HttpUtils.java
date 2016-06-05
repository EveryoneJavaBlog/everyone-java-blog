/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zuoxiaolong.blog.common.utils;

import com.zuoxiaolong.blog.common.bean.Attachment;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 发送HTTP请求的工具方法类
 *
 * @author Xiaolong Zuo
 * @since 1.0.0
 */
public interface HttpUtils {

    String DEFAULT_CHARSET = "UTF-8";

    /**
     * 发送普通的HTTP请求
     *
     * @param method
     * @param url
     * @return
     */
    static String sendHttpRequest(String method, String url ) {
        return sendHttpRequest(method, url, "");
    }

    /**
     * 发送普通的HTTP请求
     *
     * @param method
     * @param url
     * @param params
     * @return
     */
    static String sendHttpRequest(String method, String url , Map<String, String> params) {
        StringBuffer paramsString = new StringBuffer();
        if (!CollectionUtils.isEmpty(params)) {
            for (String key : params.keySet()) {
                paramsString.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        return sendHttpRequest(method, url, paramsString.length() == 0 ? paramsString.toString() : paramsString.substring(0, paramsString.length() - 1));
    }

    /**
     * 发送普通的HTTP请求
     *
     * @param method
     * @param url
     * @param params
     * @return
     */
    static String sendHttpRequest(String method, String url , String params) {
        try {
            return new String(sendHttpRequestAndGetBytes(method, url, params), DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送普通的HTTP请求
     *
     * @param method
     * @param url
     * @param params
     * @return
     */
    static byte[] sendHttpRequestAndGetBytes(String method , String url, String params) {
        try {
            if (method.equals("GET") && params != null) {
                url = url + "?" + params;
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod(method);
            connection.connect();
            if (params != null && method.equals("POST")) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params.getBytes(DEFAULT_CHARSET));
                outputStream.flush();
            }
            return IOUtils.readStreamBytes(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送带文件的HTTP请求
     *
     * @param url
     * @param params
     * @param attachmentKey
     * @param attachments
     * @return
     * @throws IOException
     */
    static String sendSimpleHttpMultipartRequest(String url, Map<String, String> params, String attachmentKey, Attachment[] attachments) throws IOException {
        return sendHttpMultipartRequest(url, CollectionUtils.mapToArrayMap(params), CollectionUtils.newMap(attachmentKey, attachments));
    }

    /**
     * 发送带文件的HTTP请求
     *
     * @param url
     * @param params
     * @param attachmentMap
     * @return
     * @throws IOException
     */
    static String sendHttpMultipartRequest(String url, Map<String, String[]> params, Map<String, Attachment[]> attachmentMap) throws IOException {
        return sendHttpMultipartRequest(url, DEFAULT_CHARSET, new HashMap<>(), params, attachmentMap);
    }

    /**
     * 发送带文件的HTTP请求
     *
     * @param url
     * @param charset
     * @param headerMap
     * @param params
     * @param attachmentMap
     * @return
     * @throws IOException
     */
    static String sendHttpMultipartRequest(String url, String charset, Map<String, String> headerMap, Map<String, String[]> params, Map<String, Attachment[]> attachmentMap) throws IOException {
        final String enter = "\r\n";
        String BOUNDARY = "---------HttpForward" + UUID.randomUUID().toString();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        for (String headerName : headerMap.keySet()) {
            connection.setRequestProperty(headerName, headerMap.get(headerName));
        }
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        OutputStream outputStream = connection.getOutputStream();
        if (!CollectionUtils.isEmpty(params)) {
            for (String key : params.keySet()) {
                String[] values = params.get(key);
                for (String value : values) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("--").append(BOUNDARY).append(enter);
                    stringBuilder.append("Content-Disposition: form-data;name=\"" + key + "\"" + enter + enter);
                    byte[] format = stringBuilder.toString().getBytes(charset);
                    outputStream.write(format);
                    outputStream.write(value.getBytes(charset));
                    outputStream.write(enter.getBytes(charset));
                }
            }
        }
        if (!CollectionUtils.isEmpty(attachmentMap)) {
            for (String key : attachmentMap.keySet()) {
                Attachment[] attachments = attachmentMap.get(key);
                for (Attachment attachment : attachments) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("--").append(BOUNDARY).append(enter);
                    stringBuilder.append("Content-Disposition: form-data;name=\"" + key + "\";filename=\"" + attachment.getFileName() + "\"" + enter);
                    stringBuilder.append("Content-Type:" + attachment.getContentType() + enter + enter);
                    byte[] format = stringBuilder.toString().getBytes(charset);
                    outputStream.write(format);
                    outputStream.write(attachment.getData());
                    outputStream.write(enter.getBytes(charset));
                }
            }
        }
        outputStream.write((enter + "--" + BOUNDARY + "--" + enter).getBytes(charset));
        outputStream.flush();
        return IOUtils.readStream(connection, charset);
    }

}
