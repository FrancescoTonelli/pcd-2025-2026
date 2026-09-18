package pcd.assignment04.cluster;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorSelection;
import org.apache.pekko.actor.Props;

public final class DistributedSensor extends AbstractActor {
    public enum Trigger { INSTANCE }
    private final String sensorId;
    private final ClusterAlarmController.SensorType type;
    private final String controllerPath;

    public static Props props(String id, ClusterAlarmController.SensorType type, String controllerPath) {
        return Props.create(DistributedSensor.class, () -> new DistributedSensor(id, type, controllerPath));
    }

    private DistributedSensor(String id, ClusterAlarmController.SensorType type, String controllerPath) {
        this.sensorId = id; this.type = type; this.controllerPath = controllerPath;
    }

    public Receive createReceive() {
        return receiveBuilder().matchEquals(Trigger.INSTANCE, ignored -> {
            ActorSelection controller = context().actorSelection(controllerPath);
            controller.tell(new ClusterAlarmController.SensorEvent(sensorId, type), self());
        }).build();
    }
}
