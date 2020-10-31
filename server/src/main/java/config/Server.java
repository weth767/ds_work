package config;

import DTO.MessageDTO;
import model.enumeration.EnumBankMessages;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.RequestHandler;

import java.util.List;

public class Server extends ReceiverAdapter implements RequestHandler {
    JChannel channel;
    List<Address> members;
    LoginMethods loginMethods;
    ClientMethods clientMethods;
    AccountMethods accountMethods;

    public void start (String channelName, String configs) throws Exception {
        /*quando o coordenador for instanciado ele gera o canal e os forks*/
        channel = new JChannel(configs);
        channel.setReceiver(this);
        channel.setDiscardOwnMessages(true);
        channel.connect(channelName);
        loginMethods = new LoginMethods(channel);
        clientMethods = new ClientMethods(channel);
        accountMethods = new AccountMethods(channel);
    }

    /**
     * Métodos de debug para saber quando uma nova mensagem chegou ou uma nova view foi instanciada*/
    @Override
    public void receive(Message msg) {
        MessageDTO message = (MessageDTO) msg.getObject();
        Address userAddress = msg.getSrc();
        switch (message.getBankMessage()) {
            case LOGIN_CLIENT:
                loginMethods.loginClient(message, userAddress);
                break;
            case SAVE_CLIENT:
                clientMethods.saveClient(message, userAddress);
                break;
            case LOGOUT_CLIENT:
                loginMethods.logout(message, userAddress);
                break;
            case GET_BALANCE:
                accountMethods.checkBalance(message, userAddress);
                break;
            case GET_EXTRACT:
                accountMethods.checkExtract(message, userAddress);
                break;
            case GET_AMOUNT:
                accountMethods.checkAmount(userAddress);
                break;
            case TRANSFER_VALUE:
                accountMethods.transferValue(message, userAddress);
                break;
            case REQUEST_SERVER:
                try {
                    MessageDTO response = new MessageDTO();
                    response.setBankMessage(EnumBankMessages.YOU_WISH);
                    response.setObject("What you wish");
                    channel.send(userAddress, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                invalidOperation(userAddress);
                break;
        }
    }

    @Override
    public void viewAccepted(View new_view) {
        members = channel.getView().getMembers();
        System.out.println("Members: " + members);
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

    private void invalidOperation(Address userAddress) {
        try {
            MessageDTO response = new MessageDTO();
            response.setBankMessage(EnumBankMessages.INVALID_OPERATION_ERROR);
            response.setObject("Operação Invalída!");
            channel.send(userAddress, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
