import java.io.Serializable;
import java.util.ArrayList;

public class Direct implements Serializable {
    private String username;
    private ArrayList<Message> sentMessages;
    private ArrayList<Message> receivedMessages;

    public Direct(String username) {
        this.username = username;
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    public ArrayList<Message> getReceivedMessages() {
        return receivedMessages;
    }
}
