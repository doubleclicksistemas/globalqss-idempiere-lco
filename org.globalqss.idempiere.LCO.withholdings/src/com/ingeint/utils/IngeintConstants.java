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
	public static final String COLUMNNAME_C_BaseTax_ID = "C_BaseTax_ID";
	public static final String COLUMNNAME_ExemptAmt = "ExemptAmt";
	public static final String COLUMNNAME_ExemptAmtCurrency = "ExemptAmtCurrency";
	public static final String COLUMNNAME_TaxAmtCurrency = "TaxAmtCurrency";
	public static final String COLUMNNAME_TaxBaseAmtCurrency = "TaxBaseAmtCurrency";
	public static final String COLUMNNAME_TotalInvoice = "TotalInvoice";
	public static final String COLUMNNAME_TotalInvoiceCurrency = "TotalInvoiceCurrency";
	public static final String COLUMNNAME_Aliquote = "Aliquote";
	public static final String COLUMNNAME_TotalBaseAmt = "TotalBaseAmt";
	public static final String COLUMNNAME_TotalBaseAmtCurrency = "TotalBaseAmtCurrency";
	
	//--------------------------------------------------Sys Configs----------------------------------------------------------------
	public static final String SYSCONFIG_LVE_GENERATE_WITHHOLDINGS_WITH_LIST_PRECISION = "LVE_GENERATE_WITHHOLDINGS_WITH_LIST_PRECISION";
	public static final String SYSCONFIG_LVE_DROP_ALLOCATION_DIFFERENCIAL = "LVE_DROP_ALLOCATION_DIFFERENCIAL";
	public static final String SYSCONFIG_PRECISION_RATE = "PRECISION_RATE";
	public static final String SYSCONFIG_POST_DOCUMENT_DATE = "POST_DOCUMENT_DATE";
	public static final String SYSCONFIG_VOUCHER_CURRENCY_TO = "VOUCHER_CURRENCY_TO";
	
	public static final String ATTRIBUTE_TESTALLOCATION = "TESTALLOCATION";
	public static final String ATTRIBUTE_RECHECKLINES = "RECHECKLINES";
	
	public static BigDecimal getvalueAsBigDecimal(PO po, String columnName) {
		return Optional.ofNullable((BigDecimal) po.get_Value(columnName))
				.orElse(BigDecimal.ZERO);
	}
}
