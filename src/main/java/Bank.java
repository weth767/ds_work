import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import view.StartScreen;

public class Bank extends ReceiverAdapter {
    JChannel channel;

    private void start() throws Exception{
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("Parana Internet Banking");
        channel.close();
    }

    public void receive(Message msg) {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }

    public void viewAccepted(View new_view) {
        System.out.println("\t\t[DEBUG] ** view: " + new_view);
    }
}
