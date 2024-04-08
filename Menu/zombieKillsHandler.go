package Menu

import (
	"encoding/json"
	"net/http"
)

func ZombieKillsHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
	w.Header().Set("Access-Control-Allow-Headers", "Content-Type")

	if r.Method == "OPTIONS" {
		w.WriteHeader(http.StatusOK)
		return
	}

	err := json.NewDecoder(r.Body).Decode(&playerData)
	if err != nil {
		http.Error(w, "Erreur de désérialisation des données JSON: "+err.Error(), http.StatusBadRequest)
		return
	}

	jsonResponse, err := displayAndEncode(playerData)
	if err != nil {
		http.Error(w, "Erreur lors de l'encodage des données JSON: "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write(jsonResponse)
}
