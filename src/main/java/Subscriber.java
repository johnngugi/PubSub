import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import java.io.IOException;
import java.net.InetAddress;

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

    public void receive() throws XMPPException.XMPPErrorException, SmackException.NotConnectedException,
            InterruptedException, SmackException.NoResponseException {
        // Create a pubsub manager using an existing XMPPConnection
        PubSubManager pubSubManager = PubSubManager.getInstance(conn);

        LeafNode eventNode = pubSubManager.getNode("testNode");
        eventNode.addItemEventListener(this);
        eventNode.subscribe(String.valueOf(conn.getUser()));
        while (true);
    }

    @Override
    public void handlePublishedItems(ItemPublishEvent itemPublishEvent) {
//        System.out.println("Item count: " + itemPublishEvent);
        for (Object obj: itemPublishEvent.getItems()) {
            PayloadItem item = (PayloadItem) obj;
            System.out.println("Payload: " + item.getPayload().toString() );
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
            subscriber.receive();
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
    }
}
