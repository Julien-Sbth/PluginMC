package API

import "log"

func GetAllUsersWithCoins() map[string]int {
	users := make(map[string]int)

	for _, coin := range playerData.Coins {
		if _, ok := users[coin.PlayerName]; ok {
			users[coin.PlayerName] += convertStringToInt(coin.Coins)
		} else {
			users[coin.PlayerName] = convertStringToInt(coin.Coins)
		}
	}

	for user, coins := range users {
		log.Printf("Utilisateur: %s - Solde des pièces: %d\n", user, coins)
	}

	return users
}
