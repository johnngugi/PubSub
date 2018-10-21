import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import java.io.IOException;

public class Subscriber implements ItemEventListener {
    private AbstractXMPPConnection conn;

    private Subscriber(String username, String password)
            throws InterruptedException, IOException, SmackException, XMPPException {
        conn = Util.connect(username, password);
    }

    private void subscribe() {
        try {
            if (conn != null) {
                PubSubManager pubSubManager = PubSubManager.getInstance(conn);
                LeafNode eventNode = pubSubManager.getNode("testNode");
                eventNode.addItemEventListener(this);
            }
        } catch (InterruptedException | XMPPException | SmackException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlePublishedItems(ItemPublishEvent itemPublishEvent) {
//        System.out.println("Item count: " + itemPublishEvent);
        for (Object obj : itemPublishEvent.getItems()) {
            PayloadItem item = (PayloadItem) obj;
            System.out.println("Payload: " + item.getPayload().toString());
        }
    }

    public AbstractXMPPConnection getConn() {
        return conn;
    }

    public static void main(String[] args) {
        Subscriber subscriber;
        try {
            subscriber = new Subscriber("student1", "password");
            subscriber.subscribe();
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
    }
}
