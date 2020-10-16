package model;

import DTO.MessageDTO;
import DTO.TransferDTO;
import connection.Connection;
import controller.AccountController;
import controller.Controller;
import model.enumeration.EnumExecutedClass;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class Bank extends ReceiverAdapter {
    JChannel channel;
    String pathFile = "src/main/resources/database.sqlite";

    public void send(MessageDTO message) throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("Parana Internet Banking");
        Message msg = new Message(null, null, message);
        channel.send(msg);
    }

    public void close() {
        channel.close();
    }

    public void receive(Message msg) {
        MessageDTO messageDTO = (MessageDTO) msg.getObject();
        switch (messageDTO.getExecutedClass()) {
            case USER:
                Controller controller = new Controller(Connection.getConnection());
                User user = (User) messageDTO.getObject();
                if (Objects.isNull(controller.findById(user.getId(), "User"))) {
                    controller.save(messageDTO.getObject());
                }
                File db = new File(pathFile);
                try {
                    byte[] content = Files.readAllBytes(db.toPath());
                    MessageDTO messageDTO1 = new MessageDTO();
                    messageDTO1.setExecutedClass(EnumExecutedClass.REPLICATE);
                    messageDTO1.setObject(content);
                    this.send(messageDTO1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACCOUNT:
                TransferDTO transferDTO = (TransferDTO) messageDTO.getObject();
                AccountController accountController = new AccountController(Connection.getConnection());
                accountController.transferValue(transferDTO.getSource(), transferDTO.getTarget().getOwner().getCpf(), transferDTO.getValue());
                break;
            case REPLICATE:
                byte[] fileData = (byte[]) messageDTO.getObject();
                try (FileOutputStream fos = new FileOutputStream(pathFile)) {
                    fos.write(fileData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void viewAccepted(View new_view) {
        System.out.println("\t\t[DEBUG] ** view: " + new_view);
    }
}
