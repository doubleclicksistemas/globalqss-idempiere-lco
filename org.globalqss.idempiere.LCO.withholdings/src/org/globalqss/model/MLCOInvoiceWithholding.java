/**********************************************************************
* This file is part of iDempiere ERP Open Source                      *
* http://www.idempiere.org                                            *
*                                                                     *
* Copyright (C) Contributors                                          *
*                                                                     *
* This program is free software; you can redistribute it and/or       *
* modify it under the terms of the GNU General Public License         *
* as published by the Free Software Foundation; either version 2      *
* of the License, or (at your option) any later version.              *
*                                                                     *
* This program is distributed in the hope that it will be useful,     *
* but WITHOUT ANY WARRANTY; without even the implied warranty of      *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
* GNU General Public License for more details.                        *
*                                                                     *
* You should have received a copy of the GNU General Public License   *
* along with this program; if not, write to the Free Software         *
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
* MA 02110-1301, USA.                                                 *
*                                                                     *
* Contributors:                                                       *
* - Carlos Ruiz - globalqss                                           *
**********************************************************************/

package org.globalqss.model;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Function;

import org.compiere.model.MInvoice;
import org.compiere.model.Query;
import org.compiere.util.CLogger;

/**
 *	Invoice Withholding Model
 *
 *  @author Carlos Ruiz - globalqss
 */
public class MLCOInvoiceWithholding extends X_LCO_InvoiceWithholding
{
	/**
	 *
	 */
	private static final long serialVersionUID = -3086189821486687908L;
	/**	Static Logger	*/
	@SuppressWarnings("unused")
	private static CLogger	s_log	= CLogger.getCLogger (MLCOInvoiceWithholding.class);
	
	private MInvoice m_invoice = null;
	
	/**************************************************************************
	 * 	Default Constructor
	 *	@param ctx context
	 *	@param MLCOInvoiceWithholding_ID id
	 *	@param trxName transaction
	 */
	public MLCOInvoiceWithholding (Properties ctx, int MLCOInvoiceWithholding_ID, String trxName)
	{
		super(ctx, MLCOInvoiceWithholding_ID, trxName);
	}	//	MLCOInvoiceWithholding

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MLCOInvoiceWithholding(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MLCOInvoiceWithholding
	
	public MLCOInvoiceWithholding(Properties ctx, MLCOInvoiceWithholding copy, String trxName) {
		this(ctx, 0, trxName);
		copyPO(copy);
	}
	
	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord
	 *	@return true if save
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		log.fine("New=" + newRecord);
		MInvoice inv = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
		if (inv.getReversal_ID() <= 0) {
			if (getLCO_WithholdingRule_ID() > 0) {
				
				// Fill isCalcOnPayment according to rule
				X_LCO_WithholdingRule wr = new X_LCO_WithholdingRule(getCtx(), getLCO_WithholdingRule_ID(), get_TrxName());
				X_LCO_WithholdingCalc wc = new X_LCO_WithholdingCalc(getCtx(), wr.getLCO_WithholdingCalc_ID(), get_TrxName());
				setIsCalcOnPayment( ! wc.isCalcOnInvoice() );

			} else {
				
				if (inv.isProcessed()) {
					setIsCalcOnPayment(true);
				}
			}

			// Fill DateTrx and DateAcct for isCalcOnInvoice according to the invoice
			if (getC_AllocationLine_ID() <= 0) {
				X_LCO_WithholdingType wt = (X_LCO_WithholdingType) getLCO_WithholdingType();
				if (wt.isOverrideDateOnAllocation())
				{
					setDateTrx(inv.getDateInvoiced());
					setDateAcct(inv.getDateAcct());
				}
			}
		}
		
		return true;
	}	//	beforeSave
	
	public static int sortByOrgAndInvoice(MLCOInvoiceWithholding wh1, MLCOInvoiceWithholding wh2) {
		
		if (wh1.getAD_Org_ID() < wh2.getAD_Org_ID())
			return -1;
		else if (wh1.getAD_Org_ID() > wh2.getAD_Org_ID())
			return 1;
		
		return (wh1.getC_Invoice_ID() < wh2.getC_Invoice_ID()) ? -1
				: (wh1.getC_Invoice_ID() == wh2.getC_Invoice_ID()) ? 0 : 1;
	}
	
	public static MLCOInvoiceWithholding[] getFromInvoice(Properties ctx, int C_Invoice_ID, String trxName) {
		return new Query(ctx, Table_Name, "C_Invoice_ID = ?", trxName)
				.setParameters(C_Invoice_ID)
				.setOnlyActiveRecords(true)
				.list()
				.toArray(MLCOInvoiceWithholding[]::new);
	}
	
	public static MLCOInvoiceWithholding[] getFromInvoice(MInvoice invoice) {
		return getFromInvoiceAndSet(invoice, inv -> getFromInvoice(inv.getCtx(), inv.get_ID(), inv.get_TrxName()));
	}
	
	public static MLCOInvoiceWithholding[] getAutomaticAllocationWithholding(MInvoice invoice, boolean automaticAllocation) {
		return getFromInvoiceAndSet(invoice
				, inv -> getAutomaticAllocationWithholding(inv.getCtx(), inv.get_ID()
						, automaticAllocation, inv.get_TrxName()));
	}
	
	public static MLCOInvoiceWithholding[] getFromInvoiceAndSet(MInvoice invoice
			, Function<MInvoice, MLCOInvoiceWithholding[]> f) {
		return Arrays.stream(f.apply(invoice))
				.peek(withholding -> withholding.setInvoice(invoice))
				.toArray(MLCOInvoiceWithholding[]::new);
	}
	
	public static MLCOInvoiceWithholding[] getAutomaticAllocationWithholding(Properties ctx
			, int C_Invoice_ID, boolean automaticAllocation
			, String trxName) {
		
		return new Query(ctx, Table_Name, "LCO_InvoiceWithholding.C_Invoice_ID = ? AND wt.IsAllocateWithholdingAuto = ?", trxName)
				.addJoinClause("INNER JOIN LCO_WithholdingType wt ON wt.LCO_WithholdingType_ID = LCO_InvoiceWithholding.LCO_WithholdingType_ID")
				.setParameters(C_Invoice_ID, automaticAllocation)
				.setOnlyActiveRecords(true)
				.setOrderBy(COLUMNNAME_LCO_WithholdingType_ID)
				.list()
				.toArray(MLCOInvoiceWithholding[]::new);
	}
	
	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return saved
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (!success)
			return success;

		return LCO_MInvoice.updateHeaderWithholding(getC_Invoice_ID(), get_TrxName());
	}	//	afterSave
	
	/**
	 * 	After Delete
	 *	@param success success
	 *	@return deleted
	 */
	protected boolean afterDelete (boolean success)
	{
		if (!success)
			return success;
		return LCO_MInvoice.updateHeaderWithholding(getC_Invoice_ID(), get_TrxName());
	}	//	afterDelete
	
	public void setInvoice(MInvoice invoice) {
		setC_Invoice_ID(invoice.get_ID());
		m_invoice = invoice;
	}
	
	@Override
	public MInvoice getC_Invoice() throws RuntimeException {
		
		if (m_invoice != null && getC_Invoice_ID() != m_invoice.get_ID())
			m_invoice = null;
		
		if (m_invoice == null && getC_Invoice_ID() > 0)
			m_invoice = (MInvoice) super.getC_Invoice();
		
		return m_invoice;
	}
}	//	MLCOInvoiceWithholding
