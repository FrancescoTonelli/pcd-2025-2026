package main

import "testing"

func TestTournamentReturnsOneOfThePlayers(t *testing.T) {
	players := []Player{{1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7}, {8, 8}}
	winner := tournament(players)
	if winner.ID < 1 || winner.ID > 8 {
		t.Fatalf("invalid winner: %d", winner.ID)
	}
}
