#!/bin/bash

directory="$1"
total_lines=$(find "$directory" -type f -o -name "*.java" | xargs wc -l | tail -n 1 | awk '{print $1}')
echo "Liczba linii kodu w folderze '$directory': $total_lines"