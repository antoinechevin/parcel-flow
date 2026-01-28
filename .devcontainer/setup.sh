#!/bin/bash
set -e

# Fix permissions for persisted Gemini config and gcloud config
sudo mkdir -p /home/vscode/.gemini /home/vscode/.npm /home/vscode/.m2 /home/vscode/.config/gcloud /home/vscode/.railway
sudo chown -R vscode:vscode /home/vscode/.gemini /home/vscode/.npm /home/vscode/.m2 /home/vscode/.config/gcloud /home/vscode/.railway

# Installer Gemini CLI
npm install -g @google/gemini-cli

# Installer Railway CLI
npm install -g @railway/cli

# Install Git LFS
sudo apt-get update && sudo apt-get install -y git-lfs
git lfs install --force

# Installation des dépendances Backend (sans lancer les tests pour aller vite)
echo "Installing Backend dependencies..."
mvn -f backend/pom.xml install -DskipTests

# Installation des dépendances Frontend
echo "Installing Frontend dependencies..."
cd frontend && npm install && cd ..

# Ajoutez vos futures commandes ici
# ex: npm install -g autre-outil
