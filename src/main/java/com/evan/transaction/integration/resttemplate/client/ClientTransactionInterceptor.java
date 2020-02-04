package com.evan.transaction.integration.resttemplate.client;

import com.evan.transaction.GlobalTransactionManager;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;

public class ClientTransactionInterceptor implements ClientHttpRequestInterceptor {

    @SneakyThrows
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        String groupId = GlobalTransactionManager.getGroupId();
        URI url = httpRequest.getURI();
        if (!StringUtils.isEmpty(groupId) && !isTransactionPath(url.getPath())) {
            String serviceUrl = url.getScheme() + "://" + url.getHost();
            if (-1 != url.getPort()) {
                serviceUrl = serviceUrl + ":" + url.getPort();
            }
            String branchId = GlobalTransactionManager.branchRegister(serviceUrl);
            HttpHeaders headers = httpRequest.getHeaders();
            headers.add(GlobalTransactionManager.KEY_GROUP_ID, branchId);
        }
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }

    private boolean isTransactionPath(String path) {
        String commitPath = "/global-commit";
        String rollbackPath = "/global-rollback";
        return rollbackPath.equals(path) || commitPath.equals(path);
    }
}
