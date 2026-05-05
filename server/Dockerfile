FROM node:20-alpine

WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install

COPY . .
RUN mkdir -p /app/server/data

EXPOSE 3000
CMD ["npm", "start"]
