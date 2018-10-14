import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

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
                List<Subscription> subscriptions = eventNode.getSubscriptions();
                System.out.println("User: " + conn.getUser());
                if (subscriptions.size() == 0) {
                    eventNode.subscribe(String.valueOf(conn.getUser()));
                } else {
                    for (Subscription subscription : subscriptions) {
                        System.out.println(subscription.toXML(""));
                        Jid jid = subscription.getJid();
                        EntityFullJid userJid = conn.getUser();
                        Subscription.State state = subscription.getState();
                        if (userJid == jid && state != Subscription.State.subscribed) {
                            eventNode.subscribe(String.valueOf(userJid));
                        }
                    }
                }
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
