package config;

import DTO.MessageDTO;
import DTO.TransferDTO;
import controller.AccountController;
import model.Account;
import model.Extract;
import model.enumeration.EnumBankMessages;
import model.enumeration.EnumOperationType;
import org.jgroups.Address;
import org.jgroups.JChannel;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class AccountMethods {
    AccountController accountController;
    JChannel channel;
    public AccountMethods(JChannel channel) {
        this.channel = channel;
        this.accountController = new AccountController();
    }

    public void transferValue(MessageDTO message, Address userAddress) {
        try {
            MessageDTO response = new MessageDTO();
            List<Object> transferInformations = (List<Object>) message.getObject();
            String cpfSource = (String) transferInformations.get(0);
            Account source = accountController.getAccountByCpf(cpfSource);
            String cpfTarget = (String) transferInformations.get(1);
            BigDecimal value = (BigDecimal) transferInformations.get(2);
            TransferDTO transferDTO = accountController.transferValue(source, cpfTarget, value);
            updateExtract(transferDTO);
            response.setObject("Transferência realizada com sucesso");
            response.setBankMessage(EnumBankMessages.TRANSFER_SUCESS);
            channel.send(userAddress, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateExtract(TransferDTO transferDTO) {
        Extract extractSource = new Extract();
        extractSource.setOperation(EnumOperationType.TRANSFER);
        extractSource.setTime(Time.valueOf(LocalTime.now()));
        extractSource.setDate(Date.from(Instant.now()));
        extractSource.setValue(transferDTO.getValue());
        transferDTO.getSource().getExtracts().add(extractSource);
        accountController.update(transferDTO.getSource());

        Extract extractTarget = new Extract();
        extractTarget.setOperation(EnumOperationType.RECEIVE);
        extractTarget.setTime(Time.valueOf(LocalTime.now()));
        extractTarget.setDate(Date.from(Instant.now()));
        extractTarget.setValue(transferDTO.getValue());
        transferDTO.getTarget().getExtracts().add(extractSource);
        accountController.update(transferDTO.getTarget());
    }

    public void checkAmount(Address userAddress) {
        try {
            MessageDTO response = new MessageDTO();
            response.setObject(accountController.getAmount());
            response.setBankMessage(EnumBankMessages.AMOUNT_RESULT);
            channel.send(userAddress, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkExtract(MessageDTO message, Address userAddress) {
        try {
            MessageDTO response = new MessageDTO();
            String cpf = (String) message.getObject();
            List<Extract> extracts = accountController.getAccountByCpf(cpf).getExtracts();
            response.setObject(extracts);
            response.setBankMessage(EnumBankMessages.EXTRACT_RESULT);
            channel.send(userAddress, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkBalance(MessageDTO message, Address userAddress) {
        try {
            MessageDTO response = new MessageDTO();
            String cpf = (String) message.getObject();
            BigDecimal balance = accountController.getAccountByCpf(cpf).getBalance();
            response.setObject(balance);
            response.setBankMessage(EnumBankMessages.BALANCE_RESULT);
            channel.send(userAddress, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
