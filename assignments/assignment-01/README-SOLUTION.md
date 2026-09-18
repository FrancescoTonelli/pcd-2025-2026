# Poool solution

Run `mvn test` and then choose one of the two required implementations:

```text
mvn package
java -jar target/pcd-ass-01-1.0-SNAPSHOT.jar threads
java -jar target/pcd-ass-01-1.0-SNAPSHOT.jar executor
```

Arrow keys apply impulses to the human ball. The bot acts asynchronously. `PerformanceCheck`
compares the two concurrent implementations with the sequential baseline. The finite
`src/jpf` score model is kept separate so it can be explored with Java PathFinder.
