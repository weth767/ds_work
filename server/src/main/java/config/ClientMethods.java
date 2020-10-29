package config;

import DTO.MessageDTO;
import controller.AccountController;
import controller.UserController;
import model.Account;
import model.User;
import model.enumeration.EnumBankMessages;
import org.jgroups.Address;
import org.jgroups.JChannel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientMethods {
    UserController userController;
    AccountController accountController;
    JChannel channel;
    public ClientMethods(JChannel channel) {
        this.channel = channel;
        this.userController = new UserController();
        this.accountController = new AccountController();
    }

    public void saveClient(MessageDTO message, Address userAddress) {
        try {
            List<Object> userInformations = (List<Object>) message.getObject();
            MessageDTO response = new MessageDTO();
            if (userInformations.size() < 2 ||
                    userInformations.stream().filter(userInfo -> Objects.isNull(null)).count() > 1) {
                response.setObject("Erro ao receber informações do usuário");
                response.setBankMessage(EnumBankMessages.ERROR);
            }
            User user = (User) userInformations.get(0);
            Account account = (Account) userInformations.get(1);
            if (Objects.nonNull(user)) {

                // verificar campos obrigatorios se precisar
                if (Objects.nonNull(userController.findByCpf(user.getCpf()) )
                        || Objects.nonNull(userController.findByUserName(user.getUsername()))) {
                    response.setObject("Usuário já existe no sistema, não pode ser cadastrado novamente");
                    response.setBankMessage(EnumBankMessages.ERROR);
                } else {
                    userController.save(user);
                    accountController.save(account);
                    response.setObject("Usuário cadastrado com sucesso");
                    response.setBankMessage(EnumBankMessages.SAVE_CLIENT_SUCESS);
                }
                channel.send(userAddress, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
