import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class Subscriber implements ItemEventListener {
    private AbstractXMPPConnection conn;

    public void connect() throws InterruptedException, XMPPException, SmackException, IOException {
        InetAddress hostAddress = InetAddress.getByName("192.168.100.2");
        SmackConfiguration.DEBUG = true;
        // Create a connection to the jabber.org server on a specific port.
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword("student2", "password")
                .setXmppDomain("strathmore-computer")
                .setHostAddress(hostAddress)
                .setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .build();

        conn = new XMPPTCPConnection(config);
        conn.connect().login();
    }

    private void subscribe() {
        try {
            if (conn != null) {
                PubSubManager pubSubManager = PubSubManager.getInstance(conn);
                LeafNode eventNode = pubSubManager.getNode("testNode");
                eventNode.addItemEventListener(this);
                List<Subscription> subscriptions = eventNode.getSubscriptions();
                if (subscriptions.size() == 0) {
                    eventNode.subscribe(String.valueOf(conn.getUser()));
                } else {
                    for (Subscription subscription : subscriptions) {
                        Subscription.State state = subscription.getState();
                        System.out.println(subscription.toXML());
                        if (state != Subscription.State.subscribed) {
                            eventNode.subscribe(String.valueOf(conn.getUser()));
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
        Subscriber subscriber = null;
        try {
            subscriber = new Subscriber();
            subscriber.connect();
            subscriber.subscribe();
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
    }
}
