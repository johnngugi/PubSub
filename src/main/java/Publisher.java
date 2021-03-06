import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smackx.pubsub.*;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class Publisher {
    private AbstractXMPPConnection conn;

    private int sizeOfImageInBytes = 0;

    private Publisher(String username, String password)
            throws InterruptedException, IOException, SmackException, XMPPException {
        conn = Util.connect(username, password);
    }

    private String encodeFileToBase64Binary(File file) {
        String encodedFile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            sizeOfImageInBytes = fileInputStreamReader.read(bytes);
            encodedFile = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedFile;
    }

    private void publish() throws XMPPException.XMPPErrorException, SmackException.NotConnectedException,
            InterruptedException, SmackException.NoResponseException {
        // Create a pubsub manager using an existing XMPPConnection
        PubSubManager pubSubManager = PubSubManager.getInstance(conn);

        ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
        form.setAccessModel(AccessModel.open);          //anyone can access
        form.setDeliverPayloads(true);                 //allow payloads with notifications
        form.setPersistentItems(true);                  //save published items in storage @ server
        form.setPresenceBasedDelivery(false);          //notify subscribers even when they are offline
        form.setPublishModel(PublishModel.publishers);       //only publishers (owner) can post items to this node

        String filepath = "/home/john/Pictures/unnamed.jpg";
        File file = new File(filepath);
        String imageBase64 = encodeFileToBase64Binary(file);

        LeafNode leafNode;
        String msg = "<studentcouncil@strathmore.edu>\n" +
                "\t\n" +
                "\t\n" +
                "to AllStudents\n" +
                "Good afternoon Stratizens,\n" +
                "\n" +
                "Make your way to the auditorium at 2pm for an event that SUITSA is holding an information security forum.\n" +
                "\n" +
                "Attached is the poster for more details.\n" +
                "Register now to attend here: http://bit.ly/infosecday2018\n" +
                "Kind Regards,";
        StandardExtensionElement extFileNameBuilder = StandardExtensionElement.builder(
                "file", "jabber:client")
                .addElement("base64Bin", imageBase64)
                .addAttribute("name", file.getName())
                .addAttribute("size", "" + sizeOfImageInBytes)
                .build();

        Message message = new Message();
        message.setStanzaId();
        message.setSubject("Student Council");
        message.setBody(msg);
        message.addExtension(extFileNameBuilder);

//        String xmlMsg = "<message xmlns='pubsub:test:test'>" + msg + "</message>";
        SimplePayload payload = new SimplePayload(message.toXML("").toString());
        PayloadItem<SimplePayload> item = new PayloadItem<>("5", payload);
        try {
            leafNode = pubSubManager.getNode("testNode");
            LeafNode finalLeafNode = leafNode;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Scheduler scheduler = new Scheduler();
                    scheduler.scheduleTask(finalLeafNode, TimeUnit.MINUTES, 1, item);
                }
            });
            thread.start();
        } catch (XMPPException.XMPPErrorException | PubSubException.NotAPubSubNodeException e) {
            leafNode = (LeafNode) pubSubManager.createNode("testNode", form);
        }
//        leafNode.publish(item);

        // Scheduling task
//        LeafNode finalLeafNode = leafNode;
//        Scheduler scheduler = new Scheduler();
//        scheduler.scheduleTask(finalLeafNode, TimeUnit.MINUTES, 1, item);
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
