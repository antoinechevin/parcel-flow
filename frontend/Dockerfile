# Stage 1: Build the Expo Web app
FROM node:20-alpine AS builder

WORKDIR /app

# Install dependencies
COPY package.json package-lock.json ./
RUN npm ci

# Copy source code
COPY . .

# Build for web
# npx expo export -p web builds the static files to dist/
RUN npx expo export -p web

# Stage 2: Serve the static files
FROM nginx:alpine

# Copy the built files from the builder stage to Nginx html directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
