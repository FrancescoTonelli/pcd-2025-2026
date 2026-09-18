package pcd.assignment04.cluster;

import com.typesafe.config.ConfigFactory;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.pattern.Patterns;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClusterAlarmControllerTest {
    private static final ActorSystem SYSTEM = ActorSystem.create("recovery-test",
            ConfigFactory.parseString("pekko.actor.provider=local").withFallback(ConfigFactory.load()));

    @AfterAll static void close() { SYSTEM.terminate(); }

    @Test void startsSafeAndRequiresTheCorrectPin() throws Exception {
        ActorRef controller = SYSTEM.actorOf(ClusterAlarmController.props("42",
                Duration.ofSeconds(1), Duration.ofSeconds(1)));
        assertEquals(ClusterAlarmController.State.RECOVERY, stateOf(controller));
        controller.tell(new ClusterAlarmController.SensorEvent("door",
                ClusterAlarmController.SensorType.DOOR_WINDOW), ActorRef.noSender());
        controller.tell(new ClusterAlarmController.PinEntered("wrong"), ActorRef.noSender());
        assertEquals(ClusterAlarmController.State.RECOVERY, stateOf(controller));
        controller.tell(new ClusterAlarmController.PinEntered("42"), ActorRef.noSender());
        assertEquals(ClusterAlarmController.State.DISARMED, stateOf(controller));
    }

    private static ClusterAlarmController.State stateOf(ActorRef controller) throws Exception {
        return ((ClusterAlarmController.CurrentState) Patterns.ask(controller,
                new ClusterAlarmController.GetState(), Duration.ofSeconds(1))
                .toCompletableFuture().get(1, TimeUnit.SECONDS)).state();
    }
}
