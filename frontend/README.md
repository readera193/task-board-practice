# Task Board 前端

## 本機開發

在 `frontend` 目錄執行：

```sh
npm ci
npm run dev
```

開啟 `http://localhost:5173/`。前端預設連到 `http://localhost:8080`；可在本機 `.env.local` 設定 `VITE_API_BASE_URL` 覆蓋。

## 部署到 Northflank

1. 先確認後端服務有公開 HTTPS 網址，例如 `https://api.example.com`。
2. 將這個儲存庫的變更推送到 Northflank 使用的 Git 分支。在 Northflank 同一專案建立前端 **combined service**，來源選此儲存庫與該分支。
3. Build type 選 **Dockerfile**，Dockerfile path 設為 `/frontend/Dockerfile`，build context 設為 `/frontend`。
4. 在 **Build arguments** 設定 `VITE_API_BASE_URL` 為後端公開 HTTPS 網址，結尾不要加 `/`。這是建置時的設定；變更網址後須重新建置前端。
5. 將前端服務的 `8080` port 設為 **Public HTTP**。Northflank 會提供 HTTPS 網址，該網址的 `/` 就是前端首頁。
6. 在**後端**服務的 runtime environment 設定 `APP_CORS_ALLOWED_ORIGINS` 為前端 HTTPS origin，例如 `https://app.example.com`（不含尾端 `/`），並重新部署後端。若要同時允許本機前端，用逗號分隔：`http://localhost:5173,https://app.example.com`。

前端與後端是兩個服務。前端容器以 Nginx 提供建置後的網頁；API 請求直接送到 `VITE_API_BASE_URL`。請勿把 JWT、資料庫密碼等機密放入 `VITE_API_BASE_URL` 或其他 `VITE_` 建置變數。
