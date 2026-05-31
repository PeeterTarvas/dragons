FROM node:22-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build \
    && cp dist/dragons-ui/browser/index.csr.html dist/dragons-ui/browser/index.html 2>/dev/null || true

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/dragons-ui/browser /usr/share/nginx/html
EXPOSE 80
