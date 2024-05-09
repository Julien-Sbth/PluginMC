import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Plugin activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin désactivé !");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("executego")) {
            if (args.length == 1) {
                String codeGo = args[0]; // Le code Go envoyé en argument
                executeGoCode(codeGo);
                return true;
            } else {
                sender.sendMessage("Usage: /executego <code Go>");
                return false;
            }
        }
        return false;
    }

    public void executeGoCode(String codeGo) {
        try {
            // URL de votre serveur Go
            URL url = new URL("http://localhost:8080/executego?code=" + codeGo); // Modifiez l'URL en fonction de votre serveur Go

            // Créez une connexion HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Lisez la réponse
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Affichez la réponse
            getLogger().info("Réponse du serveur Go : " + response.toString());

        } catch (Exception e) {
            getLogger().warning("Erreur lors de l'exécution du code Go : " + e.getMessage());
        }
    }
}
