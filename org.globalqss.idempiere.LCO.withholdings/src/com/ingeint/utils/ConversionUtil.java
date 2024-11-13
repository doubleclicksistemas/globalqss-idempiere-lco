package com.ingeint.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Properties;

import org.compiere.model.GridTab;
import org.compiere.model.MConversionRate;
import org.compiere.model.MCurrency;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MSysConfig;
import org.compiere.util.Env;

/**
 * 
 * @author Argenis Rodríguez
 *
 */
public class ConversionUtil {
	
	public static BigDecimal currencyConvertPayment(Properties ctx, int C_Payment_ID
			, BigDecimal amount, int C_CurrFrom_ID
			, int C_CurrTo_ID, Timestamp date, String trxName) {
		
		if (C_CurrFrom_ID == C_CurrTo_ID)
			return amount;
		
		MPayment payment = new MPayment(ctx, C_Payment_ID, trxName);
		
		int baseCurrencyId = Env.getContextAsInt(ctx, Env.C_CURRENCY_ID);
		int C_CurrencyTo_ID = payment.get_ValueAsInt(IngeintConstants.COLUMNNAME_C_CurrencyTo_ID);
		int stdPrecision = MCurrency.getStdPrecision(ctx, C_CurrTo_ID);
		BigDecimal conversionRate = (BigDecimal) payment.get_Value(IngeintConstants.COLUMNNAME_ConversionRate);
		
		//Evaluatee Override Multiply Rate
		if (payment.getC_Currency_ID() == C_CurrFrom_ID
				&& payment.getC_Currency_ID() != baseCurrencyId
				&& C_CurrTo_ID == baseCurrencyId
				&& payment.isOverrideCurrencyRate()
				&& payment.getCurrencyRate() != null
				&& payment.getCurrencyRate().signum() > 0
			|| payment.getC_Currency_ID() == C_CurrFrom_ID
				&& payment.getC_Currency_ID() == baseCurrencyId
				&& C_CurrTo_ID == C_CurrencyTo_ID
				&& payment.isOverrideCurrencyRate()
				&& payment.getCurrencyRate() != null
				&& payment.getCurrencyRate().signum() > 0)
		{
			amount = amount.multiply(payment.getCurrencyRate());
			
			if (amount.scale() > stdPrecision)
				amount = amount.setScale(stdPrecision, RoundingMode.HALF_UP);
		}
		//Evaluate Divide Override Rate
		else if (payment.getC_Currency_ID() != baseCurrencyId
				&& C_CurrFrom_ID == baseCurrencyId
				&& C_CurrTo_ID == payment.getC_Currency_ID()
				&& payment.isOverrideCurrencyRate()
				&& payment.getCurrencyRate() != null
				&& payment.getCurrencyRate().signum() > 0)
		{
			amount = amount.divide(payment.getCurrencyRate(), stdPrecision, RoundingMode.HALF_UP);
		}
		else if (payment.getC_Currency_ID() == baseCurrencyId
				&& C_CurrFrom_ID == C_CurrencyTo_ID
				&& C_CurrTo_ID == baseCurrencyId
				&& payment.isOverrideCurrencyRate()
				&& conversionRate != null
				&& conversionRate.signum() > 0)
		{
			amount = amount.multiply(conversionRate);
			
			if (amount.scale() > stdPrecision)
				amount = amount.setScale(stdPrecision, RoundingMode.HALF_UP);
		}
		else
		{
			amount = MConversionRate.convert(ctx, amount
					, C_CurrFrom_ID, C_CurrTo_ID
					, date, payment.getC_ConversionType_ID()
					, payment.getAD_Client_ID(), payment.getAD_Org_ID());
		}
		
		return amount;
	}
	
