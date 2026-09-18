package pcd.assignment04.cluster;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;

public final class DistributedKeypad extends AbstractActor {
    public record Enter(String pin) { }
    private final String controllerPath;
    public static Props props(String controllerPath) {
        return Props.create(DistributedKeypad.class, () -> new DistributedKeypad(controllerPath));
    }
    private DistributedKeypad(String controllerPath) { this.controllerPath = controllerPath; }
    public Receive createReceive() {
        return receiveBuilder().match(Enter.class, message -> context().actorSelection(controllerPath)
                .tell(new ClusterAlarmController.PinEntered(message.pin()), self())).build();
    }
}
