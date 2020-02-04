package com.evan.transaction.aop;

import com.evan.transaction.GlobalTransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GlobalTransactionalAspect {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Pointcut("@annotation(com.evan.transaction.annotation.GlobalTransactional)")
    public void GlobalTransactionalPoint(){
    }

    @Around("GlobalTransactionalPoint()")
    public void GlobalTransactionalAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String groupId = GlobalTransactionManager.masterBegin(transactionManager);
        log.debug("开启全局事务：{}",groupId);

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            log.debug("全局事务回退：{}",groupId);
            GlobalTransactionManager.rollback(groupId);
            throw throwable;
        }
        log.debug("提交全局事务：{}",groupId);
        GlobalTransactionManager.commit(groupId);
    }
}
