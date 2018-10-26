import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private Timer timer = new Timer("publish timer");

    public void scheduleTask(LeafNode leafNode, TimeUnit timeUnit, long delay, Item item) {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                System.out.println("Task performed on " + new Date());
                try {
                    leafNode.publish(item);
                } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException |
                        SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
                cancel();
            }
        };
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(repeatedTask, delay, timeUnit);
        executor.shutdown();
        timer.cancel();
    }

}
