package com.evan.transaction.dao;

import java.util.List;

public interface GlobalTransactionDAO {
    void executeRollbackSQL(String dynamicSQL, List<Object[]> paramList);


}