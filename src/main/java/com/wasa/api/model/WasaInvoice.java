package com.wasa.api.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WasaInvoice {
	private String invoiceId;
	private String invoiceType;
	private String invoiceDate;
	private String receivedDate;
	private String invoiceNumber;
	private String dueDate;
	private String discount;
	private String vendorAccount;
	private String createdBy;
	private String siteId;
	private String comId;
	private String amount; // Total amount
	private String currency; // Get CurrencyId & CurrencyRate
	private String currencyRate; // Base currency (e.g PKR)= 0 otherwise please rate of currency
	private String description;
	private List<WasaInvoiceDetail> detail = new ArrayList<>();
}
