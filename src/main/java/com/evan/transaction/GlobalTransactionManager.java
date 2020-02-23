package com.evan.transaction;

import com.evan.transaction.entity.BranchRecord;
import com.evan.transaction.entity.RollbackRecord;
import com.evan.transaction.exception.GlobalTransactionException;
import com.evan.transaction.remote.GtcRestClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalTransactionManager {

    public static final String KEY_GROUP_ID = "GROUP_ID";
    private static ThreadLocal<String> groupIdLocal = new ThreadLocal<>();
    private static Map<String, Stack<BranchRecord>> transactionLogMap = new ConcurrentHashMap<>();
    private static Map<String, List<RollbackRecord>> rollbackMap = new ConcurrentHashMap<>();
    private static Map<String, Connection> localTransactionMap = new ConcurrentHashMap<>();
//    private static Map<String, String> tableLockMap = new ConcurrentHashMap<>();

    public static String masterBegin(DataSourceTransactionManager transactionManager) throws GlobalTransactionException {
        if (getGroupId() == null) {
            String groupId = UUID.randomUUID().toString();
            groupIdLocal.set(groupId);
            transactionLogMap.put(groupId, new Stack<>());
            rollbackMap.put(groupId, new ArrayList<>());
            localTransactionBegin(groupId, transactionManager);
            return groupId;
        } else {
            throw new GlobalTransactionException("全局事务已被创建!");
        }
    }

    private static void localTransactionBegin(String groupId, DataSourceTransactionManager transactionManager) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionManager.getTransaction(def);
        Connection connection = DataSourceUtils.getConnection(transactionManager.getDataSource());
        localTransactionMap.put(groupId, connection);
    }

    public static String branchRegister(String serviceUrl) throws GlobalTransactionException {
        if (null != getGroupId()) {
            String branchId = UUID.randomUUID().toString();
            BranchRecord branchRecord = new BranchRecord(branchId, serviceUrl);
            Stack<BranchRecord> branchRecordStack = transactionLogMap.get(getGroupId());
            branchRecordStack.push(branchRecord);
            return branchId;
        }
        throw new GlobalTransactionException("注册分支事务失败!");
    }

    public static void commit(String commitGroupId) throws SQLException {
        Connection connection = localTransactionMap.remove(commitGroupId);
        if (null != connection) {
            connection.commit();
        }
        List<RollbackRecord> rollbackRecordList = null;
        Stack<BranchRecord> branchRecordStack = transactionLogMap.get(commitGroupId);
        while (!branchRecordStack.isEmpty()) {
            BranchRecord branchRecord = branchRecordStack.pop();
            GtcRestClient.commitExecute(branchRecord.getBranchId(), branchRecord.getBranchServiceUrl());
            rollbackRecordList = getRollbackRecordList(commitGroupId);
//            removeTableLock(rollbackRecordList);
        }
        groupIdLocal.remove();
        transactionLogMap.remove(commitGroupId);
        rollbackMap.remove(commitGroupId);
    }

    public static void addRollbackRecord(RollbackRecord rollbackRecord) throws GlobalTransactionException {
        List<RollbackRecord> rollbackRecordList = rollbackMap.get(getGroupId());
        if (null != rollbackRecordList && !rollbackRecordList.isEmpty()) {
            rollbackRecordList.add(rollbackRecord);
        } else {
            rollbackRecordList = Arrays.asList(rollbackRecord);
            rollbackMap.put(getGroupId(), rollbackRecordList);
        }
//        addTableLock(rollbackRecord);
    }

    public static void rollback(String rollbackGroupId) throws SQLException {
        Connection connection = localTransactionMap.remove(rollbackGroupId);
        if (null != connection) {
            connection.rollback();
        }
//            List<RollbackRecord> rollbackRecordList = getRollbackRecordList(rollbackGroupId);
        try {
//                for (RollbackRecord rollbackRecord : rollbackRecordList) {
//                    String dynamicSQL = rollbackRecord.getDynamicSQL();
//                    List<Object[]> paramList = rollbackRecord.getParamList();
//                    //todo 执行回退sql
//                }
            Stack<BranchRecord> branchRecordStack = transactionLogMap.get(rollbackGroupId);
            while (!branchRecordStack.isEmpty()) {
                BranchRecord branchRecord = branchRecordStack.pop();
                GtcRestClient.rollbackExecute(branchRecord.getBranchId(), branchRecord.getBranchServiceUrl());
            }
        } finally {
//                removeTableLock(rollbackRecordList);
        }
    }

    public static String getGroupId() {
        return groupIdLocal.get();
    }

    public static void slaveBegin(String groupId, DataSourceTransactionManager transactionManager) {
        groupIdLocal.set(groupId);
        transactionLogMap.put(groupId, new Stack<>());
        rollbackMap.put(groupId, new ArrayList<>());
        localTransactionBegin(groupId, transactionManager);
    }

    public static void slaveComplete() {
        groupIdLocal.remove();
    }


    public static List<RollbackRecord> getRollbackRecordList(String branchId) {
        List<RollbackRecord> rollbackRecordList = rollbackMap.get(branchId);
        if (null == rollbackRecordList) {
            return new ArrayList<>();
        }
        return rollbackRecordList;
    }

//    private static void addTableLock(RollbackRecord rollbackRecord) {
//        tableLockMap.put(rollbackRecord.getTableName(), getGroupId());
//    }

//    private static void removeTableLock(List<RollbackRecord> rollbackRecordList) {
//        for (RollbackRecord rollbackRecord : rollbackRecordList) {
//            tableLockMap.remove(rollbackRecord.getTableName());
//        }
//    }

    public static Stack<BranchRecord> getBranchRecords(String groupId) {
        return transactionLogMap.get(groupId);
    }

}
