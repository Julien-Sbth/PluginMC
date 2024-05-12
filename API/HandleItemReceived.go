package API

import (
	"encoding/json"
	"net/http"
)

func HandleItemReceived(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	var requestData struct {
		ItemID string `json:"itemID"`
	}
	err := json.NewDecoder(r.Body).Decode(&requestData)
	if err != nil {
		http.Error(w, "Erreur lors de la lecture des données de la requête: "+err.Error(), http.StatusBadRequest)
		return
	}

	err = markItemAsReceived(requestData.ItemID)
	if err != nil {
		http.Error(w, "Erreur lors de la mise à jour de l'item: "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write([]byte(`{"message": "Confirmation de l'item reçue."}`))
}
