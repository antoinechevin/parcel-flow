#!/bin/bash

# Installer Gemini CLI
npm install -g @google/gemini-cli

# Fix permissions for persisted Gemini config
sudo mkdir -p /home/vscode/.gemini
sudo chown -R vscode:vscode /home/vscode/.gemini

# Ajoutez vos futures commandes ici
# ex: npm install -g autre-outil
