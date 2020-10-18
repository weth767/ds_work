package model;

import DTO.MessageDTO;
import DTO.TransferDTO;
import controller.AccountController;
import controller.UserController;
import model.enumeration.EnumExecutedClass;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Bank extends ReceiverAdapter {
    JChannel channel;
    User user = null;


    public Bank(User user) {
        this.user = user;
    }

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
        File userFile;
        File accountFile;
        switch (messageDTO.getExecutedClass()) {
            case USER:
                UserController controller = new UserController();
                User user = (User) messageDTO.getObject();
                if (Objects.isNull(controller.findById(user.getId()))) {
                    controller.save((User) messageDTO.getObject());
                }
                userFile = new File(Constants.userPathFile);
                accountFile = new File(Constants.accountPathFile);
                try {
                    byte[] userContent = Files.readAllBytes(userFile.toPath());
                    byte[] accountContent = Files.readAllBytes(accountFile.toPath());
                    MessageDTO messageDTO1 = new MessageDTO();
                    messageDTO1.setExecutedClass(EnumExecutedClass.REPLICATE);
                    messageDTO1.setObject(new ArrayList<>(Arrays.asList(userContent, accountContent)));
                    this.send(messageDTO1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACCOUNT:
                TransferDTO transferDTO = (TransferDTO) messageDTO.getObject();
                if (transferDTO.getSource().getOwner().getId().equals(this.user.getId())) {
                    userFile = new File(Constants.userPathFile);
                    accountFile = new File(Constants.accountPathFile);
                    try {
                        byte[] userContent = Files.readAllBytes(userFile.toPath());
                        byte[] accountContent = Files.readAllBytes(accountFile.toPath());
                        MessageDTO messageDTO1 = new MessageDTO();
                        messageDTO1.setExecutedClass(EnumExecutedClass.REPLICATE);
                        messageDTO1.setObject(new ArrayList<>(Arrays.asList(userContent, accountContent)));
                        this.send(messageDTO1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                /*AccountController accountController = new AccountController();
                accountController.transferValue(transferDTO.getSource(),
                        transferDTO.getTarget().getOwner().getCpf(), transferDTO.getValue());*/
                break;
            case REPLICATE:
                try {
                    Files.deleteIfExists(new File(Constants.accountPathFile).toPath());
                    Files.deleteIfExists(new File(Constants.userPathFile).toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ArrayList<byte[]> contents = (ArrayList<byte[]>) messageDTO.getObject();
                try (FileOutputStream fos = new FileOutputStream(Constants.userPathFile)) {
                    fos.write(contents.get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try (FileOutputStream fos = new FileOutputStream(Constants.accountPathFile)) {
                    fos.write(contents.get(1));
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
