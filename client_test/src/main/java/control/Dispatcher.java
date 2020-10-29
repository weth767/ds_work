package control;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.RspList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Dispatcher extends MessageDispatcher {
    /**
     * Classe do despachante de mensagens, responsável pelos métodos de envio de mensagem entre os membros*/
    public Dispatcher(JChannel channel, RequestHandler handler) {
        super(channel, handler);
    }

    /**
     * Método para enviar message multicast para um grupo seleto ou para todos
     * @param <T> interface polimorfica de classe(permite receber o valor de qualquer tipo)
     * @param message objeto da mensagem do JGROUPS
     * @param targets lista de alvos da mensagem(podendo ser um grupo ou caso precise ser todos basta passar null)
     * @param needMajorityApproves verificador se necessita da aprovação da maioria ou do primeiro
     * @return um objeto rsplist do jgroups contendo os dados enviados por um dos membros*/
    public <T>RspList<T> sendMultiCastMessage(Message message, List<Address> targets, boolean needMajorityApproves) {
        try {
            RequestOptions requestOptions = new RequestOptions();
            if (needMajorityApproves) {
                requestOptions.setMode(ResponseMode.GET_MAJORITY);
            } else {
                requestOptions.setMode(ResponseMode.GET_FIRST);
            }
            requestOptions.setAnycasting(false);
            return castMessage(targets, message, requestOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método para enviar mensagem a um dos membros aleatoriamente dentre um grupo seleto de membros
     * ou todos
     * @param message objeto de mensagem de JGROUPS
     * @param targets lista de alvos para terem change de receber a mensagem anycast ou null para não
     * selecionar membros
     * @return objeto rsplist com os dados da resposta*/
    public RspList<Object> sendAnyCastMessage(Message message, List<Address> targets) {
        try {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.setMode(ResponseMode.GET_FIRST);
            requestOptions.setAnycasting(true);
            return castMessage(targets, message, requestOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método para enviar mensagem unicast
     * @param message objeto de mensagem do JGROUPS
     * @param target alvo da comunicação unicast(obrigatorio)
     * @return objeto de resposta rsplist*/
    public RspList<Object> sendUniCastMessage(Message message, Address target) {
        if (Objects.nonNull(target)) {
            try {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.setMode(ResponseMode.GET_FIRST);
                requestOptions.setAnycasting(false);
                return castMessage(Collections.singletonList(target), message, requestOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
