package com.wasa.api.model;

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
public class WasaInvoiceDetail {
	private String detailId;
	private String itemAccountCode;
	private String itemRate;
	private String itemQty;
	private String unit;
	private String unitType;
	private String discount;
	private String amount;
	private String actualAmount; // In case of multi-currency
	private String description;
	private String classId;
}
