package pcd.assignment03.alarm;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;
import java.time.Duration;

public final class AlarmController extends AbstractActor {
    public enum State { DISARMED, EXIT_DELAY, ARMED, ENTRY_DELAY, ALARM }
    public record PinEntered(String pin) { }
    public record SensorEvent(String sensorId, Sensor.Type type) { }
    public record GetState() { }
    public record CurrentState(State state) { }
    private record Timeout(long generation) { }

    private final String pin;
    private final Duration exitDelay;
    private final Duration entryDelay;
    private State state = State.DISARMED;
    private long timerGeneration;

    public static Props props(String pin, Duration exitDelay, Duration entryDelay) {
        return Props.create(AlarmController.class, () -> new AlarmController(pin, exitDelay, entryDelay));
    }

    private AlarmController(String pin, Duration exitDelay, Duration entryDelay) {
        this.pin = pin;
        this.exitDelay = exitDelay;
        this.entryDelay = entryDelay;
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
            case DISARMED -> transitionWithTimer(State.EXIT_DELAY, exitDelay);
            case ENTRY_DELAY, ALARM -> transition(State.DISARMED);
            default -> { }
        }
    }

    private void onSensor(SensorEvent event) {
        if (state == State.ARMED) transitionWithTimer(State.ENTRY_DELAY, entryDelay);
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
        state = next;
        timerGeneration++;
        System.out.println("Alarm state: " + state);
    }
}
