import DTO.MessageDTO;
import model.Extract;
import model.User;
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

public class Main extends ReceiverAdapter implements RequestHandler {
    JChannel channel;
    StartScreen startScreen;

    private void start() throws Exception {
        channel = new JChannel(Constants.CAST_XML);
        channel.setReceiver(this);
        channel.setDiscardOwnMessages(true);
        channel.connect(Constants.CHANNEL_NAME);
        StartScreen startScreen = new StartScreen(channel);
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
                MainScreen mainScreen = new MainScreen(user, channel);
                mainScreen.setVisible(true);
                break;
            case SAVE_CLIENT_SUCESS:
                JOptionPane.showMessageDialog(null,
                        "Usuário cadastrado com sucesso");
                startScreen = new StartScreen(channel);
                startScreen.setVisible(true);
                break;
            case AMOUNT_RESULT:
                showValue((BigDecimal) messageDTO.getObject());
                break;
            case TRANSFER_SUCESS:
                JOptionPane.showMessageDialog(null, "Transferência realizada com sucesso");
                break;
            case EXTRACT_RESULT:
                List<Extract> extracts = (List<Extract>) messageDTO.getObject();
                ExtractScreen extractScreen = new ExtractScreen(null, true, extracts);
                extractScreen.setVisible(true);
                break;
            case BALANCE_RESULT:
                showValue((BigDecimal) messageDTO.getObject());
            case ERROR:
                JOptionPane.showMessageDialog(null, messageDTO.getObject());
                startScreen = new StartScreen(channel);
                startScreen.setVisible(true);
            default:
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

    public static void main(String[] args) {
        try {
            new Main().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
