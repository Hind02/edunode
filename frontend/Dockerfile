FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

FROM nginx:alpine
# Copie des fichiers buildés vers Nginx
# Attention: Avec Angular 17/18, le build se trouve dans dist/frontend/browser
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
# Configuration de base pour le routing Angular
RUN echo "server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files \$uri \$uri/ /index.html; \
    } \
}" > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
