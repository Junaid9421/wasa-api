package com.wasa.api.controller.auth;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wasa.api.dao.DAO;
import com.wasa.api.model.WebUser;

import io.sentry.Sentry;

@Component
public class LoginServiceImpl implements LoginService {

	@Autowired
	private DAO dao;

	@Override
	public WebUser findByUserName(String username) {
		WebUser user = null;
		PreparedStatement stmt = null;
		try {
			String query = "SELECT WU.USER_NME,WU.ACTIVE_IND,WU.USER_PASSWORD FROM WEB_USERS WU"
					+ " WHERE TRIM(UPPER(WU.USER_NME))=? AND WU.ACTIVE_IND=?";
			stmt = this.dao.getJDBTemplate().getDataSource().getConnection().prepareStatement(query);
			stmt.setString(1, username.toUpperCase().trim());
			stmt.setString(2, "Y");
			ResultSet rs = stmt.executeQuery();
			if (rs != null && rs.next()) {
				user = new WebUser();
				user.setUsername(rs.getString("USER_NME") != null ? rs.getString("USER_NME") : "");
				user.setPassword(rs.getString("USER_PASSWORD") != null ? rs.getString("USER_PASSWORD") : "");
				rs.close();
			} else {
				user = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
		}
		return user;
	}

}
