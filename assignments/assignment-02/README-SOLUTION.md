# FSStatLib solution

The three packages intentionally keep separate report types and implementations:

- `eventloop`: Vert.x asynchronous filesystem API; handlers never block.
- `reactive`: a cold RxJava `Flowable` tree reduced to one `Single<FSReport>`.
- `virtualthreads`: ordinary blocking filesystem code run by virtual threads.

Run every verification with `mvn test`. Each package contains a minimal `Example` main class.
