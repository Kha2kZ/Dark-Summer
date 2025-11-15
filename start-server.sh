
#!/bin/bash

# Download Paper if not exists
if [ ! -f "paper.jar" ]; then
    echo "Downloading Paper 1.20.1..."
    wget -O paper.jar https://api.papermc.io/v2/projects/paper/versions/1.20.1/builds/196/downloads/paper-1.20.1-196.jar
fi

# Create plugins folder
mkdir -p plugins

# Copy built plugin to plugins folder
if [ -f "target/BoxPvPCore-1.0.0.jar" ]; then
    echo "Copying plugin to plugins folder..."
    cp target/BoxPvPCore-1.0.0.jar plugins/
fi

# Accept EULA
echo "eula=true" > eula.txt

# Create server.properties
cat > server.properties << EOF
online-mode=false
server-port=25565
motd=§8§m━━━§r §b§l◆ §3§lOCEAN BOXPVP §b§l◆ §8§m━━━\n     §b§l≫ §3KHAI TRƯƠNG SERVER ĐẦU HÈ §b§l≪
max-players=20
EOF

# Start server (reduced RAM for Replit free tier)
java -Xmx512M -Xms256M -jar paper.jar nogui
