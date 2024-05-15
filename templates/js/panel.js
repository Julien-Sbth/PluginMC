function fetchPlayers() {
    const playerList = document.getElementById("playerList");
    if (!playerList) return;

    fetch("/read")
        .then(response => response.json())
        .then(data => {
            playerList.innerHTML = "";

            if (data) {
                data.forEach(player => {
                    const listItem = document.createElement("li");
                    listItem.textContent = `ID: ${player.player_id[0]}, Name: ${player.player_name[0]}, BlockNames: ${player.block_name}, Items_name ${player.Inventory.item_name} Coins: ${player.coins}, Kills: ${player.kills[0]}, EntityType: ${player.entity_type[0]}, Blocks: ${player.Blocks.blocks_moved}, Achievements: ${player.Achievements.achievements}, ItemName: ${player.Inventory.item_name}, Quantity: ${player.Purchase.quantity}, Price: ${player.Purchase.price}, PurchaseDate: ${player.purchase_date[0]}, BlockName: ${player.block_name}, Position: ${player.Purchase.position[0]}, NomBlocks: ${player.nom_blocks}`;
                    playerList.appendChild(listItem);
                });
            } else {
                console.error('No player data received.');
            }
        })
        .catch(error => console.error('Error fetching players:', error));
}

// Variable globale pour stocker l'ID actuel
let currentId = 0;

function createPlayer() {
    // Récupère les valeurs des champs
    const player_id = document.getElementById("player_id").value;
    const nameBlock = document.getElementById("nameBlock").value;
    const player_name = document.getElementById("player_name").value;
    const coins = document.getElementById("coins").value;
    const kills = document.getElementById("kills").value;
    const entityType = document.getElementById("entity_type").value;
    const blocks = document.getElementById("blocks").value;
    const achievements = document.getElementById("achievements").value;
    const item_name = document.getElementById("item_name").value;
    const quantity = document.getElementById("quantity").value;
    const price = document.getElementById("price").value;
    const purchaseDate = document.getElementById("purchase_date").value;
    const position = document.getElementById("position").value;
    const nomItem = document.getElementById("nomItem").value;
    const block_name = document.getElementById("block_name").value;


    // Vérifie si tous les champs requis sont remplis
    if (!player_id || !player_name || !coins || !kills || !entityType || !blocks || !achievements || !item_name || !quantity || !price || !purchaseDate || !position || !nomItem) {
        console.error("Tous les champs doivent être remplis");
        return; // Arrête la fonction si un champ requis est manquant
    }
    const data = {
        player_id: player_id,
        player_name: player_name,
        kills: kills,
        entity_type: entityType,
        block_name: block_name,
        item_name: item_name,
        purchase_date: purchaseDate,
        price: parseInt(price),
        position: parseInt(position),
        coins: coins,

        Player: [{

        }],
        Inventory:{
            item_name: item_name,
        },
        Stats: {

        },
        Blocks: {
            blocks_moved: blocks
        },
        Achievements: {
            achievements: parseInt(achievements)
        },
        Purchase: {
            quantity: parseInt(quantity),
        },
        Block: {
            nom_blocks: nomItem
        },
        Coins: [{


        }]
    };

    console.log("Data to send:", data);

    // Envoie les données au serveur pour la création du joueur
    fetch("/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            console.log("Response received:", response);
            if (response.ok) {
                console.log("Player created successfully");
                fetchPlayers();
                document.getElementById("createPlayerForm").reset();
            } else {
                console.error("Failed to create player:", response.status);
            }
        })
        .catch(error => console.error("Error creating player:", error));
}

document.getElementById("deletePlayerForm").addEventListener("submit", function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    fetch("/delete", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (response.ok) {
                fetchPlayers();
                this.reset();
            } else {
                console.error('Delete player failed:', response.status);
            }
        })
        .catch(error => console.error('Error deleting player:', error));
});

function updatePlayer() {
    const updateId = document.getElementById("updateId").value;
    const updateName = document.getElementById("updateName").value;
    const updateScore = document.getElementById("updateScore").value;

    const data = {
        player_id: updateId,
        player_name: updateName,
        Stats: {
            score: parseInt(updateScore)
        }
    };

    fetch("/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                fetchPlayers();
                document.getElementById("updatePlayerForm").reset();
            } else {
                console.error('Update player failed:', response.status);
            }
        })
        .catch(error => console.error('Error updating player:', error));
}

document.addEventListener("DOMContentLoaded", fetchPlayers);