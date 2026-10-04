FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

FROM nginx:alpine
# Mise a jour des paquets systeme (correctifs de securite signales par Trivy)
RUN apk upgrade --no-cache
# Configuration nginx : sert Angular et relaie /api vers le backend
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Fichiers Angular compiles (Angular 17/18 : dist/frontend/browser)
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]