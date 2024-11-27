package com.wasa.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasa.api.model.WebUser;
import com.wasa.api.service.ServiceFactory;

@Component
public class CutomeUserDetailService implements UserDetailsService {
	ObjectMapper mapper = new ObjectMapper();
	@Autowired
	private ServiceFactory serviceFactory;

	@Override
	public WebUser loadUserByUsername(String username) throws UsernameNotFoundException {
		WebUser user = this.serviceFactory.getLoginService().findByUserName(username);
		if (user != null) {
			try {
				System.out.println(mapper.writer().writeValueAsString(user));
			} catch (JsonProcessingException e) {
				System.out.println("Exception " + e);
				e.printStackTrace();
			}
		} else {
			System.out.println("User Information is not found.");
		}
		return user;
	}
}
