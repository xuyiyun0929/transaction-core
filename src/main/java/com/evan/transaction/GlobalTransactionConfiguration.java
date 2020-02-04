package com.evan.transaction;

import com.evan.transaction.integration.resttemplate.server.ServerTransactionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan("com.evan.transaction")
@EnableAspectJAutoProxy
public class GlobalTransactionConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private ServerTransactionInterceptor serverTransactionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverTransactionInterceptor);
    }

}
