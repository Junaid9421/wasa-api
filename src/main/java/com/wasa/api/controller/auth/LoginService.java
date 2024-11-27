package com.wasa.api.controller.auth;

import org.springframework.stereotype.Service;

import com.wasa.api.model.WebUser;

@Service
public interface LoginService {
	WebUser findByUserName(String username);
}
