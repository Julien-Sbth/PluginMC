# PluginMC

A Minecraft plugin designed to enhance your gaming experience with its unique features.

## Usage

For this program to work you need to execute the code in the branch API_Minecraft & API_Reception.

## API Communication Overview

In the Minecraft API, there exists a function responsible for sending all data to a designated URL. Subsequently, the API Receiver intercepts this URL to receive the transmitted data. Upon reception, the receiver identifies the type of data being received. These data types include:

- **Inventory Images**: Images representing the player's inventory.
- **Achievements**: Information regarding achievements attained.
- **Blocks Traveled**: Data indicating the distance traveled across different blocks.
- **Monster Kills**: Statistics regarding monsters killed.
- **Blocks Destroyed**: Information on blocks destroyed by the player.
- **Coins Earned**: Currency earned within the game.

This communication process facilitates the seamless transfer of data from the Minecraft API to the API Receiver, allowing for efficient data analysis and visualization.

## Integrating Spigot into Your Project:

1. Download the plugin from [Spigot](https://getbukkit.org/download/spigot).
2. Create a new project in IntelliJ IDEA.
3. Select Gradle as the build system for seamless integration.
4. Create a folder named "server" within your project directory.
5. Copy the Spigot server files into the "server" folder.
6. Open IntelliJ IDEA and navigate to Files -> Project Structure.
7. Under Project Settings, select Modules.
8. Click the "+" icon and choose Minecraft from the dropdown menu.

## Launching Spigot:

1. Navigate to "Current Files" in IntelliJ IDEA.
2. Select "Edit Configuration".
3. Click "Add New" and search for "Jar Application".
4. Specify the path to the Spigot JAR file, typically located in the "server" folder.
5. Write "-nogui" in the Program Arguments field.
6. Specify the working directory by providing the path to the "server" folder.
7. If you launch the program for the first time the program will exit, a file named eula.txt will be create you need to modify this file to replace eula = false to eula = true

   *I recommend using Corretto-17 for optimal performance.*

## API

### API Features

Explore the powerful features offered by the plugin's API, including a wide range of methods for customization and integration. Unlock new possibilities and extend the functionality of your Minecraft server.

### Usage Example:

```java
package fr.api.pluginmc;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("Plugin MC has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin MC has been disabled!");
    }
}

```
Execute this code and if no errors occur, your plugin is ready to go!
