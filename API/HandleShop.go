package API

import (
	"html/template"
	"net/http"
)

func HandleShop(w http.ResponseWriter, r *http.Request) {
	tmpl, err := template.ParseFiles("templates/html/Menu/shop.html")
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
