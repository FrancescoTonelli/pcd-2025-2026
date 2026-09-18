package pcd.assignment04.cluster;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import java.time.Duration;

public final class ClusterNode {
    private static final String CONTROLLER_PATH = "pekko://smart-home@127.0.0.1:2551/user/controller";

    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Usage: ClusterNode <port> <controller|sensor|keypad>");
        int port = Integer.parseInt(args[0]);
        String role = args[1];
        Config config = ConfigFactory.parseString("pekko.remote.artery.canonical.port=" + port
                + "\npekko.cluster.roles=[\"" + role + "\"]").withFallback(ConfigFactory.load());
        ActorSystem system = ActorSystem.create("smart-home", config);
        switch (role) {
            case "controller" -> system.actorOf(ClusterAlarmController.props("1234",
                    Duration.ofSeconds(20), Duration.ofSeconds(10)), "controller");
            case "sensor" -> {
                ActorRef sensor = system.actorOf(DistributedSensor.props("front-door",
                        ClusterAlarmController.SensorType.DOOR_WINDOW, CONTROLLER_PATH), "front-door");
                sensor.tell(DistributedSensor.Trigger.INSTANCE, ActorRef.noSender());
            }
            case "keypad" -> {
                ActorRef keypad = system.actorOf(DistributedKeypad.props(CONTROLLER_PATH), "keypad");
                keypad.tell(new DistributedKeypad.Enter("1234"), ActorRef.noSender());
            }
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
