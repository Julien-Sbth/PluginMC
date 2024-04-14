package Menu

import (
	"html/template"
	"net/http"
)

func HandleMenu(w http.ResponseWriter, r *http.Request) {
	tmpl, err := template.ParseFiles("templates/html/index.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
