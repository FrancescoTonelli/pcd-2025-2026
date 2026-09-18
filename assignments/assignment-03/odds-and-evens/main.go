package main

import (
	"fmt"
	"math/rand"
	"os"
	"strconv"
)

type Player struct {
	ID   int
	Seed int64
}

type playRequest struct {
	round int
	reply chan<- int
}

func playerAgent(player Player, requests <-chan playRequest) {
	random := rand.New(rand.NewSource(player.Seed))
	for request := range requests {
		request.reply <- random.Intn(6)
	}
}

func playMatch(round int, first Player, second Player, firstAgent chan<- playRequest,
	secondAgent chan<- playRequest, result chan<- Player) {
	firstChoice := make(chan int)
	secondChoice := make(chan int)
	firstAgent <- playRequest{round: round, reply: firstChoice}
	secondAgent <- playRequest{round: round, reply: secondChoice}
	a, b := <-firstChoice, <-secondChoice
	// First player chooses even, second chooses odd. A tie is impossible by parity.
	if (a+b)%2 == 0 {
		result <- first
	} else {
		result <- second
	}
}

func tournament(players []Player) Player {
	agents := make(map[int]chan playRequest, len(players))
	for _, player := range players {
		requests := make(chan playRequest)
		agents[player.ID] = requests
		go playerAgent(player, requests)
	}

	current := players
	for round := 1; len(current) > 1; round++ {
		results := make(chan Player)
		for index := 0; index < len(current); index += 2 {
			first, second := current[index], current[index+1]
			go playMatch(round, first, second, agents[first.ID], agents[second.ID], results)
		}
		next := make([]Player, 0, len(current)/2)
		for range len(current) / 2 {
			next = append(next, <-results)
		}
		current = next
	}
	for _, requests := range agents {
		close(requests)
	}
	return current[0]
}

func main() {
	exponent := 3
	if len(os.Args) > 1 {
		value, err := strconv.Atoi(os.Args[1])
		if err != nil || value < 1 || value > 20 {
			panic("m must be between 1 and 20")
		}
		exponent = value
	}
	players := make([]Player, 1<<exponent)
	for index := range players {
		players[index] = Player{ID: index + 1, Seed: int64(index + 1)}
	}
	fmt.Printf("Winner: player %d\n", tournament(players).ID)
}
