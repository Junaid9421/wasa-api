package com.wasa.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.wasa.api.controller.auth.LoginService;
import com.wasa.api.controller.wasa.WasaAPIService;

@Component
public class ServiceFactoryImpl implements ServiceFactory {

	@Autowired
	WasaAPIService wasaAPIService;

	@Autowired
	LoginService loginService;

	@Override
	public WasaAPIService getWasaAPIService() {
		return wasaAPIService;
	}

	@Override
	public LoginService getLoginService() {
		return loginService;
	}

}
