package com.wasa.api.controller.wasa;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wasa.api.model.WasaInvoice;
import com.wasa.api.service.ServiceFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping(value = "api/wasa")
@RestController
public class WasaAPIController {

	@Autowired
	private ServiceFactory serviceFactory;	

	@PostMapping(value = "processInvoice", produces = "application/json")
	public ResponseEntity<?> getPurchaseInvoice(@RequestBody WasaInvoice invoice) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode obj = objectMapper.createObjectNode();
		if (invoice.getComId() != null && !invoice.getComId().isEmpty()) {
			if (invoice.getComId().equals("38")) {
				Map map = this.serviceFactory.getWasaAPIService().savePurchaseInvoice(invoice);
				if (map.get("voucherMasterId") != null && !map.get("voucherMasterId").toString().isEmpty()) {
					obj.put("status", "success");
					obj.put("invoiceId", map.get("voucherMasterId").toString());
				} else {
					obj.put("status", "error");
					obj.put("invoiceId", "");
				}
			} else {
				obj.put("status", "error");
				obj.put("Message", "Invalid comId.");
			}
		} else {
			obj.put("status", "error");
			obj.put("Message", "comId is missing.");
			obj.put("comId", "");
		}
		return ResponseEntity.ok().body(objectMapper.writeValueAsString(obj));
	}

	public boolean validateAuthKey(HttpServletRequest request, String companyId) {
		boolean flag = false;

		return flag;
	}

}
