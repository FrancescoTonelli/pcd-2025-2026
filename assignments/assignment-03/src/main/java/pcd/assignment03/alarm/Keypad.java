package pcd.assignment03.alarm;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;

public final class Keypad extends AbstractActor {
    public record Enter(String pin) { }
    private final ActorRef controller;

    public static Props props(ActorRef controller) { return Props.create(Keypad.class, () -> new Keypad(controller)); }
    private Keypad(ActorRef controller) { this.controller = controller; }

    public Receive createReceive() {
        return receiveBuilder().match(Enter.class,
                message -> controller.tell(new AlarmController.PinEntered(message.pin()), self())).build();
    }
}
