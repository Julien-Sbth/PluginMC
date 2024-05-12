package API

import (
	"APIMC/Connexion"
	"database/sql"
	"html/template"
	"net/http"
)

func HandleAdministrator(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()

	if r.Method == "POST" {
		adminUsername := r.FormValue("username")
		adminPassword := r.FormValue("password")
		email := r.FormValue("email")

		hashedPassword, err := Connexion.HashPassword(adminPassword)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = db.Exec("INSERT INTO utilisateurs (username, password, email, est_admin) VALUES (?, ?, ?, 1)", adminUsername, hashedPassword, email)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		_, err = db.Exec("UPDATE utilisateurs SET date_inscription = CURRENT_TIMESTAMP WHERE username = ?", adminUsername)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		http.Redirect(w, r, "/login", http.StatusFound)
		return
	}

	tmpl, err := template.ParseFiles("templates/html/Connexion/admin.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
}
