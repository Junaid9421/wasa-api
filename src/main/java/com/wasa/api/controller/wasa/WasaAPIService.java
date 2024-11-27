package com.wasa.api.controller.wasa;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.wasa.api.dao.DAO;
import com.wasa.api.model.WasaInvoice;

@Service
public interface WasaAPIService {
	Map savePurchaseInvoice(WasaInvoice vo);

	boolean validateCompanyInfo(String key, String companyId);
}
