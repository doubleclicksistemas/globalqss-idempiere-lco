package com.ingeint.acct;

import java.math.BigDecimal;

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine_Allocation;
import org.compiere.model.MAllocationLine;

public class INGDocLine_Allocation extends DocLine_Allocation {
	
	public INGDocLine_Allocation(MAllocationLine line, Doc doc) {
		super(line, doc);
	}
	
	@Override
	public void setC_ConversionType_ID(int C_ConversionType_ID) {
		super.setC_ConversionType_ID(C_ConversionType_ID);
	}
	
	@Override
	public void setCurrencyRate(BigDecimal currencyRate) {
		super.setCurrencyRate(currencyRate);
	}
}
