# Assignment 03 solution

## Smart Home Alarm

The Maven project models controller, keypad and sensors as Pekko actors. All interaction uses
immutable messages. The controller owns the complete state machine and uses generation-tagged
timeout messages, so an obsolete timer cannot change a newer state.

Run with `mvn test` and `mvn exec:java -Dexec.mainClass=pcd.assignment03.alarm.AlarmApplication`.

## Odds-and-Evens

The Go program is under `odds-and-evens`. Every player is an independent goroutine. Matches and
rounds exchange immutable values only through unbuffered channels; no game state is shared.

Run `go test ./...` and `go run . 3` (where `3` means 2^3 players).
