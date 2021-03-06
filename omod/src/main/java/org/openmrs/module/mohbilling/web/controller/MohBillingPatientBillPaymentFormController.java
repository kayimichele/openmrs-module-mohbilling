/**
 * 
 */
package org.openmrs.module.mohbilling.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohbilling.businesslogic.*;
import org.openmrs.module.mohbilling.model.*;
import org.openmrs.module.mohbilling.service.BillingService;
import org.openmrs.web.WebConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author rbcemr 
 */
public class MohBillingPatientBillPaymentFormController extends
		ParameterizableViewController {

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.setViewName(getViewName());
		BillPayment payment = null;
		
		if(request.getParameter("refundId")!=null){
			PaymentRefund refund = PaymentRefundUtil.getRefundById(Integer.valueOf(request.getParameter("refundId")));
			updatePaymentAfterRefund(request, refund);
		}
		
		if (request.getParameter("save") != null ){			
			payment = handleSavePatientBillPayment(request);
			mav.addObject("payment",payment);
		}

		 try{
			Consommation consommation = null;
			List<Consommation> consommations = null;

			consommation = Context.getService(BillingService.class).getConsommation(
					Integer.parseInt(request.getParameter("consommationId")));
			
			consommations = ConsommationUtil.getConsommationsByBeneficiary(consommation.getBeneficiary());

			mav.addObject("consommation", consommation);
			mav.addObject("consommations", consommations);
			mav.addObject("beneficiary", consommation.getBeneficiary());
			

			InsurancePolicy ip = consommation.getBeneficiary().getInsurancePolicy();
		    mav.addObject("insurancePolicy", ip);

			// check the validity of the insurancePolicy for today
			Date today = new Date();
			mav.addObject(
					"valid",
					((ip.getCoverageStartDate().getTime() <= today.getTime()) && (today
							.getTime() <= ip.getExpirationDate().getTime())));
			mav.addObject("todayDate", today);
			mav.addObject("authUser", Context.getAuthenticatedUser());
			mav.addObject("patientAccount", PatientAccountUtil.getPatientAccountByPatient(consommation.getBeneficiary().getPatient()));

		} catch (Exception e) {
			log.error(">>>>MOH>>BILLING>> " + e.getMessage());
			e.printStackTrace();
			//return new ModelAndView(new RedirectView("patientSearchBill.form"));
		}
	
		
		return mav;
	}
	/**
	 * @param request
	 * @return
	 */
	private BillPayment handleSavePatientBillPayment(HttpServletRequest request) {
	
		BillPayment billPayment = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {			
			Consommation consommation = ConsommationUtil.getConsommation(Integer.parseInt(request.getParameter("consommationId")));
			PatientBill pb =consommation.getPatientBill();
				BillPayment bp = new BillPayment();
				/**
				 * We need to add both Patient Due amount and amount paid by
				 * third part
				 */
				
				bp.setCollector(Context.getAuthenticatedUser());
				bp.setDateReceived(Context.getDateFormat().parse(
						request.getParameter("dateBillReceived")));				
				bp.setPatientBill(pb);
				bp.setCreatedDate(new Date());
				bp.setCreator(Context.getAuthenticatedUser());
				//bp = PatientBillUtil.createBillPayment(bp);
				
				//mark as paid all  selected items for payment purpose
				
				if(request.getParameter("cashPayment")!=null){
					bp.setAmountPaid(BigDecimal.valueOf(Double.parseDouble(request
							.getParameter("receivedCash"))));
					//create cashPayment
					CashPayment cp =new CashPayment(bp);
					setParams(cp, Context.getAuthenticatedUser(), false, new Date());
					
					cp = PatientBillUtil.createCashPayment(cp);
					billPayment =cp;
					createPaidServiceBill(request, consommation, cp);
					request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR,
							"The Bill Payment with cash has been saved successfully !");
				}
				
				if(request.getParameter("depositPayment")!=null){
					BigDecimal deductedAmount = BigDecimal.valueOf(Double.parseDouble(request.getParameter("deductedAmount")));
					PatientAccount patientAccount = PatientAccountUtil.getPatientAccountByPatient(consommation.getBeneficiary().getPatient());
					//create deposit payment
					if(deductedAmount.compareTo(patientAccount.getBalance())==-1){
					//update the patient account balance
					patientAccount.setBalance(patientAccount.getBalance().subtract(deductedAmount));
					
					//create transaction
					Transaction transaction = PatientAccountUtil.createTransaction(deductedAmount.negate(), new Date(), new Date(), Context.getAuthenticatedUser(), patientAccount, "Bill Payment", Context.getAuthenticatedUser());
					
					bp.setAmountPaid(deductedAmount);
					DepositPayment dp = new DepositPayment(bp);

					setParams(dp, Context.getAuthenticatedUser(), false, new Date());
					dp.setTransaction(transaction);
					
					dp = PatientBillUtil.createDepositPayment(dp);
					createPaidServiceBill(request, consommation, dp);
					
					billPayment = dp;
					
					request.getSession().setAttribute(
							WebConstants.OPENMRS_MSG_ATTR,
							"The Bill Payment with deposit has been saved successfully !");
					}
					else
						request.getSession().setAttribute(
								WebConstants.OPENMRS_ERROR_ATTR,
								"The Bill Payment is not saved. No sufficient balance !");
				} 

				return billPayment;
		} catch (Exception e) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					"The Bill Payment has not been saved !");
			log.error("" + e.getMessage());
			e.printStackTrace();

			return null;
		}
	}
	
	public BillPayment updatePaymentAfterRefund(HttpServletRequest request,PaymentRefund refund){
		Set<PaidServiceBillRefund> refundedItems = refund.getRefundedItems();
		BillPayment paymentCopy = new BillPayment();
		BigDecimal remainingQty = null;
		for (PaidServiceBillRefund refundedItem : refundedItems) {
			remainingQty = refundedItem.getPaidItem().getPaidQty().subtract(refundedItem.getRefQuantity());
			
			//if all qty have been refunded, no reason to copy the paidItem
			if(remainingQty.compareTo(BigDecimal.ZERO)>0){
			PaidServiceBill refundedItemCopy = new PaidServiceBill();
			refundedItemCopy.setPaidQty(remainingQty);
			refundedItemCopy.setBillPayment(refund.getBillPayment());
			refundedItemCopy.setCreator(refundedItem.getCreator());
			refundedItemCopy.setCreatedDate(refundedItem.getCreatedDate());			
			refundedItemCopy.setVoided(refundedItem.getVoided());
			refundedItemCopy.setBillItem(refundedItem.getPaidItem().getBillItem());
			BillPaymentUtil.createPaidServiceBill(refundedItemCopy);
			}
			
			//void previous paidItem
			PaidServiceBill paidItem = refundedItem.getPaidItem();
			paidItem.setVoided(true);
			paidItem.setVoidedBy(Context.getAuthenticatedUser());
			paidItem.setVoidedDate(new Date());
			paidItem.setVoidReason("Refunded Reason: "+refundedItem.getRefundReason());
			BillPaymentUtil.createPaidServiceBill(paidItem);
		}
		return paymentCopy;
		
	}
	public void createPaidServiceBill(HttpServletRequest request,Consommation consommation, BillPayment bp){
  
		Map<String, String[]> parameterMap = request.getParameterMap();			
		
		for (String  parameterName : parameterMap.keySet()) {
			
			if (!parameterName.startsWith("item-")) {
				continue;
			}
			PaidServiceBill paidSb = new PaidServiceBill();
			String[] splittedParameterName = parameterName.split("-");
			String psbIdStr = splittedParameterName[2];			
			//String psbIdStr = request.getParameter(parameterName);
			Integer  patientServiceBillId = Integer.parseInt(psbIdStr);	
			PatientServiceBill psb  = ConsommationUtil.getPatientServiceBill(patientServiceBillId);
			
			
			paidSb.setBillItem(psb);
		
			BigDecimal paidQuantity = new BigDecimal(Double.valueOf(request.getParameter("paidQty_"+psb.getPatientServiceBillId())));
			paidSb.setPaidQty(paidQuantity);
			paidSb.setBillPayment(bp);
			paidSb.setCreator(Context.getAuthenticatedUser());
			paidSb.setCreatedDate(new Date());			
			paidSb.setVoided(false);
			BillPaymentUtil.createPaidServiceBill(paidSb);
			
			BigDecimal totalQtyPaid=paidQuantity;
			if(psb.getPaidQuantity()!=null)
				totalQtyPaid = psb.getPaidQuantity().add(paidQuantity);
			
			//if paid,then update patientservicebill as paid
			psb.setPaid(true);
			psb.setPaidQuantity(totalQtyPaid);
			ConsommationUtil.createPatientServiceBill(psb);
		}
	
		
	}
	public void setParams(BillPayment payment,User creator,Boolean voided,Date createdDate){
		payment.setCreator(Context.getAuthenticatedUser());
		payment.setVoided(false);
		payment.setCreatedDate(new Date());
	}

}
