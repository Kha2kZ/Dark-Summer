
#!/bin/bash

echo "=== BoxPvP Server Startup ==="

# Change to BoxPvP directory
cd BoxPvP || {
    echo "ERROR: BoxPvP folder not found!"
    exit 1
}

# Download Paper if not exists
if [ ! -f "paper.jar" ]; then
    echo "Downloading Paper 1.20.1..."
    wget -q --show-progress -O paper.jar https://api.papermc.io/v2/projects/paper/versions/1.20.1/builds/196/downloads/paper-1.20.1-196.jar
    
    if [ ! -f "paper.jar" ]; then
        echo "ERROR: Failed to download Paper!"
        exit 1
    fi
    echo "Paper downloaded successfully!"
else
    echo "Paper already exists, skipping download."
fi

# Create plugins folder
mkdir -p plugins

# Copy built plugin from parent directory
if [ -f "../target/BoxPvPCore-1.0.0.jar" ]; then
    echo "Copying BoxPvPCore plugin to plugins folder..."
    cp ../target/BoxPvPCore-1.0.0.jar plugins/
    echo "Plugin copied successfully!"
else
    echo "WARNING: BoxPvPCore plugin not found in target folder!"
fi

# Accept EULA
echo "eula=true" > eula.txt

# Create server.properties
cat > server.properties << 'EOF'
online-mode=false
server-port=25565
motd=§8§m━━━§r §b§l◆ §3§lOCEAN BOXPVP §b§l◆ §8§m━━━\n     §b§l≫ §3KHAI TRƯƠNG SERVER ĐẦU HÈ §b§l≪
max-players=20
server-ip=0.0.0.0
EOF

echo "Starting Minecraft server from BoxPvP directory..."
echo "RAM: 512MB (Replit Free Tier)"
echo "Connect using: <repl-url>:25565"
echo "================================"

# Start server (reduced RAM for Replit free tier)
java -Xmx512M -Xms256M -jar paper.jar nogui
