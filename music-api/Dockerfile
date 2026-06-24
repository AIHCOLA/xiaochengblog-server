FROM node:20-alpine

WORKDIR /app/music-api

# Install parent deps (NeteaseCloudMusicApi needed by multi-platform)
COPY package*.json ./
RUN npm install

# Install child deps
COPY multi-platform/package*.json ./multi-platform/
RUN cd multi-platform && npm install

# Copy source
COPY . .

WORKDIR /app/music-api/multi-platform
EXPOSE 3100
CMD ["node", "app.js"]
