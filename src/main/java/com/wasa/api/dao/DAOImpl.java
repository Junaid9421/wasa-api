package com.wasa.api.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class DAOImpl implements DAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    public JdbcTemplate getJDBTemplate() {
        return jdbcTemplate;
    }

    @Override
    public List<?> getData(String query) {
        return jdbcTemplate.queryForList(query);
    }

    @Override
    public boolean insertAll(List<String> query, String userName) {
        boolean flag = false;

        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            // this.jdbcTemplate.update("call SET_WEB_CTX(?)", userName);
            for (int i = 0; i < query.size(); i++) {
                jdbcTemplate.update(query.get(i));
            }
            // int[] i = this.jdbcTemplate.batchUpdate(query);
            transactionManager.commit(status);
            flag = true;
        } catch (Exception ex) {
            System.out.println(userName + " -> (call insertAll{List})");
            ex.printStackTrace();
            transactionManager.rollback(status);
        }
        return flag;
    }

    @Override
    public synchronized boolean updateAll(List<String> query, String userName) {
        boolean flag = false;

        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            // this.jdbcTemplate.update("call SET_WEB_CTX(?)", userName);
            for (int i = 0; i < query.size(); i++) {
                jdbcTemplate.update(query.get(i));
            }
            // int[] i = this.jdbcTemplate.batchUpdate(query);
            transactionManager.commit(status);
            flag = true;
        } catch (Exception ex) {
            System.out.println(userName + " -> (call updateAll[List})");
            ex.printStackTrace();
            transactionManager.rollback(status);
        }
        return flag;
    }

    @Override
    public boolean insert(String query) {
        boolean flag = false;
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = this.transactionManager.getTransaction(def);
        try {
            jdbcTemplate.update(query);
            transactionManager.commit(status);
            flag = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            transactionManager.rollback(status);
        }
        return flag;
    }
}
