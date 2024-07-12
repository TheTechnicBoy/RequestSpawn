# Minecraft Entity Spawning Mod

This mod sets up an HTTP server to handle requests for spawning entities within the Minecraft game. When the Minecraft server starts, it initializes the HTTP server, which listens for POST requests containing JSON data that specifies which entity to spawn and where.

## JSON Request Format
### Spawning at Players
```json
{
  "mob": "minecraft:phantom",
  "player": "TheTechnicBoy",
  "nbt": {"NoAI": true}
}
```
### Spawning at Coordinates
```json
{
  "mob": "minecraft:phantom",
  "x" : 30,
  "y" : 64,
  "z" : 100,
  "nbt": {"NoAI": true}
}
```
## API Details
- EndPoint: localhost:PORT/spawn
- Header: Content-Type: application/json
- Authentication: BasicAuth USER:PASSWORD
- Data: JSON as shown above
