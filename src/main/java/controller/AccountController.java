package controller;

import DTO.TransferDTO;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import model.Account;
import model.Extract;
import model.enumeration.EnumOperationType;
import utils.Constants;

import java.io.IOException;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class AccountController {

    public void save(Account account) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<Account> accounts = findAll();
            if (accounts.isEmpty()) {
                account.setId(1L);
            } else {
                account.setId(accounts.get(accounts.size() - 1).getId() + 1);
            }
            accounts.add(account);
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(Paths.get(Constants.accountPathFile).toFile(), accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Account account) {
        ArrayList<Account> accounts = findAll();
        OptionalInt indexOpt = IntStream.range(0, accounts.size())
                .filter(i -> account.getId().equals(accounts.get(i).getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            accounts.remove(indexOpt.getAsInt());
            accounts.add(indexOpt.getAsInt(), account);
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
                writer.writeValue(Paths.get(Constants.accountPathFile).toFile(), accounts);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Account> findAll() {
        ObjectMapper mapper = new ObjectMapper();
        if (Files.exists(Paths.get(Constants.accountPathFile))) {
            try {
                return new ArrayList<>(Arrays.asList(mapper.readValue(Paths.get(Constants.accountPathFile)
                        .toFile(), Account[].class)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public Account findById(Long id) {
        return findAll().stream()
                .filter(account -> account.getId().equals(id)).findFirst().orElse(null);
    }

    public BigDecimal getBalance(Long id) {
        Account account = (Account) findAll().stream().filter(ac -> ac.getId().equals(id));
        return account.getBalance();
    }

    public BigDecimal getAmount() {
        BigDecimal sum = new BigDecimal("0");
        ArrayList<Account> accounts = findAll();
        for (Account account : accounts) {
            sum = sum.add(account.getBalance());
        }
        return sum;
    }

    public Account getAccountByCpf(String cpf) {
        return findAll().stream()
                .filter(ac -> ac.getOwner().getCpf().equals(cpf)).findFirst().orElse(null);
    }

    private void transfer(Account source, Account target, BigDecimal value) {
        AccountController accountController = new AccountController();

        Extract extract = new Extract();
        extract.setValue(value);
        extract.setDate(new Date());
        extract.setTime(Time.valueOf(LocalTime.now()));
        extract.setOperation(EnumOperationType.TRANSFER);
        source.getExtracts().add(extract);
        accountController.update(source);

        Extract extractTarget = new Extract();
        extractTarget.setValue(value);
        extractTarget.setDate(new Date());
        extractTarget.setTime(Time.valueOf(LocalTime.now()));
        extractTarget.setOperation(EnumOperationType.RECEIVE);
        target.getExtracts().add(extractTarget);
        accountController.update(target);
    }

    public TransferDTO transferValue(Account source, String cpf, BigDecimal value) {
        AccountController accountController = new AccountController();
        Account target = getAccountByCpf(cpf);
        List<Account> accounts = accountController.findAll();
        TransferDTO transferDTO = null;
        if (accounts.stream().anyMatch(account -> account.getId().equals(source.getId())) && Objects.nonNull(target)) {
            if (source.getBalance().compareTo(value) >= 0) {
                BigDecimal newValue = source.getBalance().subtract(value);
                source.setBalance(newValue);
                newValue = target.getBalance().add(value);
                target.setBalance(newValue);

                transfer(source, target, value);

                transferDTO = new TransferDTO();
                transferDTO.setTarget(target);
                transferDTO.setSource(source);
                transferDTO.setValue(value);
            }
        }
        return transferDTO;
    }
}
