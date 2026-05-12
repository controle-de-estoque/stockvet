FROM node:22-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM node:22-alpine
WORKDIR /app

RUN npm install -g serve

COPY --from=build /app/dist ./dist

EXPOSE 80

CMD ["sh", "-c", "if [ -d /app/dist/frontend/browser ]; then exec serve -s /app/dist/frontend/browser -l 80; elif [ -d /app/dist/frontend ]; then exec serve -s /app/dist/frontend -l 80; else echo 'Built frontend assets not found' >&2; exit 1; fi"]
