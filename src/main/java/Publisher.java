import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.net.InetAddress;

public class Publisher {
    private AbstractXMPPConnection conn;

    public void connect() throws InterruptedException, XMPPException, SmackException, IOException {
        InetAddress hostAddress = InetAddress.getByName("192.168.100.2");
        // Create a connection to the jabber.org server on a specific port.
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword("student1", "password")
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
        form.setPublishModel(PublishModel.open);       //only publishers (owner) can post items to this node
//        form.setNodeType(NodeType.collection);
//        form.setChildrenAssociationPolicy(ChildrenAssociationPolicy.all);
//        form.setChildrenMax(65536);

//        LeafNode leafNode = (LeafNode) pubSubManager.createNode("testNode", form);
//        Item item = new PayloadItem<>("message", payload);
        String msg = "Test2";
        String xmlMsg = "<message xmlns='pubsub:test:test'><body>" + msg + "</body></message>";
        SimplePayload payload = new SimplePayload(
                "test", "pubsub:test:test", xmlMsg);
        PayloadItem<SimplePayload> item = new PayloadItem<>("5", payload);
        LeafNode leafNode = pubSubManager.getNode("testNode");
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
