package pcd.assignment03.alarm;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import java.time.Duration;

public final class AlarmApplication {
    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("smart-home-alarm");
        ActorRef controller = system.actorOf(AlarmController.props("1234", Duration.ofSeconds(3), Duration.ofSeconds(3)), "controller");
        ActorRef keypad = system.actorOf(Keypad.props(controller), "keypad");
        ActorRef motion = system.actorOf(Sensor.props("hall", Sensor.Type.MOTION, controller), "hall-motion");
        keypad.tell(new Keypad.Enter("1234"), ActorRef.noSender());
        Thread.sleep(4_000);
        motion.tell(Sensor.Trigger.INSTANCE, ActorRef.noSender());
        Thread.sleep(4_000);
        system.terminate();
    }
}
