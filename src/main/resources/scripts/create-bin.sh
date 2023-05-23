# Append a basic launcher script to the jar

echo '#!/bin/sh' > x
echo 'exec java -jar $0 $@' >> x
echo 'echo "exit 0"' >> x

cat x $1 > jacamo

# Make the new jar executable
chmod +x jacamo

rm x
