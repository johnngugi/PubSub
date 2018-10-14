import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.net.InetAddress;

public class Publisher {
    private AbstractXMPPConnection conn;

    public void connect() throws InterruptedException, XMPPException, SmackException, IOException {
        InetAddress hostAddress = InetAddress.getByName("127.0.0.1");
        // Create a connection to the jabber.org server on a specific port.
        SmackConfiguration.DEBUG = true;
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

    public void publish() throws XMPPException.XMPPErrorException, SmackException.NotConnectedException,
            InterruptedException, SmackException.NoResponseException {
        // Create a pubsub manager using an existing XMPPConnection
        PubSubManager pubSubManager = PubSubManager.getInstance(conn);

        ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
        form.setAccessModel(AccessModel.open);          //anyone can access
        form.setDeliverPayloads(true);                 //allow payloads with notif
        form.setPersistentItems(true);                  //save published items in storage @ server
        form.setPresenceBasedDelivery(false);          //notify subscribers even when they are offline
        form.setPublishModel(PublishModel.publishers);       //only publishers (owner) can post items to this node

        LeafNode leafNode = null;
        String msg = "Test2";
        String xmlMsg = "<message xmlns='pubsub:test:test'><body>" + msg + "</body></message>";
        SimplePayload payload = new SimplePayload(
                "test", "pubsub:test:test", xmlMsg);
        PayloadItem<SimplePayload> item = new PayloadItem<>("5", payload);
        try {
            leafNode = pubSubManager.getNode("testNode");
        } catch (XMPPException.XMPPErrorException e) {
            leafNode = (LeafNode) pubSubManager.createNode("testNode", form);
        }
        leafNode.publish(item);
    }

    public AbstractXMPPConnection getConn() {
        return conn;
    }

    public static void main(String[] args) {
        Publisher publisher = null;
        try {
            publisher = new Publisher();
            publisher.connect();
            publisher.publish();
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
    }
}
