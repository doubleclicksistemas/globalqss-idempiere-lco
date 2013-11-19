/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.globalqss.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for LCO_DIAN_ConceptSource
 *  @author iDempiere (generated) 
 *  @version Release 2.0
 */
@SuppressWarnings("all")
public interface I_LCO_DIAN_ConceptSource 
{

    /** TableName=LCO_DIAN_ConceptSource */
    public static final String Table_Name = "LCO_DIAN_ConceptSource";

    /** AD_Table_ID=1000017 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 2 - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(2);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name CalcColumnPosition */
    public static final String COLUMNNAME_CalcColumnPosition = "CalcColumnPosition";

	/** Set CalcColumnPosition	  */
	public void setCalcColumnPosition (int CalcColumnPosition);

	/** Get CalcColumnPosition	  */
	public int getCalcColumnPosition();

    /** Column name C_ElementValue_ID */
    public static final String COLUMNNAME_C_ElementValue_ID = "C_ElementValue_ID";

	/** Set Account Element.
	  * Account Element
	  */
	public void setC_ElementValue_ID (int C_ElementValue_ID);

	/** Get Account Element.
	  * Account Element
	  */
	public int getC_ElementValue_ID();

	public org.compiere.model.I_C_ElementValue getC_ElementValue() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name FieldExpression */
    public static final String COLUMNNAME_FieldExpression = "FieldExpression";

	/** Set Field Expression	  */
	public void setFieldExpression (String FieldExpression);

	/** Get Field Expression	  */
	public String getFieldExpression();

    /** Column name GL_Category_ID */
    public static final String COLUMNNAME_GL_Category_ID = "GL_Category_ID";

	/** Set GL Category.
	  * General Ledger Category
	  */
	public void setGL_Category_ID (int GL_Category_ID);

	/** Get GL Category.
	  * General Ledger Category
	  */
	public int getGL_Category_ID();

	public org.compiere.model.I_GL_Category getGL_Category() throws RuntimeException;

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name LCO_DIAN_Concept_ID */
    public static final String COLUMNNAME_LCO_DIAN_Concept_ID = "LCO_DIAN_Concept_ID";

	/** Set DIAN Concept	  */
	public void setLCO_DIAN_Concept_ID (int LCO_DIAN_Concept_ID);

	/** Get DIAN Concept	  */
	public int getLCO_DIAN_Concept_ID();

	public org.globalqss.model.I_LCO_DIAN_Concept getLCO_DIAN_Concept() throws RuntimeException;

    /** Column name LCO_DIAN_ConceptSource_ID */
    public static final String COLUMNNAME_LCO_DIAN_ConceptSource_ID = "LCO_DIAN_ConceptSource_ID";

	/** Set DIAN Concept Source	  */
	public void setLCO_DIAN_ConceptSource_ID (int LCO_DIAN_ConceptSource_ID);

	/** Get DIAN Concept Source	  */
	public int getLCO_DIAN_ConceptSource_ID();

    /** Column name LCO_DIAN_ConceptSource_UU */
    public static final String COLUMNNAME_LCO_DIAN_ConceptSource_UU = "LCO_DIAN_ConceptSource_UU";

	/** Set LCO_DIAN_ConceptSource_UU	  */
	public void setLCO_DIAN_ConceptSource_UU (String LCO_DIAN_ConceptSource_UU);

	/** Get LCO_DIAN_ConceptSource_UU	  */
	public String getLCO_DIAN_ConceptSource_UU();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
