
#!/bin/bash

echo "=== Packing BoxPvP Server ==="

# Create BoxPvP folder
rm -rf BoxPvP
mkdir -p BoxPvP

# Copy essential server files
echo "Copying server files..."
cp paper.jar BoxPvP/ 2>/dev/null || echo "Warning: paper.jar not found"
cp eula.txt BoxPvP/ 2>/dev/null
cp server.properties BoxPvP/ 2>/dev/null
cp bukkit.yml BoxPvP/ 2>/dev/null
cp spigot.yml BoxPvP/ 2>/dev/null
cp commands.yml BoxPvP/ 2>/dev/null
cp help.yml BoxPvP/ 2>/dev/null
cp permissions.yml BoxPvP/ 2>/dev/null

# Copy JSON files
cp banned-ips.json BoxPvP/ 2>/dev/null
cp banned-players.json BoxPvP/ 2>/dev/null
cp ops.json BoxPvP/ 2>/dev/null
cp usercache.json BoxPvP/ 2>/dev/null
cp whitelist.json BoxPvP/ 2>/dev/null

# Copy directories
echo "Copying directories..."
cp -r plugins BoxPvP/ 2>/dev/null || echo "Warning: plugins folder not found"
cp -r libraries BoxPvP/ 2>/dev/null || echo "Warning: libraries folder not found"
cp -r config BoxPvP/ 2>/dev/null || echo "Warning: config folder not found"
cp -r versions BoxPvP/ 2>/dev/null || echo "Warning: versions folder not found"

# Copy world folders
cp -r world BoxPvP/ 2>/dev/null || echo "Warning: world folder not found"
cp -r world_nether BoxPvP/ 2>/dev/null || echo "Warning: world_nether folder not found"
cp -r world_the_end BoxPvP/ 2>/dev/null || echo "Warning: world_the_end folder not found"

# Create start script for packed server
cat > BoxPvP/start.sh << 'EOF'
#!/bin/bash
echo "=== Starting BoxPvP Server ==="
java -Xmx512M -Xms256M -jar paper.jar nogui
EOF

chmod +x BoxPvP/start.sh

echo "================================"
echo "Server packed successfully!"
echo "Folder: BoxPvP/"
echo "To start server: cd BoxPvP && ./start.sh"
echo "================================"
