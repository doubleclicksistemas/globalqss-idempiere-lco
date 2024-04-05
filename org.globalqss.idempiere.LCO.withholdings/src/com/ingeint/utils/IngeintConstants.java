package com.ingeint.utils;

import java.math.BigDecimal;
import java.util.Optional;

import org.compiere.model.PO;

public class IngeintConstants {
	
	//-----------------------------------------------Column Names------------------------------------------------------------------
	public static final String COLUMNNAME_C_Currency_ID = "C_Currency_ID";
	public static final String COLUMNNAME_ConvertedTaxBaseAmt = "ConvertedTaxBaseAmt";
	public static final String COLUMNNAME_ConvertedTaxAmt = "ConvertedTaxAmt";
	public static final String COLUMNNAME_C_CurrencyTo_ID = "C_CurrencyTo_ID";
	public static final String COLUMNNAME_ConversionRate = "ConversionRate";
	public static final String COLUMNNAME_WriteOffInvoice = "WriteOffInvoice";
	public static final String COLUMNNAME_WriteOffPayment = "WriteOffPayment";
	
	//--------------------------------------------------Sys Configs----------------------------------------------------------------
	public static final String SYSCONFIG_LVE_GENERATE_WITHHOLDINGS_WITH_LIST_PRECISION = "LVE_GENERATE_WITHHOLDINGS_WITH_LIST_PRECISION";
	public static final String SYSCONFIG_LVE_DROP_ALLOCATION_DIFFERENCIAL = "LVE_DROP_ALLOCATION_DIFFERENCIAL";
	public static final String SYSCONFIG_PRECISION_RATE = "PRECISION_RATE";
	public static final String SYSCONFIG_POST_DOCUMENT_DATE = "POST_DOCUMENT_DATE";
	
	public static BigDecimal getvalueAsBigDecimal(PO po, String columnName) {
		return Optional.ofNullable((BigDecimal) po.get_Value(columnName))
				.orElse(BigDecimal.ZERO);
	}
	
	public static BigDecimal getOldValueAsBigdecimal(PO po, String columnName) {
		return Optional.ofNullable((BigDecimal) po.get_ValueOld(columnName))
				.orElse(BigDecimal.ZERO);
	}
	
	public static int getOldValueAsInt(PO po, String columnName) {
		return Optional.ofNullable((Integer) po.get_ValueOld(columnName))
				.orElse(0);
	}
}