	public static BigDecimal getPaymentCurrencyRate(Properties ctx, GridTab tab
			, int C_CurrFrom_ID, int C_CurrTo_ID, Timestamp date) {
		
		if (C_CurrFrom_ID == C_CurrTo_ID)
			return BigDecimal.ONE;
		
		int baseCurrencyId = Env.getContextAsInt(ctx, Env.C_CURRENCY_ID);
		
		int C_CurrencyTo_ID = Optional.ofNullable((Integer) tab.getValue(IngeintConstants.COLUMNNAME_C_CurrencyTo_ID))
				.orElse(0);
		
		BigDecimal conversionRate = (BigDecimal) tab.getValue(IngeintConstants.COLUMNNAME_ConversionRate);
		
		boolean isOverrideCurrencyRate = tab.getValueAsBoolean(MPayment.COLUMNNAME_IsOverrideCurrencyRate);
		
		BigDecimal currencyRate = (BigDecimal) tab.getValue(MPayment.COLUMNNAME_CurrencyRate);
		
		int C_ConversionType_ID = Optional.ofNullable((Integer) tab.getValue(MPayment.COLUMNNAME_C_ConversionType_ID))
				.orElse(0);
		
		int C_CurrencyPayment_ID = Optional.ofNullable((Integer) tab.getValue(MPayment.COLUMNNAME_C_Currency_ID))
				.orElse(0);
		
		int AD_Client_ID = Env.getAD_Client_ID(ctx);
		
		int AD_Org_ID = Optional.ofNullable((Integer) tab.getValue(MPayment.COLUMNNAME_AD_Org_ID))
				.orElse(0);
		
		//Evaluatee Override Multiply Rate
		if (C_CurrencyPayment_ID == C_CurrFrom_ID
				&& C_CurrencyPayment_ID != baseCurrencyId
				&& C_CurrTo_ID == baseCurrencyId
				&& isOverrideCurrencyRate
				&& currencyRate != null
				&& currencyRate.signum() > 0
			|| C_CurrencyPayment_ID == C_CurrFrom_ID
				&& C_CurrencyPayment_ID == baseCurrencyId
				&& C_CurrTo_ID == C_CurrencyTo_ID
				&& isOverrideCurrencyRate
				&& currencyRate != null
				&& currencyRate.signum() > 0)
		{
			return currencyRate;
		}
		//Evaluate Divide Override Rate
		else if (C_CurrencyPayment_ID != baseCurrencyId
				&& C_CurrFrom_ID == baseCurrencyId
				&& C_CurrTo_ID == C_CurrencyPayment_ID
				&& isOverrideCurrencyRate
				&& currencyRate != null
				&& currencyRate.signum() > 0)
		{
			int precision = MSysConfig.getIntValue(IngeintConstants.SYSCONFIG_PRECISION_RATE, 12
					, AD_Client_ID, AD_Org_ID);
			
			return BigDecimal.ONE.divide(currencyRate, precision, RoundingMode.HALF_UP);
		}
		else if (C_CurrencyPayment_ID == baseCurrencyId
				&& C_CurrFrom_ID == C_CurrencyTo_ID
				&& C_CurrTo_ID == baseCurrencyId
				&& isOverrideCurrencyRate
				&& conversionRate != null
				&& conversionRate.signum() > 0)
		{
			return conversionRate;
		}
		else
		{
			return MConversionRate.getRate(C_CurrFrom_ID, C_CurrTo_ID
					, date, C_ConversionType_ID
					, AD_Client_ID, AD_Org_ID);
		}
	}
	
