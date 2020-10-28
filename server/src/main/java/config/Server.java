package config;

import DTO.MessageDTO;
import controller.UserController;
import model.User;
import model.enumeration.EnumBankMessages;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;

import java.util.List;
import java.util.Objects;

public class Server extends ReceiverAdapter implements RequestHandler {
    JChannel channel;
    UserController userController = new UserController();

    public void start (String channelName, String configs) throws Exception {
        /*quando o coordenador for instanciado ele gera o canal e os forks*/
        channel = new JChannel(configs);
        channel.setReceiver(this);
        channel.setDiscardOwnMessages(true);
        channel.connect(channelName);
    }

    /**
     * Métodos de debug para saber quando uma nova mensagem chegou ou uma nova view foi instanciada*/
    @Override
    public void receive(Message msg) {
        MessageDTO message = (MessageDTO) msg.getObject();
        Address userAddress = msg.getSrc();
        switch (message.getBankMessage()) {
            case LOGIN_CLIENT:
                loginClient(message, userAddress);
                break;
            case SAVE_CLIENT:
                saveClient(message, userAddress);
                break;
            case LOGOUT_CLIENT:
                logout(message, userAddress);
                break;
            default:
                break;
        }
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("\t** nova View do cluster: " + new_view);
    }

    /**
     * Método handle, para pegar a mensagem e verifica o que um canal deseja*/
    @Override
    public Object handle(Message msg) throws Exception {
        String pergunta = (String) msg.getObject();
        System.out.println("RECEBI uma mensagem: " + pergunta + "\n");
        return null;
    }

    private void loginClient(MessageDTO message, Address userAddress) {
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

    private void logout(MessageDTO message, Address userAddress) {
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

    private void saveClient(MessageDTO message, Address userAddress) {
        try {
            User user = (User) message.getObject();
            if (Objects.nonNull(user)) {
                MessageDTO response = new MessageDTO();
                // verificar campos obrigatorios se precisar
                if (Objects.nonNull(userController.findByCpf(user.getCpf()))) {
                    response.setObject("Usuário já existe no sistema");
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
