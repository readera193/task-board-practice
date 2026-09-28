# Task Board 前端

## 本機開發

在 `frontend` 目錄執行：

```sh
npm ci
npm run dev
```

開啟 `http://localhost:5173/`。本機開發時，前端預設連到 `http://localhost:8080`；可在本機 `.env.local` 設定 `VITE_API_BASE_URL` 覆蓋。

## 部署到 Northflank

這個儲存庫的 GitHub Actions 會在推送分支時，使用根目錄作為 Docker build context 與 `backend/Dockerfile` 建置映像，並部署到現有的 Northflank 後端服務。Dockerfile 會先建置 Vite 前端，再將網頁檔案放入 Spring Boot 的 `static` 目錄。

正式環境的首頁是現有 Northflank 服務的 `/`，API 位於同一網域的 `/api`，不需額外建立前端服務或設定跨來源網域。部署完成後可直接開啟後端的公開 HTTPS 網址。若要將前端改為獨立網域，可用 `VITE_API_BASE_URL` 指向後端，並在後端設定 `APP_CORS_ALLOWED_ORIGINS`。
