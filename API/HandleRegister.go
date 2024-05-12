package API

import (
	"APIMC/Connexion"
	"database/sql"
	_ "fmt"
	_ "github.com/mattn/go-sqlite3"
	_ "golang.org/x/crypto/bcrypt"
	"html/template"
	"log"
	"net/http"
)

type RegisterData struct {
	ErrorMessage string
}

func HandleRegister(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()
	usersWithCoins := GetAllUsersWithCoins()
	log.Println("Solde des pièces pour chaque utilisateur:", usersWithCoins)
	if r.Method == "POST" {
		username := r.FormValue("username")
		password := r.FormValue("password")
		email := r.FormValue("email")

		if !Connexion.ValidatePassword(password) {
			http.Error(w, "Le mot de passe ne respecte pas les conditions requises", http.StatusBadRequest)
			return
		}

		exists, err := Connexion.CheckUsernameExists(db, username)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if exists {
			data := struct {
				ExistingEmail    bool
				ExistingUsername bool
				ErrorMessage     string
				ErrorEmail       string
			}{
				ExistingEmail:    true,
				ExistingUsername: true,
				ErrorMessage:     "Le nom d'utilisateur est déjà pris",
				ErrorEmail:       "L'email est déjà pris",
			}

			tmpl, err := template.ParseFiles("templates/html/Connexion/register.html")
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}

			err = tmpl.Execute(w, data)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}

			return
		}

		emailExists, err := Connexion.CheckEmailExists(db, email)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if emailExists {
			data := RegisterData{
				ErrorMessage: "L'email est déjà utilisé",
			}
			tmpl, err := template.ParseFiles("templates/html/Connexion/register.html")
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			err = tmpl.Execute(w, data)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			return
		}

		hashedPassword, err := Connexion.HashPassword(password)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		token, err := GenerateToken()
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		coins := 0
		usersWithCoins := GetAllUsersWithCoins()
		if val, ok := usersWithCoins[username]; ok {
			coins = val
		}

		_, err = db.Exec("INSERT INTO utilisateurs (username, password, email, reset_token, coins) VALUES (?, ?, ?, ?, ?)", username, hashedPassword, email, token, coins)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		_, err = db.Exec("UPDATE utilisateurs SET date_inscription = CURRENT_TIMESTAMP WHERE username = ?", username)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		http.Redirect(w, r, "/login", http.StatusFound)
		return
	}

	tmpl, err := template.ParseFiles("templates/html/Connexion/register.html")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
}
