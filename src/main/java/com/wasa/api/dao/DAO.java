package com.wasa.api.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

public interface DAO {

    JdbcTemplate getJDBTemplate();

    List<?> getData(String query);

    boolean insertAll(List<String> query, String userName);

    boolean updateAll(List<String> query, String userName);

    boolean insert(String query);
}
