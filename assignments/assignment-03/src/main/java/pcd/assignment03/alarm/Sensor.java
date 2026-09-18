package pcd.assignment03.alarm;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;

public final class Sensor extends AbstractActor {
    public enum Type { MOTION, DOOR_WINDOW }
    public enum Trigger { INSTANCE }
    private final String sensorId;
    private final Type type;
    private final ActorRef controller;

    public static Props props(String sensorId, Type type, ActorRef controller) {
        return Props.create(Sensor.class, () -> new Sensor(sensorId, type, controller));
    }

    private Sensor(String sensorId, Type type, ActorRef controller) {
        this.sensorId = sensorId;
        this.type = type;
        this.controller = controller;
    }

    public Receive createReceive() {
        return receiveBuilder().matchEquals(Trigger.INSTANCE,
                ignored -> controller.tell(new AlarmController.SensorEvent(sensorId, type), self())).build();
    }
}
