package Connexion

import (
	"crypto/rand"
	"database/sql"
	"encoding/base64"
	_ "fmt"
	_ "github.com/mattn/go-sqlite3"
	"golang.org/x/crypto/bcrypt"
	_ "golang.org/x/crypto/bcrypt"
	"html/template"
	"net/http"
)

func generateToken() (string, error) {
	token := make([]byte, 32)
	_, err := rand.Read(token)
	if err != nil {
		return "", err
	}
	return base64.StdEncoding.EncodeToString(token), nil
}

type RegisterData struct {
	ErrorMessage string
}

func HandleAdministrator(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()

	if r.Method == "POST" {
		// Récupération des données du formulaire
		adminUsername := r.FormValue("username")
		adminPassword := r.FormValue("password")
		email := r.FormValue("email")

		// Hashage du mot de passe
		hashedPassword, err := hashPassword(adminPassword)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		// Insertion de l'utilisateur dans la base de données
		_, err = db.Exec("INSERT INTO utilisateurs (username, password, email, est_admin) VALUES (?, ?, ?, 1)", adminUsername, hashedPassword, email)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		// Redirection vers la page de connexion
		http.Redirect(w, r, "/login", http.StatusFound)
		return
	}

	// Affichage du formulaire pour l'ajout d'utilisateur
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

func HandleRegister(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer db.Close()

	if r.Method == "POST" {
		username := r.FormValue("username")
		password := r.FormValue("password")
		email := r.FormValue("email")

		if !ValidatePassword(password) {
			http.Error(w, "Le mot de passe ne respecte pas les conditions requises", http.StatusBadRequest)
			return
		}

		exists, err := checkUsernameExists(db, username)
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

		emailExists, err := checkEmailExists(db, email)
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

		hashedPassword, err := hashPassword(password)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		token, err := generateToken()
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = db.Exec("INSERT INTO utilisateurs (username, password, email, reset_token) VALUES (?, ?, ?, ?)", username, hashedPassword, email, token)
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
func checkAdminCredentials(username, password string) bool {
	db, err := sql.Open("sqlite3", "database.sqlite")
	if err != nil {
		// Gestion de l'erreur de connexion à la base de données
		return false
	}
	defer db.Close()

	var storedPassword string
	err = db.QueryRow("SELECT password FROM admin_users WHERE username = ?", username).Scan(&storedPassword)
	if err != nil {
		// Gestion de l'erreur lors de la recherche de l'utilisateur dans la base de données
		return false
	}

	// Comparaison du mot de passe fourni avec celui stocké en base de données
	err = bcrypt.CompareHashAndPassword([]byte(storedPassword), []byte(password))
	if err != nil {
		// Le mot de passe ne correspond pas ou une erreur s'est produite lors de la comparaison
		return false
	}

	// Les identifiants sont valides
	return true
}
