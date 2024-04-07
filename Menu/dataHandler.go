package Menu

import (
	"encoding/base64"
	"encoding/json"
	"fmt"
	"io/ioutil"
)

func displayAndEncode(playerData PlayerData) ([]byte, error) {
	fmt.Println("Nouvelles données reçues depuis l'API Java:")
	fmt.Printf("Player ID: %s\nEntity Type: %s\n", playerData.PlayerID, playerData.EntityType)

	fmt.Println("Coins:")
	for _, coin := range playerData.Coins {
		fmt.Printf("Player ID: %s - Coins: %s\n", coin.PlayerID, coin.Coins)
	}

	fmt.Println("Items:")
	for _, item := range playerData.Items {
		fmt.Printf("Player UUID: %s - Item Data: %s - Amount: %s\n", item.PlayerUUID, item.ItemData, item.Amount)
	}

	fmt.Println("Kills:")
	for _, kill := range playerData.Kills {
		fmt.Printf("Player ID: %s - Entity Type: %s - Kills: %s\n", kill.PlayerID, kill.EntityType, kill.Kills)
	}

	fmt.Println("Blocks:")
	for _, block := range playerData.Blocks {
		fmt.Printf("Player ID: %s - Blocks: %s\n", block.PlayerID, block.Blocks)
	}

	fmt.Println("Shop:")
	for _, shop := range playerData.Shop {
		fmt.Printf("Player ID: %s - ID: %s - Amount: %s - ItemName: %s - Price: %s - PurchaseDate: %s\n", shop.PlayerID, shop.ID, shop.Amount, shop.ItemName, shop.Price, shop.PurchaseDate)
	}

	fmt.Println("Achievements:")
	for _, achievements := range playerData.Achievements {
		fmt.Printf("Player ID: %s - Coins: %s\n", achievements.PlayerID, achievements.Achievements)
	}

	for i, blocks_destroy := range playerData.BlocksDestroy {
		// Décoder l'image base64
		imageData, err := base64.StdEncoding.DecodeString(blocks_destroy.ImagePath)
		if err != nil {
			fmt.Printf("Erreur lors du décodage de l'image base64 pour l'élément %s: %s\n", blocks_destroy.BlockName, err)
			continue
		}

		// Créer un fichier pour enregistrer l'image
		imagePath := "blocks_destroy/" + blocks_destroy.NomBlocks + ".png"
		err = ioutil.WriteFile(imagePath, imageData, 0644)
		if err != nil {
			fmt.Printf("Erreur lors de l'enregistrement de l'image pour l'élément %s: %s\n", blocks_destroy.NomBlocks, err)
			continue
		}

		// Mettre à jour le chemin d'accès à l'image dans la structure Inventory
		playerData.BlocksDestroy[i].ImagePath = imagePath

		// Stocker l'image décodée pour l'affichage dans le template HTML
		playerData.BlocksDestroy[i].ImageData = base64.StdEncoding.EncodeToString(imageData)
	}

	for i, inventory := range playerData.Inventory {
		// Décoder l'image base64
		imageData, err := base64.StdEncoding.DecodeString(inventory.ImagePath)
		if err != nil {
			fmt.Printf("Erreur lors du décodage de l'image base64 pour l'élément %s: %s\n", inventory.NomItem, err)
			continue
		}

		// Créer un fichier pour enregistrer l'image
		imagePath := "images/" + inventory.NomItem + ".png"
		err = ioutil.WriteFile(imagePath, imageData, 0644)
		if err != nil {
			fmt.Printf("Erreur lors de l'enregistrement de l'image pour l'élément %s: %s\n", inventory.NomItem, err)
			continue
		}

		// Mettre à jour le chemin d'accès à l'image dans la structure Inventory
		playerData.Inventory[i].ImagePath = imagePath

		// Stocker l'image décodée pour l'affichage dans le template HTML
		playerData.Inventory[i].ImageData = base64.StdEncoding.EncodeToString(imageData)
	}

	responseData := ResponseData{
		PlayerID:      playerData.PlayerID,
		EntityType:    playerData.EntityType,
		Coins:         playerData.Coins,
		Items:         playerData.Items,
		Kills:         playerData.Kills,
		Blocks:        playerData.Blocks,
		Achievements:  playerData.Achievements,
		BlocksDestroy: playerData.BlocksDestroy,
		Inventory:     playerData.Inventory,
	}

	jsonResponse, err := json.Marshal(responseData)
	if err != nil {
		return nil, err
	}

	fmt.Println("JSON Response:", string(jsonResponse))

	return jsonResponse, nil
}
