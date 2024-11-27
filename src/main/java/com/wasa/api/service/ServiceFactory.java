package com.wasa.api.service;

import org.springframework.stereotype.Service;

import com.wasa.api.controller.auth.LoginService;
import com.wasa.api.controller.auth.LoginServiceImpl;
import com.wasa.api.controller.wasa.WasaAPIService;
import com.wasa.api.controller.wasa.WasaAPIServiceImpl;

@Service
public interface ServiceFactory {
	WasaAPIService getWasaAPIService();
		
	LoginService getLoginService();
	
}
