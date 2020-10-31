import control.RequestControl;

public class Main {
    public static void main(String[] args) {
        try {
            new RequestControl().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
