package API

import (
	"html/template"
	"net/http"
)

func HandleAddToCart(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	itemID := r.URL.Query().Get("itemID")
	addToCart(itemID)

	http.Redirect(w, r, "/api/user/panier", http.StatusSeeOther)
}

func HandlePanier(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Méthode non autorisée", http.StatusMethodNotAllowed)
		return
	}

	tmpl, err := template.ParseFiles("templates/html/Menu/panier.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	err = tmpl.Execute(w, playerData)
	if err != nil {
		http.Error(w, "Erreur lors de l'exécution de la template HTML: "+err.Error(), http.StatusInternalServerError)
		return
	}
}

func addToCart(itemID string) {
	var itemToAdd ShopItem
	for _, item := range playerData.ShopItem {
		if item.ID == itemID {
			itemToAdd = item
			break
		}
	}
	playerData.Panier = append(playerData.Panier, PanierItem{
		ID:        itemToAdd.ID,
		ItemsName: itemToAdd.ItemsName,
		Quantity:  itemToAdd.Quantity,
		Price:     itemToAdd.Price,
		ImagePath: itemToAdd.ImagePath,
		ImageData: itemToAdd.ImageData,
	})
}
