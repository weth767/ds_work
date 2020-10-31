package control;

import DTO.MessageDTO;
import model.Extract;
import model.User;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.RequestHandler;
import utils.Constants;
import view.ExtractScreen;
import view.MainScreen;
import view.StartScreen;

import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class RequestControl extends ReceiverAdapter implements RequestHandler {
    public JChannel channel;
    StartScreen startScreen;
    MainScreen mainScreen;
    public Address serverAddress;
    public boolean serverAlreadyResponse = false;

    public void start() throws Exception {
        channel = new JChannel(Constants.CAST_XML);
        channel.setReceiver(this);
        channel.setDiscardOwnMessages(true);
        channel.connect(Constants.CHANNEL_NAME);
        startScreen = new StartScreen(this);
        startScreen.setVisible(true);
    }

    /**
     * Métodos de debug para saber quando uma nova mensagem chegou ou uma nova view foi instanciada*/
    @Override
    public void receive(Message msg) {
        MessageDTO messageDTO = (MessageDTO) msg.getObject();
        switch (messageDTO.getBankMessage()) {
            case LOGIN_SUCESS:
                User user = (User) messageDTO.getObject();
                mainScreen = new MainScreen(user, this);
                mainScreen.setVisible(true);
                break;
            case LOGOUT_SUCESS:
                startScreen.dispose();
                startScreen = new StartScreen(this);
                startScreen.setVisible(true);
                break;
            case SAVE_CLIENT_SUCESS:
                JOptionPane.showMessageDialog(null,
                        "Usuário cadastrado com sucesso");
                startScreen = new StartScreen(this);
                startScreen.setVisible(true);
                break;
            case AMOUNT_RESULT:
            case BALANCE_RESULT:
                showValue((BigDecimal) messageDTO.getObject());
                break;
            case TRANSFER_SUCESS:
                JOptionPane.showMessageDialog(null, "Transferência realizada com sucesso");
                break;
            case EXTRACT_RESULT:
                List<Extract> extracts = (List<Extract>) messageDTO.getObject();
                ExtractScreen extractScreen = new ExtractScreen(mainScreen, true, extracts);
                extractScreen.setVisible(true);
                break;
            case ERROR:
                JOptionPane.showMessageDialog(null, messageDTO.getObject());
                startScreen = new StartScreen(this);
                startScreen.setVisible(true);
                break;
            case YOU_WISH:
                /*garante o get_first*/
                if (!serverAlreadyResponse) {
                    this.serverAlreadyResponse = true;
                    this.serverAddress = msg.getSrc();
                }
                break;
            default:
                JOptionPane.showMessageDialog(null, messageDTO.getObject());
                break;
        }
    }

    private void showValue(BigDecimal value) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        JOptionPane.showMessageDialog(null, "Seu saldo é: R$"
                +  new DecimalFormat("#,##0.00", decimalFormatSymbols).format(value));
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("\t** nova View do cluster: " + new_view);
    }

    /**
     * Método handle, para pegar a mensagem e verifica o que um canal deseja*/
    @Override
    public Object handle(Message msg) throws Exception { // responde requisições recebidas
        return null;
    }
}
