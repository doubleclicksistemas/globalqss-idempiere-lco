package com.ingeint.acct;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine_Allocation;
import org.compiere.model.MAllocationLine;

public class INGDocLine_Allocation extends DocLine_Allocation {
	
	private int m_AD_Org_ID = -1;
	private Timestamp m_DateConv = null;
	
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
	
	public void setAD_Org_ID(int AD_Org_ID) {
		m_AD_Org_ID = AD_Org_ID;
	}
	
	@Override
	public int getAD_Org_ID() {
		
		if (m_AD_Org_ID > 0)
			return m_AD_Org_ID;
		
		return super.getAD_Org_ID();
	}
	
	public void setDateConv(Timestamp dateConv) {
		m_DateConv = dateConv;
	}
	
	@Override
	public Timestamp getDateConv() {
		if (m_DateConv != null)
			return m_DateConv;
		
		return super.getDateConv();
	}
}
