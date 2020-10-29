package config;

import DTO.MessageDTO;
import controller.UserController;
import model.User;
import model.enumeration.EnumBankMessages;
import org.jgroups.Address;
import org.jgroups.JChannel;

import java.util.List;
import java.util.Objects;

public class LoginMethods {
    UserController userController;
    JChannel channel;
    public LoginMethods(JChannel channel) {
        this.channel = channel;
        this.userController = new UserController();
    }

    public void loginClient(MessageDTO message, Address userAddress) {
        try {
            List<String> informations = (List<String>) message.getObject();
            MessageDTO response = new MessageDTO();
            if (Objects.isNull(informations) || informations.size() < 2) {
                response.setObject("Erro ao logar");
                response.setBankMessage(EnumBankMessages.ERROR);
                channel.send(userAddress, message);
                return;
            }
            User user = userController.findByUserPassWord(informations.get(0), informations.get(1));
            if (Objects.isNull(user)) {
                response.setObject("Login ou senha incorretos");
                response.setBankMessage(EnumBankMessages.ERROR);
            } else if(user.isLogged()) {
                response.setObject("Erro ao logar");
                response.setBankMessage(EnumBankMessages.ERROR);
            } else {
                user.setLogged(true);
                userController.update(user);
                response.setObject(user);
                response.setBankMessage(EnumBankMessages.LOGIN_SUCESS);
            }
            channel.send(userAddress, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout(MessageDTO message, Address userAddress) {
        try {
            MessageDTO response = new MessageDTO();
            User user = (User) message.getObject();
            user.setLogged(false);
            userController.update(user);
            response.setBankMessage(EnumBankMessages.LOGOUT_SUCESS);
            response.setObject("Deslogado com sucesso");
            channel.send(userAddress, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
