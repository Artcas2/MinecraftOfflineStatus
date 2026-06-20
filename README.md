# MinecraftOfflineStatus

A standalone application that displays a status when your server goes offline (1.7.2+).

<img width="800" height="101" alt="Example" src="https://github.com/user-attachments/assets/db145111-1541-4af8-b782-2aef0a70b4c7" />

## Documentation

Command-line usage: java -jar path/to/file.jar \<port\> \<status\> \<message\>

Options:
  - **\<port\>** : The port number of your Minecraft server
  - **\<status\>** : The status to display next to the ping icon (replaces the player count)
  - **\<message\>** : The motd and the message to display when trying to connect

To automatically detect when your server starts and stops, you must add this argument to your Minecraft server startup command: `-Dapp.name=MinecraftServer`

Example: `java -Xms2048M -Xmx2048M -Dapp.name=MinecraftServer -jar spigot.jar --nogui`

Press Ctrl+C to stop.
