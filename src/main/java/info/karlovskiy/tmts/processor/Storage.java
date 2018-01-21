package info.karlovskiy.tmts.processor;

import info.karlovskiy.tmts.model.Bank;
import info.karlovskiy.tmts.model.Customer;
import info.karlovskiy.tmts.route.TmtsBusinessException;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static info.karlovskiy.tmts.route.TmtsBusinessError.TRANSFER_PROCESS_ERROR;

public class Storage {

    public static final CustomerAccount NEO = new CustomerAccount("BH81CITI00000123456789", "Neo", "CITIBHBX");
    public static final CustomerAccount MATRIX = new CustomerAccount("BH86BBME00000987654321", "Matrix", "BBMEBHBX");
    public static final CustomerAccount MR_ANDERSON = new CustomerAccount("BH50CHAS00000077777777", "Mr Anderson", "CHASBHBM");

    private static final Map<CustomerAccount, BigDecimal> BALANCES = new HashMap<>();
    private static final ConcurrentHashMap<CustomerAccount, Lock> LOCKS = new ConcurrentHashMap<>();

    static {
        BALANCES.put(NEO, new BigDecimal("10.34"));
        BALANCES.put(MATRIX, new BigDecimal("1000.43"));
        BALANCES.put(MR_ANDERSON, new BigDecimal("300.00"));
    }

    public static void moveMoney(CustomerAccount from, CustomerAccount to, BigDecimal amount) {
        Lock fromLock = LOCKS.get(from);
        if (fromLock == null) {
            Lock newLock = new ReentrantLock();
            fromLock = LOCKS.putIfAbsent(from, newLock);
            if (fromLock == null) {
                fromLock = newLock;
            }
        }

        Lock toLock = LOCKS.get(to);
        if (toLock == null) {
            Lock newLock = new ReentrantLock();
            toLock = LOCKS.putIfAbsent(to, newLock);
            if (toLock == null) {
                toLock = newLock;
            }
        }

        Lock first;
        Lock second;
        if (from.compareTo(to) > 1) {
            first = fromLock;
            second = toLock;
        } else {
            first = toLock;
            second = fromLock;
        }

        try {
            first.lock();
            try {
                second.lock();
                BigDecimal balanceFrom = BALANCES.get(from);
                BigDecimal newBalanceFrom = balanceFrom.subtract(amount);
                if (newBalanceFrom.compareTo(BigDecimal.ZERO) < 0) {
                    throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR,
                            "No sufficient fund on account " + from.account + ", " + from.bic + ", " + from.name);
                }
                BigDecimal balanceTo = BALANCES.get(to);
                BigDecimal newBalanceTo = balanceTo.add(amount);

                BALANCES.put(from, newBalanceFrom);
                BALANCES.put(to, newBalanceTo);
            } finally {
                second.unlock();
            }
        } finally {
            first.unlock();
        }

        LOCKS.putIfAbsent(from, new ReentrantLock());
    }

    public static BigDecimal balance(CustomerAccount account) {
        return BALANCES.get(account);
    }

    public static class CustomerAccount implements Comparable<CustomerAccount> {

        private final String account;
        private final String name;
        private final String bic;

        public CustomerAccount(Customer customer, Bank bank) {
            this(
                    customer != null ? customer.getAccount() : null,
                    customer != null ? customer.getName() : null,
                    bank != null ? bank.getBic() : null
            );
        }

        public CustomerAccount(String account, String name, String bic) {
            this.account = account;
            this.name = name;
            this.bic = bic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CustomerAccount)) return false;
            CustomerAccount that = (CustomerAccount) o;
            return Objects.equals(account, that.account) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(bic, that.bic);
        }

        @Override
        public int hashCode() {
            return Objects.hash(account, name, bic);
        }

        @Override
        public int compareTo(CustomerAccount o) {
            if (o == null) {
                return 1;
            }
            int result = ObjectUtils.compare(this.account, o.account);
            if (result == 0) {
                result = ObjectUtils.compare(this.bic, o.bic);
                if (result == 0) {
                    result = ObjectUtils.compare(this.name, o.name);
                }
            }
            return result;
        }
    }

}
