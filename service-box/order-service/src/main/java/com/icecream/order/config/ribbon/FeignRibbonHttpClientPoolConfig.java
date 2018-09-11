package com.icecream.order.config.ribbon;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/20 0020
 */
@Component
public class FeignRibbonHttpClientPoolConfig {
    private static final int POOL_MAX_TOTAL = 3000;
    private static final int DEFAULT_MAX_PER_ROUTE = 500;

    private static final int VALIDATE_AFTER_INACTIVITY = 1000;

    @Bean(name = "httpClient", destroyMethod = "close")
    CloseableHttpClient httpClient() throws KeyManagementException {
        return buildCloseableHttpClient();
    }

    /**
     * 构建HttpClient连接池
     *
     * @return
     * @throws KeyManagementException
     */
    public CloseableHttpClient buildCloseableHttpClient() throws KeyManagementException {
        SSLContext sslcontext = SSLContexts.createDefault();
        sslcontext.init(null, new TrustManager[]{new TrustAnyManager()}, null); //设置https客户端信任万能证书
        SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);
        //注册请求方式，根据URL自动请求
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", ssf)
                .build();
        //创建Http连接池，单位时间内释放已使用过连接池中的连接
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(POOL_MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        connectionManager.setValidateAfterInactivity(VALIDATE_AFTER_INACTIVITY);

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
                .build();
    }

    class TrustAnyManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

}
