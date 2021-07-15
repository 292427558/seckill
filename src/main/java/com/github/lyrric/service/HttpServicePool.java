package com.github.lyrric.service;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * mode class
 *
 * @Author LiuJun
 * @Date 2021/3/16 14:07
 */

public class HttpServicePool {
    
    RestTemplate restTemplate;
//    RestTemplate restTemplate = new RestTemplate();

    private static int maxTotalConnect = 8; //连接池的最大连接数默认为0
    private static int maxConnectPerRoute = 10; //单个主机的最大连接数
    private static int connectTimeout = 2000; //连接超时默认2s
    private static int readTimeout =10000; //读取超时默认30s

    public HttpServicePool() {
        this.restTemplate = getRestTemplate();
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
                                          @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables){
        return restTemplate.exchange(url,method,requestEntity,responseType,uriVariables);
    }


    
    private  ClientHttpRequestFactory createFactory(){
        if (maxTotalConnect <= 0) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(connectTimeout);
            factory.setReadTimeout(readTimeout);
            return factory;
        }
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(maxTotalConnect)
                .setMaxConnPerRoute(maxConnectPerRoute)
//                .setProxy(new HttpHost("127.0.0.1", 8888,"https"))
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                httpClient);
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }


    public  RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(createFactory());
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();

        HttpMessageConverter<?> converterTarget = null;
        for (HttpMessageConverter<?> item : converterList) {
            if (StringHttpMessageConverter.class == item.getClass()) {
                converterTarget = item;
                break;
            }
        }
        if (null != converterTarget) {
            converterList.remove(converterTarget);
        }
        converterList.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        converterList.add(new FastJsonHttpMessageConverter());
        return restTemplate;
    }
}
