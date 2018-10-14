import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;

public class Publisher {
    private AbstractXMPPConnection conn;

    private Publisher(String username, String password)
            throws InterruptedException, IOException, SmackException, XMPPException {
        conn = Util.connect(username, password);
    }

    private void publish() throws XMPPException.XMPPErrorException, SmackException.NotConnectedException,
            InterruptedException, SmackException.NoResponseException {
        // Create a pubsub manager using an existing XMPPConnection
        PubSubManager pubSubManager = PubSubManager.getInstance(conn);

        ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
        form.setAccessModel(AccessModel.open);          //anyone can access
        form.setDeliverPayloads(true);                 //allow payloads with notif
        form.setPersistentItems(true);                  //save published items in storage @ server
        form.setPresenceBasedDelivery(false);          //notify subscribers even when they are offline
        form.setPublishModel(PublishModel.publishers);       //only publishers (owner) can post items to this node

        LeafNode leafNode;
        String msg = "Test5";
        Message message = new Message();
        message.setStanzaId();
        message.setSubject("Test");
        message.setBody(msg);
        SimplePayload payload = new SimplePayload(
                null, null, message.toXML());
        PayloadItem<SimplePayload> item = new PayloadItem<>(payload);
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
        Publisher publisher;
        try {
            publisher = new Publisher("student2", "password");
            publisher.publish();
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
    }
}
