package pcd.assignment04.cluster;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;
import java.time.Duration;

public final class ClusterAlarmController extends AbstractActor {
    public enum State { RECOVERY, DISARMED, EXIT_DELAY, ARMED, ENTRY_DELAY, ALARM }
    public record PinEntered(String pin) implements ClusterMessage { }
    public record SensorEvent(String sensorId, SensorType type) implements ClusterMessage { }
    public record GetState() implements ClusterMessage { }
    public record CurrentState(State state) implements ClusterMessage { }
    public enum SensorType { MOTION, DOOR_WINDOW }
    private record Timeout(long generation) { }

    private final String pin;
    private final Duration exitDelay;
    private final Duration entryDelay;
    private State state;
    private long timerGeneration;

    public static Props props(String pin, Duration exitDelay, Duration entryDelay) {
        return Props.create(ClusterAlarmController.class,
                () -> new ClusterAlarmController(pin, exitDelay, entryDelay));
    }

    private ClusterAlarmController(String pin, Duration exitDelay, Duration entryDelay) {
        this.pin = pin; this.exitDelay = exitDelay; this.entryDelay = entryDelay;
    }

    @Override public void preStart() {
        // A newly created or restarted volatile controller never guesses the old security state.
        state = State.RECOVERY;
        System.out.println("Controller entered safe recovery mode; PIN required");
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(PinEntered.class, this::onPin)
                .match(SensorEvent.class, this::onSensor)
                .match(Timeout.class, this::onTimeout)
                .match(GetState.class, message -> sender().tell(new CurrentState(state), self()))
                .build();
    }

    private void onPin(PinEntered message) {
        if (!pin.equals(message.pin())) return;
        switch (state) {
            case RECOVERY, ENTRY_DELAY, ALARM -> transition(State.DISARMED);
            case DISARMED -> transitionWithTimer(State.EXIT_DELAY, exitDelay);
            default -> { }
        }
    }

    private void onSensor(SensorEvent message) {
        if (state == State.ARMED) transitionWithTimer(State.ENTRY_DELAY, entryDelay);
        else System.out.println("Sensor " + message.sensorId() + " ignored in " + state);
    }

    private void onTimeout(Timeout timeout) {
        if (timeout.generation() != timerGeneration) return;
        if (state == State.EXIT_DELAY) transition(State.ARMED);
        else if (state == State.ENTRY_DELAY) transition(State.ALARM);
    }

    private void transitionWithTimer(State next, Duration delay) {
        transition(next);
        long generation = timerGeneration;
        context().system().scheduler().scheduleOnce(delay, self(), new Timeout(generation),
                context().dispatcher(), self());
    }

    private void transition(State next) {
        state = next; timerGeneration++;
        System.out.println("Cluster alarm state: " + state);
    }
}