	public static BigDecimal getPaymentCurrencyRate(MPayment payment, int C_CurrFrom_ID
			, int C_CurrTo_ID, Timestamp date) {
		
		int baseCurrencyId = Env.getContextAsInt(payment.getCtx(), Env.C_CURRENCY_ID);
		int C_CurrencyTo_ID = payment.get_ValueAsInt(IngeintConstants.COLUMNNAME_C_CurrencyTo_ID);
		BigDecimal conversionRate = (BigDecimal) payment.get_Value(IngeintConstants.COLUMNNAME_ConversionRate);
		
		//Evaluatee Override Multiply Rate
		if (payment.getC_Currency_ID() == C_CurrFrom_ID
				&& payment.getC_Currency_ID() != baseCurrencyId
				&& C_CurrTo_ID == baseCurrencyId
				&& payment.isOverrideCurrencyRate()
				&& payment.getCurrencyRate() != null
				&& payment.getCurrencyRate().signum() > 0
			|| payment.getC_Currency_ID() == C_CurrFrom_ID
				&& payment.getC_Currency_ID() == baseCurrencyId
				&& C_CurrTo_ID == C_CurrencyTo_ID
				&& payment.isOverrideCurrencyRate()
				&& payment.getCurrencyRate() != null
				&& payment.getCurrencyRate().signum() > 0)
		{
			return payment.getCurrencyRate();
		}
		//Evaluate Divide Override Rate
		else if (payment.getC_Currency_ID() != baseCurrencyId
				&& C_CurrFrom_ID == baseCurrencyId
				&& C_CurrTo_ID == payment.getC_Currency_ID()
				&& payment.isOverrideCurrencyRate()
				&& payment.getCurrencyRate() != null
				&& payment.getCurrencyRate().signum() > 0)
		{
			int precision = MSysConfig.getIntValue(IngeintConstants.SYSCONFIG_PRECISION_RATE, 12
					, payment.getAD_Client_ID(), payment.getAD_Org_ID());
			
			return BigDecimal.ONE.divide(payment.getCurrencyRate(), precision, RoundingMode.HALF_UP);
		}
		else if (payment.getC_Currency_ID() == baseCurrencyId
				&& C_CurrFrom_ID == C_CurrencyTo_ID
				&& C_CurrTo_ID == baseCurrencyId
				&& payment.isOverrideCurrencyRate()
				&& conversionRate != null
				&& conversionRate.signum() > 0)
		{
			return conversionRate;
		}
		else
		{
			return MConversionRate.getRate(C_CurrFrom_ID, C_CurrTo_ID
					, date, payment.getC_ConversionType_ID()
					, payment.getAD_Client_ID(), payment.getAD_Org_ID());
		}
	}
	
	public static BigDecimal getPaymentCurrencyRate(Properties ctx, int C_Payment_ID
			, int C_CurrFrom_ID, int C_CurrTo_ID, Timestamp date, String trxName) {
		if (C_CurrFrom_ID == C_CurrTo_ID)
			return BigDecimal.ONE;
		
		MPayment payment = new MPayment(ctx, C_Payment_ID, trxName);
		
		return getPaymentCurrencyRate(payment, C_CurrFrom_ID, C_CurrTo_ID, date);
	}
	
