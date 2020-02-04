package com.evan.transaction.controller;

import com.evan.transaction.GlobalTransactionManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class globalTransactionController {

    @RequestMapping(value = "/global-rollback", method = RequestMethod.POST)
    public Boolean rollback(@RequestBody String groupId) throws SQLException {
        GlobalTransactionManager.rollback(groupId);
        return true;
    }

    @RequestMapping(value = "/global-commit", method = RequestMethod.POST)
    public Boolean commit(@RequestBody String groupId) throws SQLException {
        GlobalTransactionManager.commit(groupId);
        return true;
    }
}
