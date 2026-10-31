# 本機執行、專案結構與 CI/CD

## 本機執行

### 使用 Docker Compose（推薦，含 MySQL）

```powershell
cd backend
docker compose up --build
```

需先在 `backend/.env` 設定 `DB_PASSWORD`、`JWT_SECRET`（此檔已被 `.gitignore` 排除，不會進版控）。

### 分開執行前後端（開發模式）

```powershell
# Backend
cd backend
.\mvnw.cmd spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

### 執行測試

```powershell
cd backend
.\mvnw.cmd test
```

若本機有 Docker，`MySqlRepositoryIntegrationTest` 會自動用 Testcontainers 啟動 MySQL；沒有 Docker 則自動跳過該測試。

## 專案結構

```
task-board-practice/
├── backend/                 # Spring Boot 後端
│   ├── src/main/java/com/example/taskboard/
│   │   ├── domain/          # Entity
│   │   ├── application/     # Service、Repository 介面、Result DTO
│   │   ├── infrastructure/  # Repository 實作、Security、JWT
│   │   ├── presentation/    # Controller、Request/Response DTO
│   │   └── shared/          # 共用例外
│   ├── src/main/resources/db/migration/  # Flyway 遷移腳本
│   ├── src/test/            # Unit / Slice / Integration 測試
│   └── TESTING.md           # 測試策略說明
└── frontend/                # React + TypeScript 前端
    └── src/
        ├── api/             # API 呼叫
        ├── components/      # AuthPage、TaskBoard、TaskForm、TaskItem
        ├── hooks/           # useAuth、useTasks（狀態與副作用）
        ├── services/        # Token 儲存等前端服務
        └── utils/           # 錯誤處理等工具
```

## CI/CD（GitHub Actions → Northflank）

流程定義在 [`.github/workflows/ci.yml`](../.github/workflows/ci.yml)：

1. **Test**：任何分支 push / PR 都會跑 `./mvnw clean verify`（含 spotless 格式檢查與四層測試）。
2. **Build & Push**：測試通過且是 `main` 分支的 push 時，才用 Docker Buildx 建置 image，並以 `latest` 與 `sha-<commit>` 兩種 tag push 到 GHCR（啟用 `cache-from` / `cache-to` 加速）。
3. **Deploy**：以剛 build 出的 `sha-<commit>` image 呼叫 `northflank/deploy-to-northflank` action 部署到正式環境。

Northflank API Key 等機敏資訊以 GitHub Secrets / Variables 管理。

## 已知限制與取捨

- **分頁型別沿用 Spring Data**：`TaskRepository`（application 層的 port）直接使用 `Page` / `Pageable`，不另建一套分頁型別；真要換掉持久化技術時再抽離。
- **Token 存在 `localStorage`**：容易受 XSS 影響，但改用 HttpOnly Cookie 會引入 CSRF 防護，對這個規模屬過度設計。
- **登入與註冊限流**：單一實例每分鐘每來源最多登入 20 次、註冊 10 次，合計全站最多 300 次，包含失敗嘗試；超量回應 429 與 Retry-After。可透過 AUTH_LOGIN_PER_MINUTE、AUTH_REGISTER_PER_MINUTE、AUTH_TOTAL_PER_MINUTE 調整。計數於重啟後重置，多實例應由入口平台統一限流。不直接採信 X-Forwarded-For；反向代理後可能共用來源額度，需依平台可信代理設定調整。
- **Demo 體驗登入**：`POST /api/auth/demo` 建立隨機 `demo-xxxxxxxx` 帳號（`users.demo = true`）與 3 筆範例任務並直接回傳 JWT，密碼為不對外提供的隨機值。預設關閉，以環境變數 `DEMO_LOGIN_ENABLED=true` 啟用（dev profile 預設開啟）；超過 `app.demo-login.retention-hours`（預設 24 小時）的 Demo 帳號與其任務由每小時一次的排程清除，一般註冊的使用者不受影響。與註冊共用建立帳號的限流額度。
- **角色（Role）**：目前只有 `USER` 一種角色，沒有任何端點依角色授權；保留是為日後擴充預留（角色會以 `ROLE_*` 寫入 JWT 的 `scope`），移除需連帶調整 `users` 表欄位與 token 內容，成本高於保留。
- **程式碼格式**：後端以 spotless（google-java-format AOSP 風格）檢查，`./mvnw spotless:apply` 可自動格式化。前端以 Prettier 統一格式（`npm run format`），CI 以 `npm run format:check` 檢查。
