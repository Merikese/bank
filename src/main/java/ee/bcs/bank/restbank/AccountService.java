package ee.bcs.bank.restbank;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    public AccountDto createExampleAccount() {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountNumber(createRandomAccountNumber());
        accountDto.setFirstName("Juss");
        accountDto.setLastName("Sepp");
        accountDto.setBalance(0);
        accountDto.setLocked(false);
        return accountDto;

    }


    private String createRandomAccountNumber() {
        //  Creates random account number between EE1000 -  EE9999
        Random random = new Random();
        return "EE" + (random.nextInt(9999) + 1000);
    }

    public boolean accountIdExists(List<AccountDto> accounts, int accountId) {
        for (AccountDto account : accounts) {
            if (account.getId() == accountId) {
                return true;
            }
        }
        return false;
    }

    public AccountDto getAccountById(List<AccountDto> accounts, int accountId) {
        //käime läbi kõik kontod accounts listis, ja paneme iga konto muutujasse 'account'
        for (AccountDto account : accounts) {
            //kui leiame konto, mille id on võrdne accountId-ga
            if (account.getId() == accountId) {
                //siis tagastame selle konto
                return account;
            }
        }
        return null;
    }

    public boolean accountNumberExists(List<AccountDto> accounts, String receiverAccountNumber) {
        for (AccountDto account : accounts) {
            if (account.getAccountNumber().equals(receiverAccountNumber)) {
                return true;
            }
        }

        return false;
    }

    public AccountDto getAccountByNumber(List<AccountDto> accounts, String receiverAccountNumber) {
        for (AccountDto account : accounts) {
            if (account.getAccountNumber().equals(receiverAccountNumber)) {
                return account;
            }
        }

        return null;
    }

    public RequestResult updateOwnerDetails(List<AccountDto> accounts, AccountDto accountDto) {
        RequestResult requestResult = new RequestResult();

        int accountId = accountDto.getId();
        if (!accountIdExists(accounts, accountId)) {
            requestResult.setError(" Account Id: " + accountId + "does not exist");
            requestResult.setAccountId(accountId);

            return requestResult;
        }
        AccountDto account = getAccountById(accounts, accountId);
        account.setFirstName(accountDto.getFirstName());
        account.setLastName(accountDto.getLastName());

        requestResult.setAccountId(accountId);
        requestResult.setMessage("Successfully updated account");

        return requestResult;

    }

    public RequestResult deleteAccount(List<AccountDto> accounts, int accountId) {
        RequestResult requestResult = new RequestResult();

        if (!accountIdExists(accounts, accountId)) {
            requestResult.setError(" Account Id: " + accountId + "does not exist");

            return requestResult;
        }

        AccountDto account = getAccountById(accounts, accountId);
        accounts.remove(account);
        requestResult.setMessage("Account deleted");
        requestResult.setAccountId(accountId);
        return requestResult;

    }

    public RequestResult lockUnlockAccount(List<AccountDto> accounts, int accountId) {
        RequestResult requestResult = new RequestResult();
        if (!accountIdExists(accounts, accountId)) {
            requestResult.setError("Account ID: " + accountId + " does not exist");
            requestResult.setAccountId(accountId);
            return requestResult;

        }
        AccountDto account = getAccountById(accounts, accountId);
        if (account.getLocked()) { //  kui on lukus,
            account.setLocked(false); //siis tee lahti
            requestResult.setAccountId(accountId);//account Id added to msg
            requestResult.setMessage("Account unlocked: " + accountId); // add to msg account unlocked

            return requestResult;
        }
        account.setLocked(true);// when previous if did not get through - kui eelmine if ei läinud läbi- hüppas üle siia
        requestResult.setAccountId (accountId);
        requestResult.setMessage("Account is locked: " + accountId);

        return requestResult;

    }

}
