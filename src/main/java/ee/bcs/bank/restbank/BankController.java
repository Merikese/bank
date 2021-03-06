package ee.bcs.bank.restbank;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/solution")
public class BankController {

    public static Bank bank = new Bank();

    @Resource
    private AccountService accountService;

    @Resource
    private TransactionService transactionService;

    @Resource
    private BankService bankService;

    @GetMapping("/bank")
    public Bank getBank() {
        return bank;
    }

    @GetMapping("/example/account")
    public AccountDto getExampleAccount() {
        return accountService.createExampleAccount();
    }

    @GetMapping("/example/transaction")
    public TransactionDto getExampleTransaction() {
        return transactionService.createExampleTransaction();
    }

    @PostMapping("/new/account")
    public RequestResult addAccountToBank(@RequestBody AccountDto accountDto) {
        return bankService.addAccountToBank(bank, accountDto);

    }

    @PostMapping("/new/transaction")
    public RequestResult addNewTransaction(@RequestBody TransactionDto transactionDto) {
        return transactionService.addNewTransaction(bank, transactionDto);
    }

    @PostMapping("/receive/money")
    public RequestResult receiveNewTransaction(@RequestBody TransactionDto transactionDto) {
        return transactionService.receiveNewTransaction(bank, transactionDto);

    }

    @PutMapping("/update/owner")
    public RequestResult updateOwnerDetails(@RequestBody AccountDto accountDto) {
        return accountService.updateOwnerDetails(bank.getAccounts(), accountDto);
    }

    @DeleteMapping ("/delete/account")
    public RequestResult deleteAccount (@RequestParam int accountId) {
        return accountService.deleteAccount(bank.getAccounts(), accountId);

    }
    //tee endpoint, millega saab kontot lukustada/avada. kontrollige ka ID olemasolu

    @PutMapping("/lock/account")
    public RequestResult lockUnlockAccount(@RequestParam int accountId) {
        return accountService.lockUnlockAccount(bank.getAccounts(), accountId);

    }    //tee endpoint, millega saab kontot lukustada/avada. kontrollige ka ID olemasolu

//todo: Loo endpoint /bankstatement/by/lastname





    // Et lisada uus account, loo uus controlleri endpoint                    /new/account
    //  v??ta RequestBodyst sisse accountDto objekt
    //  loo bankService alla uus teenus                                             addAccountToBank()
    //  ja lisa see konto bank accounts listi
    //  teenus v??iks tagastada RequestResult objekti koos koos loodava konto id ja transaktsiooni id'ga


    //  loo transactionService alla uus teenus                                      createTransactionForNewAccount()
    //  loo bankService alla uus teenus                                             addTransaction()


}
