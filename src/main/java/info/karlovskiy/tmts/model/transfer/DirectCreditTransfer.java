package info.karlovskiy.tmts.model.transfer;

import info.karlovskiy.tmts.model.Bank;
import info.karlovskiy.tmts.model.Customer;
import info.karlovskiy.tmts.route.transfer.ITransferVisitor;

import java.math.BigDecimal;
import java.util.Date;

public class DirectCreditTransfer implements ITransfer {

    private String senderReference;
    private Date valueDate;
    private BigDecimal amount;
    private String currency;

    private Customer orderingCustomer;
    private Bank orderingInstitution;

    private Customer beneficiaryCustomer;
    private Bank accountWithInstitution;

    private String remittanceInformation;

    @Override
    public String description() {
        return "Direct credit";
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

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
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

    public Customer getOrderingCustomer() {
        return orderingCustomer;
    }

    public void setOrderingCustomer(Customer orderingCustomer) {
        this.orderingCustomer = orderingCustomer;
    }

    public Bank getOrderingInstitution() {
        return orderingInstitution;
    }

    public void setOrderingInstitution(Bank orderingInstitution) {
        this.orderingInstitution = orderingInstitution;
    }

    public Customer getBeneficiaryCustomer() {
        return beneficiaryCustomer;
    }

    public void setBeneficiaryCustomer(Customer beneficiaryCustomer) {
        this.beneficiaryCustomer = beneficiaryCustomer;
    }

    public Bank getAccountWithInstitution() {
        return accountWithInstitution;
    }

    public void setAccountWithInstitution(Bank accountWithInstitution) {
        this.accountWithInstitution = accountWithInstitution;
    }

    public String getRemittanceInformation() {
        return remittanceInformation;
    }

    public void setRemittanceInformation(String remittanceInformation) {
        this.remittanceInformation = remittanceInformation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DirectCreditTransfer{");
        sb.append("senderReference='").append(senderReference).append('\'');
        sb.append(", valueDate=").append(valueDate);
        sb.append(", amount=").append(amount);
        sb.append(", currency='").append(currency).append('\'');
        sb.append(", orderingCustomer=").append(orderingCustomer);
        sb.append(", orderingInstitution=").append(orderingInstitution);
        sb.append(", beneficiaryCustomer=").append(beneficiaryCustomer);
        sb.append(", accountWithInstitution=").append(accountWithInstitution);
        sb.append(", remittanceInformation='").append(remittanceInformation).append('\'');
        sb.append(", description='").append(description()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
