/**
 * 
 */
package org.openmrs.module.mohbilling.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.User;


/**
 * @author emr
 * 
 */
public class Consommation {

	private Integer consommationId;

	private Beneficiary beneficiary;

	private Set<PatientServiceBill> billItems;

	private PatientBill patientBill;

	private InsuranceBill insuranceBill;

	private ThirdPartyBill thirdPartyBill;

	private GlobalBill globalBill;	

	private User creator;

	private Date createdDate;

	private boolean voided = false;

	private User voidedBy;

	private Date voidedDate;

	private String voidReason;

	/**
	 * @return the consommationId
	 */
	public Integer getConsommationId() {
		return consommationId;
	}

	/**
	 * @param consommationId
	 *            the consommationId to set
	 */
	public void setConsommationId(Integer consommationId) {
		this.consommationId = consommationId;
	}

	/**
	 * @return the billItems
	 */
	public Set<PatientServiceBill> getBillItems() {
		return billItems;
	}

	/**
	 * @param billItems
	 *            the billItems to set
	 */
	public void setBillItems(Set<PatientServiceBill> billItems) {
		this.billItems = billItems;
	}

	/**
	 * @return the globalBill
	 */
	public GlobalBill getGlobalBill() {
		return globalBill;
	}

	/**
	 * @param globalBill
	 *            the globalBill to set
	 */
	public void setGlobalBill(GlobalBill globalBill) {
		this.globalBill = globalBill;
	}

	/**
	 * @return the bill
	 */
	public PatientBill getPatientBill() {
		return patientBill;
	}

	/**
	 * @param bill
	 *            the bill to set
	 */
	public void setPatientBill(PatientBill patientBill) {
		this.patientBill = patientBill;
	}

	/**
	 * @return the insuranceBill
	 */
	public InsuranceBill getInsuranceBill() {
		return insuranceBill;
	}

	/**
	 * @param insuranceBill
	 *            the insuranceBill to set
	 */
	public void setInsuranceBill(InsuranceBill insuranceBill) {
		this.insuranceBill = insuranceBill;
	}

	/**
	 * @return the thirdPartyBill
	 */
	public ThirdPartyBill getThirdPartyBill() {
		return thirdPartyBill;
	}

	/**
	 * @param thirdPartyBill
	 *            the thirdPartyBill to set
	 */
	public void setThirdPartyBill(ThirdPartyBill thirdPartyBill) {
		this.thirdPartyBill = thirdPartyBill;
	}


	/**
	 * @return the beneficiary
	 */
	public Beneficiary getBeneficiary() {
		return beneficiary;
	}

	/**
	 * @param beneficiary
	 *            the beneficiary to set
	 */
	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}

	/**
	 * @return the creator
	 */
	public User getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            the creator to set
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the voided
	 */
	public boolean isVoided() {
		return voided;
	}

	/**
	 * @param voided
	 *            the voided to set
	 */
	public void setVoided(boolean voided) {
		this.voided = voided;
	}

	/**
	 * @return the voidedBy
	 */
	public User getVoidedBy() {
		return voidedBy;
	}

	/**
	 * @param voidedBy
	 *            the voidedBy to set
	 */
	public void setVoidedBy(User voidedBy) {
		this.voidedBy = voidedBy;
	}

	/**
	 * @return the voidedDate
	 */
	public Date getVoidedDate() {
		return voidedDate;
	}

	/**
	 * @param voidedDate
	 *            the voidedDate to set
	 */
	public void setVoidedDate(Date voidedDate) {
		this.voidedDate = voidedDate;
	}

	/**
	 * @return the voidReason
	 */
	public String getVoidReason() {
		return voidReason;
	}

	/**
	 * @param voidReason
	 *            the voidReason to set
	 */
	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}

	/**
	 * Add the given psb to the given list of bill items for this Consommation
	 */
	public void addBillItem(PatientServiceBill psb) {
		// TODO Auto-generated method stub

		if (billItems == null)
			billItems = new TreeSet<PatientServiceBill>();
		if (psb != null) {
			psb.setConsommation(this);
			billItems.add(psb);
		}

	}

}
