/**
 * 
 */
package org.openmrs.module.mohbilling.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.User;
import org.openmrs.util.OpenmrsUtil;

/**
 * A Refund is done when the the Hospital wants to reimburse amount to patient
 * due to either an error or a missing service.The BillPayment is a precondition
 * of refund.The Refund is associate to refunded billable services varying from
 * 1 to n and the refund is done based bill payment associated to paid service bills
 * 
 * @author emr
 * 
 */
public class PaymentRefund {

	private Integer refundId;

	private BillPayment billPayment;

	private BigDecimal refundedAmount;

	private Set<PaidServiceBillRefund> refundedItems;

	private User refundedBy;
	

	private Boolean approved = false;

	private User approvedBy;

	private Date approvalDate;

	private Boolean declined = false;

	private String decliningNote;

	private User creator;

	private Date createdDate;

	private Boolean voided = false;

	private User voidedBy;

	private Date voidedDate;

	private String voidReason;

	/**
	 * @return the refundId
	 */
	public Integer getRefundId() {
		return refundId;
	}

	/**
	 * @param refundId
	 *            the refundId to set
	 */
	public void setRefundId(Integer refundId) {
		this.refundId = refundId;
	}

	/**
	 * @return the billPayment
	 */
	public BillPayment getBillPayment() {
		return billPayment;
	}

	/**
	 * @param billPayment
	 *            the billPayment to set
	 */
	public void setBillPayment(BillPayment billPayment) {
		this.billPayment = billPayment;
	}

	/**
	 * @return the refundedAmount
	 */
	public BigDecimal getRefundedAmount() {
		return refundedAmount;
	}

	/**
	 * @param refundedAmount
	 *            the refundedAmount to set
	 */
	public void setRefundedAmount(BigDecimal refundedAmount) {
		this.refundedAmount = refundedAmount;
	}

	/**
	 * @return the refundedBy
	 */
	public User getRefundedBy() {
		return refundedBy;
	}

	/**
	 * @param refundedBy
	 *            the refundedBy to set
	 */
	public void setRefundedBy(User refundedBy) {
		this.refundedBy = refundedBy;
	}

	public Boolean isApproved() {
		return approved;
	}

	/**
	 * @return the approved
	 */
	public Boolean getApproved() {
		return isApproved();
	}

	/**
	 * @param approved
	 *            the approved to set
	 */
	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	/**
	 * @return the approvedBy
	 */
	public User getApprovedBy() {
		return approvedBy;
	}

	/**
	 * @param approvedBy
	 *            the approvedBy to set
	 */
	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	/**
	 * @return the approvalDate
	 */
	public Date getApprovalDate() {
		return approvalDate;
	}

	/**
	 * @return the decliningNote
	 */
	public String getDecliningNote() {
		return decliningNote;
	}

	/**
	 * @param decliningNote the decliningNote to set
	 */
	public void setDecliningNote(String decliningNote) {
		this.decliningNote = decliningNote;
	}

	/**
	 * @param approvalDate
	 *            the approvalDate to set
	 */
	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * @return the declined
	 */
	public Boolean getDeclined() {
		return declined;
	}

	/**
	 * @param declined
	 *            the declined to set
	 */
	public void setDeclined(Boolean declined) {
		this.declined = declined;
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
	public Boolean getVoided() {
		return voided;
	}

	/**
	 * @param voided
	 *            the voided to set
	 */
	public void setVoided(Boolean voided) {
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
	 * @return the refundedItems
	 */
	public Set<PaidServiceBillRefund> getRefundedItems() {

		return refundedItems;
	}

	/**
	 * @param refundedItems
	 *            the refundedItems to set
	 */
	public void setRefundedItems(Set<PaidServiceBillRefund> refundedItems) {
		this.refundedItems = refundedItems;
	}
	/**
	 * Add the given refunded item to the refunded items list of this refund
	  * @param refundedItem
	 */
	public void addRefundedItem(PaidServiceBillRefund refundedItem) {

		if (refundedItems == null)
			refundedItems = new TreeSet<PaidServiceBillRefund>();
		if (refundedItem != null) {
			refundedItem.setRefund(this);
			refundedItems.add(refundedItem);
		}
	}
}

