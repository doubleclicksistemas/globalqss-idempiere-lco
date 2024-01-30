package com.ingeint.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

import org.compiere.model.MConversionRate;
import org.compiere.model.MInvoice;
import org.compiere.util.Env;

/**
 * 
 * @author Argenis Rodríguez
 *
 */
public class ConversionUtil {
	
	public static BigDecimal getInvoiceRate(MInvoice invoice, int C_CurrFrom_ID, int C_CurrTo_ID) {
		
		if (C_CurrFrom_ID == C_CurrTo_ID)
			return BigDecimal.ONE;
		
		int CurrencySchema_ID = Env.getContextAsInt(invoice.getCtx(), Env.C_CURRENCY_ID);
		int C_CurrencyTo_ID = invoice.get_ValueAsInt(IngeintConstants.COLUMNNAME_C_CurrencyTo_ID);
		
		if (invoice.getC_Currency_ID() == C_CurrFrom_ID
				&& invoice.getC_Currency_ID() != CurrencySchema_ID
				&& C_CurrTo_ID == CurrencySchema_ID
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum() > 0
			|| invoice.getC_Currency_ID() == C_CurrFrom_ID
				&& C_CurrFrom_ID == CurrencySchema_ID
				&& C_CurrTo_ID == C_CurrencyTo_ID
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum() > 0)
			return invoice.getCurrencyRate();
		
		return MConversionRate.getRate(C_CurrFrom_ID, C_CurrTo_ID
				, invoice.getDateAcct(), invoice.getC_ConversionType_ID()
				, invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
	}
	
	public static BigDecimal convertInvoice(BigDecimal amount, MInvoice invoice, int C_Currency_ID, boolean toInvoiceCurrency
			, int precision) {
		
		if (amount == null)
			throw new IllegalArgumentException("Required parameter missing - Amt");
		
		if (invoice.getC_Currency_ID() == C_Currency_ID || BigDecimal.ZERO.compareTo(amount) == 0)
			return amount;
		
		int C_Currency1_ID = Env.getContextAsInt(invoice.getCtx(), Env.C_CURRENCY_ID);
		
		if (C_Currency_ID == C_Currency1_ID
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum() > 0)
		{
			//BigDecimal retValue = amount.multiply(invoice.getCurrencyRate());
			BigDecimal retValue = toInvoiceCurrency
					? amount.divide(invoice.getCurrencyRate(), precision, RoundingMode.HALF_UP)
					: amount.multiply(invoice.getCurrencyRate());
			
			if (retValue.scale() > precision)
				retValue = retValue.setScale(precision, RoundingMode.HALF_UP);
			
			return retValue;
		}
		
		int C_CurrFrom_ID = toInvoiceCurrency ? C_Currency_ID : invoice.getC_Currency_ID();
		int C_CurrTo_ID = toInvoiceCurrency ? invoice.getC_Currency_ID(): C_Currency_ID;
		
		return convertByMajorRate(amount, C_CurrFrom_ID, C_CurrTo_ID
				, invoice.getDateAcct(), invoice.getC_ConversionType_ID()
				, invoice.getAD_Client_ID(), invoice.getAD_Org_ID()
				, precision);
	}
	
	/**
	 * 
	 * Convert to Major rate
	 * @author Argenis Rodríguez
	 * @param amt
	 * @param C_Currency_ID
	 * @param C_CurrencyTo_ID
	 * @param conversionDate
	 * @param C_ConversionType_ID
	 * @param AD_Client_ID
	 * @param AD_Org_ID
	 * @param precision
	 * @return
	 */
	public static BigDecimal convertByMajorRate(BigDecimal amt, int C_Currency_ID, int C_CurrencyTo_ID
			, Timestamp conversionDate, int C_ConversionType_ID
			, int AD_Client_ID, int AD_Org_ID, int precision) {
		if (amt == null)
			throw new IllegalArgumentException("Required parameter missing - Amt");
		if (C_Currency_ID == C_CurrencyTo_ID || amt.compareTo(Env.ZERO)==0)
			return amt;
		
		boolean divide = false;
		BigDecimal retValue = MConversionRate.getRate(C_Currency_ID, C_CurrencyTo_ID
				, conversionDate, C_ConversionType_ID
				, AD_Client_ID, AD_Org_ID);
		
		if (retValue == null)
			return null;
		
		if (divide = BigDecimal.ONE.compareTo(retValue) > 0)
			retValue = MConversionRate.getRate(C_CurrencyTo_ID, C_Currency_ID
					, conversionDate, C_ConversionType_ID
					, AD_Client_ID, AD_Org_ID);
		
		retValue = divide
				? amt.divide(retValue, precision, RoundingMode.HALF_UP)
				: amt.multiply(retValue);
		
		if (retValue.scale() > precision)
			retValue = retValue.setScale(precision, RoundingMode.HALF_UP);
		
		return retValue;
	}
}
