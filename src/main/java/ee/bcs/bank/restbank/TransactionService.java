package ee.bcs.bank.restbank;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    public static final String ATM = "ATM";
    public static final char NEW_ACCOUNT = 'n';
    public static final char DEPOSIT = 'd';
    public static final char WITHDRAWAL = 'w';
    public static final char SEND_MONEY = 's';
    public static final char RECEIVE_MONEY = 'r';

    @Resource
    private AccountService accountService;

    @Resource
    private BalanceService balanceService;


    public TransactionDto createExampleTransaction() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAccountId(123);
        transactionDto.setBalance(1000);
        transactionDto.setAmount(100);
        transactionDto.setTransactionType(SEND_MONEY);
        transactionDto.setReceiverAccountNumber("EE123");
        transactionDto.setSenderAccountNumber("EE456");
        transactionDto.setLocalDateTime(LocalDateTime.now());
        return transactionDto;

    }

    public RequestResult addNewTransaction(Bank bank, TransactionDto transactionDto) {
        //loon vajalikud objektid (tühjad)

        RequestResult requestResult = new RequestResult();
        List<AccountDto> accounts = bank.getAccounts();
        int accountId = transactionDto.getAccountId();

        //kontrolli kas konto eksisteerib
        if (!accountService.accountIdExists(accounts, accountId)) {
            requestResult.setAccountId(accountId);
            requestResult.setError("Account ID" + accountId + " does not exist!!!");
            return requestResult;
        }
        //vajalike andmete lisamine muutujatesse
        Character transactionType = transactionDto.getTransactionType();
        int transactionId = bank.getAccountIdCount();


        //pärime välja accountId abiga õige konto ja balanc'i
        AccountDto account = accountService.getAccountById(accounts, accountId);
        Integer balance = account.getBalance();
        int newBalance;

        String receiverAccountNumber;

        //töötleme läbi erinevad olukorrad
        Integer amount = transactionDto.getAmount();

        switch (transactionType) {
            case NEW_ACCOUNT:
                // kontrolli kas accounId eksisteerib ja kui mitte, tagasta error sõnum

                //täidame transactionDto
                transactionDto.setSenderAccountNumber(null);
                transactionDto.setReceiverAccountNumber(null);
                transactionDto.setBalance(0);
                transactionDto.setAmount(0);
                transactionDto.setLocalDateTime(LocalDateTime.now());

                transactionDto.setId(transactionId);

                //Lisame tehingu transactionite alla (pluss inkrementeerime)
                bank.addTransactionToTransactions(transactionDto);
                bank.incrementTransactionId();

                // meisterdame result objekti
                requestResult.setTransactionId(transactionId);
                requestResult.setAccountId(accountId);
                requestResult.setMessage("Successfully added 'new account' transaction ");
                return requestResult;

            case DEPOSIT:
                //arvutame uue balanci
                newBalance = balance + amount;

                // täidame transactionDto
                transactionDto.setSenderAccountNumber(ATM);
                transactionDto.setReceiverAccountNumber(account.getAccountNumber());
                transactionDto.setBalance(newBalance);
                transactionDto.setLocalDateTime(LocalDateTime.now());
                transactionDto.setId(transactionId);

                //Lisame tehingu transactionite alla (pluss inkrementeerime)
                bank.addTransactionToTransactions(transactionDto);
                bank.incrementTransactionId();

                //uuendame konto balancit
                account.setBalance(newBalance);

                // valmistame result objekti
                requestResult.setTransactionId(transactionId);
                requestResult.setAccountId(accountId);
                requestResult.setMessage("Successfully added 'new account' transaction ");
                return requestResult;


            case SEND_MONEY:
                //kontrollime kas saatjal on piisaval raha
                if (!balanceService.enoughMoneyOnAccount(balance, amount)) {
                    requestResult.setAccountId(accountId);
                    requestResult.setError("Not enough money" + amount);
                    return requestResult;
                }
                //arvutame välja uue balanc'i (võtame saatjalt raha maha)
                newBalance = balance - amount;

                // täidame transactionDto
                transactionDto.setSenderAccountNumber(account.getAccountNumber());
                transactionDto.setBalance(newBalance);
                transactionDto.setLocalDateTime(LocalDateTime.now());
                transactionDto.setId(transactionId);

                //Lisame tehingu transactionite alla (pluss inkrementeerime)
                bank.addTransactionToTransactions(transactionDto);
                bank.incrementTransactionId();

                //uuendame konto balancit
                account.setBalance(newBalance);

                // valmistame result objekti
                requestResult.setTransactionId(transactionId);
                requestResult.setAccountId(accountId);
                requestResult.setMessage("Successfully sent money ");

                //teeme SAAJA transaktsiooni
                receiverAccountNumber = transactionDto.getReceiverAccountNumber();

                //kontrollime kas saaja kontonumber eksisteerib meie andmebaasis(bank)
                if (accountService.accountNumberExists(accounts, receiverAccountNumber)) {
                    AccountDto receiverAccount = accountService.getAccountByNumber(accounts, receiverAccountNumber);
                    int receiverNewBalance = receiverAccount.getBalance() + amount;

                    //Loome uue transaktsiooni ojbekti ja hakkame seda täitma
                    TransactionDto receiverTransactionDto = new TransactionDto();

                    // täidame transactionDto
                    receiverTransactionDto.setSenderAccountNumber(account.getAccountNumber());
                    receiverTransactionDto.setReceiverAccountNumber(receiverAccountNumber);
                    receiverTransactionDto.setBalance(receiverNewBalance);
                    receiverTransactionDto.setLocalDateTime(LocalDateTime.now());
                    receiverTransactionDto.setId(bank.getTransactionIdCount());
                    receiverTransactionDto.setAmount((amount));
                    receiverTransactionDto.setTransactionType(RECEIVE_MONEY);

                }
                return requestResult;

            default:
                requestResult.setError("unknown transaction" + transactionType);
                return requestResult;

        }

    }

    public RequestResult receiveNewTransaction(Bank bank, TransactionDto transactionDto) {
        RequestResult requestResult = new RequestResult();
        String receiverAccountNumber = transactionDto.getReceiverAccountNumber();
        List<AccountDto> accounts = bank.getAccounts();

        if(!accountService.accountNumberExists(accounts, receiverAccountNumber)) {
            requestResult.setError("No such account (" + receiverAccountNumber + ") in our bank, try SEB");
            return requestResult;
        }

        AccountDto receiverAccount = accountService.getAccountByNumber(bank.getAccounts(), receiverAccountNumber);
        int transactionId = bank.getTransactionIdCount();

        int receiverNewBalance = receiverAccount.getBalance() + transactionDto.getAmount();

        transactionDto.setTransactionType(RECEIVE_MONEY);
        transactionDto.setBalance(receiverNewBalance);
        transactionDto.setId(transactionId);
        transactionDto.setId(receiverAccount.getId());
        transactionDto.setLocalDateTime(LocalDateTime.now());

        bank.addTransactionToTransactions(transactionDto);
        bank.incrementTransactionId();

        receiverAccount.setBalance(receiverNewBalance);
        requestResult.setTransactionId(transactionId);
        requestResult.setMessage("Transaction received");


        return requestResult;


        }
        // TODO:    createTransactionForNewAccount()
        //  account number
        //  balance 0
        //  amount 0
        //  transactionType 'n'
        //  receiver jääb null
        //  sender jääb null


    }