	public static BigDecimal currencyRateInvoice(MInvoice invoice, int C_CurrFrom_ID
			, int C_CurrTo_ID, Timestamp date) {
		
		int baseCurrencyId = Env.getContextAsInt(invoice.getCtx(), Env.C_CURRENCY_ID);
		int C_CurrencyTo_ID = invoice.get_ValueAsInt(IngeintConstants.COLUMNNAME_C_CurrencyTo_ID);
		BigDecimal conversionRate = (BigDecimal) invoice.get_Value(IngeintConstants.COLUMNNAME_ConversionRate);
		
		//Multiply Conditions
		if (invoice.getC_Currency_ID() == C_CurrFrom_ID
				&& invoice.getC_Currency_ID() != baseCurrencyId
				&& C_CurrTo_ID == baseCurrencyId
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum() > 0
			|| invoice.getC_Currency_ID() == C_CurrFrom_ID
				&& invoice.getC_Currency_ID() == baseCurrencyId
				&& C_CurrTo_ID == C_CurrencyTo_ID
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum() > 0)
		{
			return invoice.getCurrencyRate();
		}
		//Divide Conditions Inversal Override
		else if (invoice.getC_Currency_ID() != baseCurrencyId
				&& C_CurrFrom_ID == baseCurrencyId
				&& C_CurrTo_ID == invoice.getC_Currency_ID()
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum()> 0)
		{
			int precision = MSysConfig.getIntValue(IngeintConstants.SYSCONFIG_PRECISION_RATE, 12
					, invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
			return BigDecimal.ONE.divide(invoice.getCurrencyRate(), precision, RoundingMode.HALF_UP);
		}
		else if (invoice.getC_Currency_ID() == baseCurrencyId
				&& C_CurrFrom_ID == C_CurrencyTo_ID
				&& C_CurrTo_ID == baseCurrencyId
				&& invoice.isOverrideCurrencyRate()
				&& conversionRate != null
				&& conversionRate.signum() > 0)
		{
			return conversionRate;
		}
		else
		{
			return MConversionRate.getRate(C_CurrFrom_ID, C_CurrTo_ID
					, date, invoice.getC_ConversionType_ID()
					, invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
		}
	}
	
	public static BigDecimal currencyRateInvoice(Properties ctx, int C_Invoice_ID
			, int C_CurrFrom_ID, int C_CurrTo_ID, Timestamp date, String trxName) {
		if (C_CurrFrom_ID == C_CurrTo_ID)
			return BigDecimal.ONE;
		
		MInvoice invoice = new MInvoice(ctx, C_Invoice_ID, trxName);
		
		return currencyRateInvoice(invoice, C_CurrFrom_ID, C_CurrTo_ID, date);
	}
	
	
	
	public static BigDecimal currencyConvertInvoice(Properties ctx, int C_Invoice_ID
			, BigDecimal amount, int C_CurrFrom_ID
			, int C_CurrTo_ID, Timestamp date, String trxName) {
		
		if (C_CurrFrom_ID == C_CurrTo_ID)
			return amount;
		
		MInvoice invoice = new MInvoice(ctx, C_Invoice_ID, trxName);
		
		int baseCurrencyId = Env.getContextAsInt(ctx, Env.C_CURRENCY_ID);
		int C_CurrencyTo_ID = invoice.get_ValueAsInt(IngeintConstants.COLUMNNAME_C_CurrencyTo_ID);
		int stdPrecision = MCurrency.getStdPrecision(ctx, C_CurrTo_ID);
		BigDecimal conversionRate = (BigDecimal) invoice.get_Value(IngeintConstants.COLUMNNAME_ConversionRate);
		
		//Multiply Conditions
		if (invoice.getC_Currency_ID() == C_CurrFrom_ID
				&& invoice.getC_Currency_ID() != baseCurrencyId
				&& C_CurrTo_ID == baseCurrencyId
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum() > 0
			|| invoice.getC_Currency_ID() == C_CurrFrom_ID
				&& invoice.getC_Currency_ID() == baseCurrencyId
				&& C_CurrTo_ID == C_CurrencyTo_ID
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum() > 0)
		{
			amount = amount.multiply(invoice.getCurrencyRate());
			
			if (amount.scale() > stdPrecision)
				amount = amount.setScale(stdPrecision, RoundingMode.HALF_UP);
		}
		//Divide Conditions Inversal Override
		else if (invoice.getC_Currency_ID() != baseCurrencyId
				&& C_CurrFrom_ID == baseCurrencyId
				&& C_CurrTo_ID == invoice.getC_Currency_ID()
				&& invoice.isOverrideCurrencyRate()
				&& invoice.getCurrencyRate() != null
				&& invoice.getCurrencyRate().signum()> 0)
		{
			amount = amount.divide(invoice.getCurrencyRate(), stdPrecision, RoundingMode.HALF_UP);
		}
		else if (invoice.getC_Currency_ID() == baseCurrencyId
				&& C_CurrFrom_ID == C_CurrencyTo_ID
				&& C_CurrTo_ID == baseCurrencyId
				&& invoice.isOverrideCurrencyRate()
				&& conversionRate != null
				&& conversionRate.signum() > 0)
		{
			amount = amount.multiply(conversionRate);
			
			if (amount.scale() > stdPrecision)
				amount = amount.setScale(stdPrecision, RoundingMode.HALF_UP);
		}
		else
		{
			amount = MConversionRate.convert(ctx, amount
					, C_CurrFrom_ID, C_CurrTo_ID
					, date, invoice.getC_ConversionType_ID()
					, invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
		}
		
		return amount;
	}
	
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
			, Timestamp dateDoc, int precision) {
		
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
				, invoice.getDateInvoiced(), invoice.getC_ConversionType_ID()
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
	
	public static BigDecimal multiply(BigDecimal n1, BigDecimal n2, int scale) {
		BigDecimal result = n1.multiply(n2);
		
		if (result.scale() > scale)
			result = result.setScale(scale, RoundingMode.HALF_UP);
		
		return result;
	}
}
