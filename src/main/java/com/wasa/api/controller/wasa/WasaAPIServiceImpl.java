package com.wasa.api.controller.wasa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wasa.api.dao.DAO;
import com.wasa.api.model.WasaInvoice;
import com.wasa.api.model.WasaInvoiceDetail;

import com.wasa.api.util.*;

import io.sentry.Sentry;

@Component
public class WasaAPIServiceImpl implements WasaAPIService {

	@Autowired
	private DAO dao;

	@Override
	public Map savePurchaseInvoice(WasaInvoice vo) {
		boolean flag = false;
		List<String> queryList = new ArrayList();
		List<String> updateList = null;
		String voucherMasterId = vo.getInvoiceId();
		Map returnMap = new HashMap();
		returnMap.put("voucherMasterId", "");
		returnMap.put("error", null);
		try {
			if (voucherMasterId != null && !voucherMasterId.isEmpty() && !voucherMasterId.equalsIgnoreCase("0")) {
				queryList.add("UPDATE VOUCHER_MASTER SET REVISION_NBR=NVL(REVISION_NBR,0)+1,LAST_UPDATED_BY='"
						+ vo.getCreatedBy() + "',LAST_UPDATED_DTE=SYSDATE," + " VOUCHER_DTE=TO_DATE('"
						+ vo.getInvoiceDate() + "','DD-MM-YYYY')," + " FIN_YEAR_ID=GET_FINANCIAL_YEAR_ID('"
						+ vo.getInvoiceDate() + "'," + vo.getComId() + "),COA_CDE='" + vo.getVendorAccount() + "',"
						+ " ORDER_NBR='" + (vo.getInvoiceDate() != null ? vo.getInvoiceNumber() : null) + "',"
						+ " ORDER_DTE="
						+ (vo.getInvoiceDate() != null ? "TO_DATE('" + vo.getInvoiceDate() + "','DD-MM-YYYY')" : null)
						+ "," + " DUE_DTE="
						+ (vo.getDueDate() != null ? "TO_DATE('" + vo.getDueDate() + "','DD-MM-YYYY')" : null) + ""
						+ " WHERE VOUCHER_MASTER_ID=" + vo.getInvoiceId() + "");
				queryList.add("DELETE FROM VOUCHER_DETAIL WHERE VOUCHER_MASTER_ID=" + vo.getInvoiceId() + "");
			} else {
				String query = "SELECT SEQ_VOUCHER_MASTER_ID.NEXTVAL VMASTER FROM DUAL";
				List list = this.dao.getJDBTemplate().queryForList(query);
				String isRecurring = "N";
				if (list != null && !list.isEmpty()) {
					Map map = (Map) list.get(0);
					voucherMasterId = (String) map.get("VMASTER").toString();
				}
				String masterQuery = "INSERT INTO VOUCHER_MASTER(VOUCHER_MASTER_ID,VOUCHER_NBR,VOUCHER_DTE,VOUCHER_SUB_TYP_ID,FIN_YEAR_ID,COA_CDE,"
						+ " COMPANY_ID,PREPARED_BY,PREPARED_DTE,POSTED_IND,SITE_ID,ORDER_NBR,ORDER_DTE,DUE_DTE) "
						+ " VALUES(" + voucherMasterId + ",''||GET_VOUCHER_NBR('" + vo.getInvoiceDate()
						+ "',985,GET_FINANCIAL_YEAR_ID('" + vo.getInvoiceDate() + "'," + vo.getComId() + "),'"
						+ isRecurring + "'," + vo.getComId() + ")||'',TO_DATE('" + vo.getInvoiceDate()
						+ "','DD-MM-YYYY'),985 , " + " GET_FINANCIAL_YEAR_ID('" + vo.getInvoiceDate() + "',"
						+ vo.getComId() + "),'" + vo.getVendorAccount() + "'," + vo.getComId() + ",'" + vo.getCreatedBy()
						+ "',SYSDATE,'N'," + vo.getSiteId() + "," + " '"
						+ (vo.getInvoiceDate() != null ? vo.getInvoiceNumber() : null) + "',"
						+ (vo.getInvoiceDate() != null ? "TO_DATE('" + vo.getInvoiceDate() + "','DD-MM-YYYY')" : null)
						+ "," + " "
						+ (vo.getDueDate() != null ? "TO_DATE('" + vo.getDueDate() + "','DD-MM-YYYY')" : null) + ")";

				queryList.add(masterQuery);
			}
			// Save Voucher Detail
			double amount = Double.parseDouble(vo.getAmount().isEmpty() ? "0" : vo.getAmount());
			String narration = "";
			if (vo.getInvoiceType().equalsIgnoreCase("PI")) {
				narration = (vo.getDescription().isEmpty() ? "CREDIT PURCHASE" : vo.getDescription());
				amount = (-1 * amount);
			}
			queryList.add(
					"INSERT INTO VOUCHER_DETAIL(VOUCHER_DETAIL_ID,VOUCHER_MASTER_ID,COA_CDE,AMNT,LINE_NARRATION,COMPANY_ID,BILL_NBR,BILL_DTE) "
							+ " VALUES(SEQ_VOUCHER_DETAIL_ID.NEXTVAL," + voucherMasterId + ",'" + vo.getVendorAccount()
							+ "'," + String.format("%.2f", amount) + "," + " '"
							+ Util.removeSpecialChar(narration.trim().toUpperCase()) + "'," + vo.getComId() + "," + " "
							+ (vo.getInvoiceNumber() != null
									? "'" + Util.removeSpecialChar(vo.getInvoiceNumber().trim().toUpperCase()) + "'"
									: null)
							+ "," + " "
							+ (vo.getInvoiceNumber() != null ? "TO_DATE('" + vo.getInvoiceDate() + "','DD-MM-YYYY')"
									: null)
							+ ")");

			List<WasaInvoiceDetail> details = vo.getDetail();
			int row_seq_nbr = 0;
			for (WasaInvoiceDetail detail : details) {
				row_seq_nbr++;
				if (vo.getInvoiceType().equalsIgnoreCase("PI")) {
					double amnt = Double.parseDouble((detail.getAmount() != null && !detail.getAmount().isEmpty()
							&& !detail.getAmount().equals("0")) ? detail.getAmount() : "0");
					double itemQty = Double.parseDouble(detail.getItemQty());
					double itemPrice = Double
							.parseDouble((detail.getItemRate() != null && !detail.getItemRate().isEmpty()
									&& !detail.getItemRate().equals("0")) ? detail.getItemRate() : "0");
//	                    String unitType = detail.getUnitType();
//	                    if (unitType.equals("P")) {
//	                        itemQty = (Double.parseDouble(detail.getItemQty()) * Double.parseDouble(detail.getUni));
//	                        itemPrice = (amnt / itemQty);
//	                    }
					String itemNarration = "PURCHASE QTY: " + detail.getItemQty() + " @ "
							+ ((detail.getItemRate() != null && !detail.getItemRate().isEmpty()
									&& !detail.getItemRate().equals("0")) ? detail.getItemRate() : "0")
							+ "";
					if (!vo.getInvoiceNumber().trim().isEmpty()) {
						itemNarration = "PURCHASE QTY: " + detail.getItemQty() + " @ "
								+ ((detail.getItemRate() != null && !detail.getItemRate().isEmpty()
										&& !detail.getItemRate().equals("0")) ? detail.getItemRate() : "0")
								+ " AGAINST INVOICE # "
								+ Util.removeSpecialChar(vo.getInvoiceNumber().trim().toUpperCase()) + "";
					}

					queryList.add(
							"INSERT INTO VOUCHER_DETAIL(VOUCHER_DETAIL_ID,VOUCHER_MASTER_ID,COA_CDE,QTY,RATE,AMNT,LINE_NARRATION,COMPANY_ID,"
									+ " BILL_NBR,BILL_DTE,ITEM_UNIT,ITEM_QTY,ITEM_RATE,UNIT_TYPE,UNIT_CONV,ROW_SEQ_NBR)"
									+ " VALUES(SEQ_VOUCHER_DETAIL_ID.NEXTVAL," + voucherMasterId + ",'"
									+ detail.getItemAccountCode() + "'," + itemQty + ","
									+ String.format("%.2f", itemPrice) + "," + String.format("%.2f", amnt) + "," + " '"
									+ Util.removeSpecialChar(itemNarration).toUpperCase() + "'," + vo.getComId() + ","
									+ " "
									+ (vo.getInvoiceNumber() != null ? "'"
											+ Util.removeSpecialChar(vo.getInvoiceNumber().trim().toUpperCase()) + "'"
											: null)
									+ "," + " "
									+ (vo.getInvoiceNumber() != null
											? "TO_DATE('" + vo.getInvoiceDate() + "','DD-MM-YYYY')"
											: null)
									+ "," + " "
									+ (detail.getUnit() != null && !detail.getUnit().trim().isEmpty()
											? "'" + Util.removeSpecialChar(detail.getUnit().trim().toUpperCase()) + "'"
											: null)
									+ "," + " "
									+ (detail.getItemQty().isEmpty()
											|| detail.getItemQty().equalsIgnoreCase("0") ? null : detail.getItemQty())
									+ "," + " "
									+ ((detail.getItemRate() != null && !detail.getItemRate().isEmpty()
											&& !detail.getItemRate().equals("0")) ? detail.getItemRate() : "0")
									+ "," + " 'S', 1," + row_seq_nbr + ")");

					// Insert FST Code - Credit
//	                    if (vo.getGstaxCode() != null && !vo.getGstaxCode().isEmpty() && !vo.getGstaxCode().equals("0") && !vo.getGstaxAmount()[i].isEmpty() && !vo.getGstaxAmount()[i].equalsIgnoreCase("0")) {
//	                        String gstNarration = "FEDERAL SALE TAX RECEIVABLE " + (vo.getGstaxRate().isEmpty() ? "" : "@ " + vo.getGstaxRate() + "%") + "";
//	                        if (!vo.getInvoiceNumber().trim().isEmpty()) {
//	                            gstNarration = "FEDERAL SALE TAX RECEIVABLE " + (vo.getGstaxRate().isEmpty() ? "" : "@ " + vo.getGstaxRate() + "% ") + "AGAINST INVOICE # " + Util.removeSpecialChar(vo.getInvoiceNumber().trim().toUpperCase()) + "";
//	                        }
//	                        amnt = Double.parseDouble(vo.getGstaxAmount()[i]);
//	                        queryList.add("INSERT INTO VOUCHER_DETAIL(VOUCHER_DETAIL_ID,VOUCHER_MASTER_ID,COA_CDE,AMNT,TAX_ID,TAX_PERC,LINE_NARRATION,REF_VOUCHER_DETAIL_ID,COMPANY_ID,ROW_SEQ_NBR)"
//	                                + " VALUES (SEQ_VOUCHER_DETAIL_ID.NEXTVAL," + voucherMasterId + ", '" + vo.getGstaxCode() + "'," + String.format("%.2f", amnt) + ","
//	                                + " " + (vo.getGstaxId().isEmpty() ? null : vo.getGstaxId()) + "," + (vo.getGstaxRate().isEmpty() ? null : vo.getGstaxRate()) + ","
//	                                + " '" + Util.removeSpecialChar(gstNarration) + "'," + voucherDetailId + "," + vo.getcId() + ",-5)");
//	                    }
					// Insert PST Code - Credit
//	                    if (vo.getProvtaxCode() != null && !vo.getProvtaxCode().isEmpty() && !vo.getProvtaxCode().equals("0") && !vo.getProvtaxAmount()[i].isEmpty() && !vo.getProvtaxAmount()[i].equalsIgnoreCase("0")) {
//	                        String gstNarration = "PROVINCIAL SALE TAX RECEIVABLE " + (vo.getProvtaxRate().isEmpty() ? "" : "@ " + vo.getProvtaxRate() + "%") + "";
//	                        if (!vo.getInvoiceNumber().trim().isEmpty()) {
//	                            gstNarration = "PROVINCIAL SALE TAX RECEIVABLE " + (vo.getProvtaxRate().isEmpty() ? "" : "@ " + vo.getProvtaxRate() + "% ") + "AGAINST INVOICE # " + Util.removeSpecialChar(vo.getInvoiceNumber().trim().toUpperCase()) + "";
//	                        }
//	                        amnt = Double.parseDouble(vo.getProvtaxAmount()[i]);
//	                        queryList.add("INSERT INTO VOUCHER_DETAIL(VOUCHER_DETAIL_ID,VOUCHER_MASTER_ID,COA_CDE,AMNT,TAX_ID,TAX_PERC,LINE_NARRATION,REF_VOUCHER_DETAIL_ID,COMPANY_ID,ROW_SEQ_NBR)"
//	                                + " VALUES (SEQ_VOUCHER_DETAIL_ID.NEXTVAL," + voucherMasterId + ", '" + vo.getProvtaxCode() + "'," + String.format("%.2f", amnt) + ","
//	                                + " " + (vo.getProvtaxId().isEmpty() ? null : vo.getProvtaxId()) + "," + (vo.getProvtaxRate().isEmpty() ? null : vo.getProvtaxRate()) + ","
//	                                + " '" + Util.removeSpecialChar(gstNarration) + "'," + voucherDetailId + "," + vo.getcId() + ",-6)");
//	                    }
				}

			}

			// Insert Advance Income Tax Code - Debit
//	            if (vo.getAdvancetaxCode() != null && !vo.getAdvancetaxCode().isEmpty() && !vo.getAdvancetaxCode().equals("0") && !vo.getAdvancetaxAmount().isEmpty() && !vo.getAdvancetaxAmount().equalsIgnoreCase("0")) {
//	                String gstNarration = "ADVANCE INCOME TAX @ " + (vo.getAdvancetaxRate().isEmpty() ? "" : vo.getAdvancetaxRate() + "%") + "";
//	                if (!vo.getInvoiceNumber().trim().isEmpty()) {
//	                    gstNarration = "ADVANCE INCOME TAX @ " + (vo.getAdvancetaxRate().isEmpty() ? "" : vo.getAdvancetaxRate() + "% ") + "AGAINST INVOICE # " + Util.removeSpecialChar(vo.getInvoiceNumber().trim().toUpperCase()) + "";
//	                }
//	                double amnt = Double.parseDouble(vo.getAdvancetaxAmount());
//	                queryList.add("INSERT INTO VOUCHER_DETAIL(VOUCHER_DETAIL_ID,VOUCHER_MASTER_ID,COA_CDE,AMNT,TAX_ID,TAX_PERC,LINE_NARRATION,COMPANY_ID,ROW_SEQ_NBR)"
//	                        + " VALUES (SEQ_VOUCHER_DETAIL_ID.NEXTVAL," + voucherMasterId + ", '" + vo.getAdvancetaxCode() + "'," + amnt + ","
//	                        + " " + (vo.getAdvancetaxId().isEmpty() ? null : vo.getAdvancetaxId()) + "," + (vo.getAdvancetaxRate().isEmpty() ? null : vo.getAdvancetaxRate()) + ","
//	                        + " '" + Util.removeSpecialChar(gstNarration) + "'," + vo.getcId() + ",-7)");
//	            }
//	            queryList.add("call UPDATE_INVENTORY_CONTROL(" + voucherMasterId + "," + vo.getVoucherSubTypeId() + ")");
//	            queryList.add("call UPDATE_FINANCIAL_CONTROL(" + voucherMasterId + ")");
//	            queryList.add("call UPDAET_DEPARTMENTS(" + voucherMasterId + "," + vo.getcId() + ")");
			flag = this.dao.insertAll(queryList, vo.getCreatedBy());
			if (flag) {
				// update history
				updateList = new ArrayList();
//	                updateList.add("call UPDATE_HISTORY(" + voucherMasterId + ",2,'" + vo.getCreatedBy() + "'," + vo.getcId() + ",NULL,'" + vo.getLastIPAddress() + "','" + vo.getLastBrowserDetail() + "')");
//	                taskExecutor.execute(new UpdateCostPriceTask(updateList, this.dao, vo.getCreatedBy()));
				// update item cost price
				String sqlquery = "SELECT VD.SUB_COA_CDE,VD.COMPANY_ID,MIN(VD.VOUCHER_MASTER_ID) VOUCHER_MASTER_ID"
						+ " FROM VOUCHER_DETAIL VD,VOUCHER_MASTER VM,VOUCHER_SUB_TYPE VST,CHART_OF_ACCOUNT COA,COA_TYPE CT"
						+ " WHERE VD.VOUCHER_MASTER_ID=VM.VOUCHER_MASTER_ID"
						+ "  AND VM.VOUCHER_SUB_TYP_ID=VST.VOUCHER_SUB_TYP_ID AND VST.VOUCHER_TYP NOT IN ('PV','RV','JV')"
						+ "  AND VD.SUB_COA_CDE IS NOT NULL AND VD.SUB_COA_CDE=COA.COA_CDE AND VD.COMPANY_ID=COA.COMPANY_ID"
						+ "  AND COA.COA_TYPE_ID=CT.COA_TYPE_ID AND CT.DESCRIPTION='INVENTORY' AND VD.ROW_SEQ_NBR<>-99"
						+ "  AND VM.VOUCHER_MASTER_ID IN (" + voucherMasterId + ") "
						+ " GROUP BY VD.SUB_COA_CDE,VD.COMPANY_ID";

				List list = this.dao.getData(sqlquery);
				if (list != null && !list.isEmpty()) {
					for (int d = 0; d < list.size(); d++) {
						Map map = (Map) list.get(d);
						updateList = new ArrayList();
						updateList.add("call UPDAET_COSTPRICE_ITM(" + map.get("VOUCHER_MASTER_ID").toString() + ",'"
								+ map.get("SUB_COA_CDE").toString() + "')");
//	                        taskExecutor.execute(new UpdateCostPriceTask(updateList, this.dao, vo.getCreatedBy()));
					}
				}
			}
//	            if (flag && vo.getSavedStatus() != null && vo.getSavedStatus().equals("save_posting")) {
//	                String[] query = new String[1];
//	                query[0] = "call VOUCHER_APPROVING(" + voucherMasterId + ",'POST_ON_SAVING','" + vo.getCreatedBy() + "')";
//	                flag = this.dao.insertAll(query, vo.getCreatedBy());
//	                if (flag) {
//	                    //update history
//	                    updateList = new ArrayList();
//	                    updateList.add("call UPDATE_HISTORY(" + voucherMasterId + ",5,'" + vo.getCreatedBy() + "'," + vo.getcId() + ",NULL,'" + vo.getLastIPAddress() + "','" + vo.getLastBrowserDetail() + "')");
////	                    taskExecutor.execute(new UpdateCostPriceTask(updateList, this.dao, vo.getCreatedBy()));
//	                }
//	            }
		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
			Sentry.captureException(ex);
			Sentry.captureMessage(ex.getMessage());
			returnMap.put("error", ex.getMessage());
		} finally {
			if (flag) {
				returnMap.put("voucherMasterId", voucherMasterId);
				returnMap.put("error", null);
			} else {
				returnMap.put("voucherMasterId", "");
				if (!queryList.isEmpty()) {
					System.out.println("Queries List => " + Arrays.toString(queryList.toArray()));
				}
			}
		}
		return returnMap;
	}

	public boolean validateCompanyInfo(String key, String companyId) {

		return false;
	}

}
