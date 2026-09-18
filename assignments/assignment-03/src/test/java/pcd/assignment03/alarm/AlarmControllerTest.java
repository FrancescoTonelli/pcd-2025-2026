package pcd.assignment03.alarm;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.pattern.Patterns;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AlarmControllerTest {
    private static final ActorSystem SYSTEM = ActorSystem.create("alarm-test");
    @AfterAll static void close() { SYSTEM.terminate(); }

    @Test void correctPinAndTimeoutArmTheSystem() throws Exception {
        ActorRef controller = SYSTEM.actorOf(AlarmController.props("7", Duration.ofMillis(20), Duration.ofSeconds(1)));
        controller.tell(new AlarmController.PinEntered("7"), ActorRef.noSender());
        Thread.sleep(100);
        AlarmController.CurrentState state = (AlarmController.CurrentState) Patterns
                .ask(controller, new AlarmController.GetState(), Duration.ofSeconds(1))
                .toCompletableFuture().get(1, TimeUnit.SECONDS);
        assertEquals(AlarmController.State.ARMED, state.state());
    }
}
