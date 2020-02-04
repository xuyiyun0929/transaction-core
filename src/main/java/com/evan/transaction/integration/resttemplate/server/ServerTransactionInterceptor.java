package com.evan.transaction.integration.resttemplate.server;

import com.evan.transaction.GlobalTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ServerTransactionInterceptor implements HandlerInterceptor {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String groupId = httpServletRequest.getHeader(GlobalTransactionManager.KEY_GROUP_ID);
        if (!StringUtils.isEmpty(groupId)) {
            GlobalTransactionManager.slaveBegin(groupId,transactionManager);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if (!StringUtils.isEmpty(GlobalTransactionManager.getGroupId())) {
            GlobalTransactionManager.slaveComplete();
        }
    }
}
