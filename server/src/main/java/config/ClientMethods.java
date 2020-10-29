package config;

import DTO.MessageDTO;
import controller.UserController;
import model.User;
import model.enumeration.EnumBankMessages;
import org.jgroups.Address;
import org.jgroups.JChannel;

import java.util.Objects;

public class ClientMethods {
    UserController userController;
    JChannel channel;
    public ClientMethods(JChannel channel) {
        this.channel = channel;
        this.userController = new UserController();
    }

    public void saveClient(MessageDTO message, Address userAddress) {
        try {
            User user = (User) message.getObject();
            if (Objects.nonNull(user)) {
                MessageDTO response = new MessageDTO();
                // verificar campos obrigatorios se precisar
                if (Objects.nonNull(userController.findByCpf(user.getCpf()) )
                        || Objects.nonNull(userController.findByUserName(user.getUsername()))) {
                    response.setObject("Usuário já existe no sistema, não pode ser cadastrado novamente");
                    response.setBankMessage(EnumBankMessages.ERROR);
                } else {
                    userController.save(user);
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
