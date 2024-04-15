package Menu

import (
	"html/template"
	"net/http"
)

func HandleShop(w http.ResponseWriter, r *http.Request) {
	tmpl, err := template.ParseFiles("templates/html/shop.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
