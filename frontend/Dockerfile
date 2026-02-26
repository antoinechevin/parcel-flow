# Stage 1: Build the Expo Web app
FROM node:20-alpine AS builder

WORKDIR /app

# Install dependencies
COPY package.json package-lock.json ./
RUN npm ci

# Copy source code
COPY . .

# Accept the API URL as a build argument
ARG EXPO_PUBLIC_API_URL
# Make it available to the build command
ENV EXPO_PUBLIC_API_URL=${EXPO_PUBLIC_API_URL}

# Build for web
# npx expo export -p web builds the static files to dist/
RUN npx expo export -p web

# Stage 2: Serve the static files
FROM nginx:alpine

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy the built files from the builder stage to Nginx html directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Healthcheck to ensure the web server is responding
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:80/ || exit 1

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
