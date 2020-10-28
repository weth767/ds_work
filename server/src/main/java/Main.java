import config.Server;
import utils.Constants;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start(Constants.CHANNEL_NAME, Constants.CAST_XML);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
