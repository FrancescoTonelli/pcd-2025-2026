# Assignment 04 solution

## Clustered alarm

Build with `mvn package`, then start three separate JVMs (controller first):

```text
mvn exec:java -Dexec.mainClass=pcd.assignment04.cluster.ClusterNode -Dexec.args="2551 controller"
mvn exec:java -Dexec.mainClass=pcd.assignment04.cluster.ClusterNode -Dexec.args="2552 sensor"
mvn exec:java -Dexec.mainClass=pcd.assignment04.cluster.ClusterNode -Dexec.args="0 keypad"
```

All cross-node communication is by serializable actor messages. The controller always starts in
`RECOVERY`; sensor events are ignored until the correct PIN explicitly establishes `DISARMED`.

## RMI Tic-Tac-Toe

Run `TicTacToeServer`, then start clients with:

```text
<host> <port> <create|join> <game-name> <player-name>
```

The lobby creates and returns remote game objects. Every game owns its board and synchronizes its
remote operations, making concurrent join and move requests atomic.
