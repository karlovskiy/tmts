package info.karlovskiy.tmts.model.transfer;

import info.karlovskiy.tmts.model.Bank;
import info.karlovskiy.tmts.model.Customer;
import info.karlovskiy.tmts.route.transfer.ITransferVisitor;

import java.math.BigDecimal;
import java.util.Date;

public class DirectDebitTransfer implements ITransfer {

    private String senderReference;
    private Date requestedExecutionDate;

    private String transactionReference;
    private BigDecimal amount;
    private String currency;

    private Bank debtorBank;
    private Customer debtor;

    private Bank creditorBank;
    private Customer creditor;

    private String mandateReference;

    private String remittanceInformation;

    @Override
    public String description() {
        return "Direct debit";
    }

    @Override
    public void accept(ITransferVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public String getSenderReference() {
        return senderReference;
    }

    public void setSenderReference(String senderReference) {
        this.senderReference = senderReference;
    }

    public Date getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(Date requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Bank getDebtorBank() {
        return debtorBank;
    }

    public void setDebtorBank(Bank debtorBank) {
        this.debtorBank = debtorBank;
    }

    public Customer getDebtor() {
        return debtor;
    }

    public void setDebtor(Customer debtor) {
        this.debtor = debtor;
    }

    public Bank getCreditorBank() {
        return creditorBank;
    }

    public void setCreditorBank(Bank creditorBank) {
        this.creditorBank = creditorBank;
    }

    public Customer getCreditor() {
        return creditor;
    }

    public void setCreditor(Customer creditor) {
        this.creditor = creditor;
    }

    public String getMandateReference() {
        return mandateReference;
    }

    public void setMandateReference(String mandateReference) {
        this.mandateReference = mandateReference;
    }

    public String getRemittanceInformation() {
        return remittanceInformation;
    }

    public void setRemittanceInformation(String remittanceInformation) {
        this.remittanceInformation = remittanceInformation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DirectDebitTransfer{");
        sb.append("senderReference='").append(senderReference).append('\'');
        sb.append(", requestedExecutionDate=").append(requestedExecutionDate);
        sb.append(", transactionReference='").append(transactionReference).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", currency='").append(currency).append('\'');
        sb.append(", debtorBank=").append(debtorBank);
        sb.append(", debtor=").append(debtor);
        sb.append(", creditorBank=").append(creditorBank);
        sb.append(", creditor=").append(creditor);
        sb.append(", mandateReference='").append(mandateReference).append('\'');
        sb.append(", remittanceInformation='").append(remittanceInformation).append('\'');
        sb.append(", description='").append(description()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
