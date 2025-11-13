
#!/bin/bash

echo "🔄 Auto-backup started at $(date)"
echo "⏰ Will backup every 5 minutes when there are changes"
echo "⚠️  Keep this tab open (can minimize but don't close)"
echo "---"

while true; do
    sleep 300  # 5 phút
    
    # Kiểm tra xem có thay đổi không
    if [[ -n $(git status -s) ]]; then
        echo "📝 Changes detected at $(date)"
        git add .
        git commit -m "Auto-backup: $(date '+%Y-%m-%d %H:%M:%S')"
        
        if git push -u origin main; then
            echo "✅ Successfully backed up at $(date)"
        else
            echo "❌ Failed to push at $(date)"
        fi
    else
        echo "⏭️  No changes to backup at $(date)"
    fi
done
