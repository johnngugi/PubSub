import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.net.InetAddress;

class Util {

    static AbstractXMPPConnection connect(String username, String password)
            throws InterruptedException, XMPPException, SmackException, IOException {
        InetAddress hostAddress = InetAddress.getByName("127.0.0.1");
        SmackConfiguration.DEBUG = true;
        // Create a connection to the jabber.org server on a specific port.
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setXmppDomain("strathmore-computer")
                .setHostAddress(hostAddress)
                .setPort(5222)
                .setResource("strathmore-student")
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .build();

        AbstractXMPPConnection conn;
        conn = new XMPPTCPConnection(config);
        conn.connect().login();
        return conn;
    }

}
