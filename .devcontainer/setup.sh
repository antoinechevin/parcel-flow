#!/bin/bash

# Installer Gemini CLI
npm install -g @google/gemini-cli

# Fix permissions for persisted Gemini config
sudo mkdir -p /home/vscode/.gemini
sudo chown -R vscode:vscode /home/vscode/.gemini

# Install Git LFS
sudo apt-get update && sudo apt-get install -y git-lfs
git lfs install

# Installation des dépendances Backend (sans lancer les tests pour aller vite)
echo "Installing Backend dependencies..."
mvn -f backend/pom.xml install -DskipTests

# Installation des dépendances Frontend
echo "Installing Frontend dependencies..."
cd frontend && npm install && cd ..

# Ajoutez vos futures commandes ici
# ex: npm install -g autre-outil
