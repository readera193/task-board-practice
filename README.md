# Task Board

一個以 **Spring Boot + React** 打造的全端任務管理（Todo/Task Board）練習專案，目標是模擬真實團隊會遇到的後端分層架構、身分驗證、資料庫遷移與測試策略，而不只是做出一個能動的 CRUD。

這份 README 同時作為練習紀錄，整理這個專案實際碰過、練過的技術點。

**線上 Demo**：https://p01--task-board-backend--vnvfqjnzxl2s.code.run/

---

## 技術棧

**Backend**
- Java 17 + Spring Boot 4.1
- Spring Security + OAuth2 Resource Server（JWT 簽發 / 驗證）
- Spring Data JPA + MySQL 8
- Flyway 資料庫版本遷移
- Maven

**Frontend**
- React 19 + TypeScript
- Vite

**Infra / DevOps**
- Docker 多階段建置（前端 build 產物直接由後端 Spring Boot 靜態資源伺服）
- Docker Compose（MySQL + Backend）
- GitHub Actions CI/CD：自動跑測試 → build Docker image → push 到 GHCR → 部署到 Northflank
- 部署於 Northflank

**Testing**
- JUnit 5 + Mockito（Unit test）
- `@DataJpaTest`（JPA slice test，H2）
- `@WebMvcTest`（Web slice test）
- `@SpringBootTest`（Full integration test）
- Testcontainers（MySQL 真實資料庫整合測試）

---

## 練習重點（給面試官看的部分）

### 1. 後端分層架構（Layered / Clean-ish Architecture）
專案依職責切成四層，強制單向依賴，避免 Controller 直接操作 Entity 或 Repository 外洩到外部：

```
presentation/    → Controller、Request/Response DTO、Exception Handler
application/     → Service（商業邏輯）、Result DTO、Repository 介面
domain/          → Entity（純領域模型）
infrastructure/  → Repository 實作（JPA）、Security（JWT）、持久化細節
shared/          → 跨層共用的例外型別
```

練習點：DTO 與 Entity 分離、Repository 介面（`application`）與實作（`infrastructure`）分離、統一的例外處理（`GlobalExceptionHandler` + `BusinessException` 階層）。

### 2. JWT 身分驗證
- 使用 Spring Security `OAuth2 Resource Server` 搭配自簽 HMAC（`NimbusJwtEncoder` / `NimbusJwtDecoder`）簽發與驗證 JWT，而非引入額外第三方套件手刻。
- `DaoAuthenticationProvider` + `UserDetailsService` 處理帳密登入、`BCrypt` 密碼雜湊。
- Stateless session（`SessionCreationPolicy.STATELESS`），搭配自訂 `AuthenticationEntryPoint` / `AccessDeniedHandler` 統一回傳 JSON 格式的 401 / 403。
- 練習了以 `scope` claim 做簡單的角色權限控管（`SCOPE_ROLE_ADMIN`）。

### 3. 資料庫遷移（Flyway）
`db/migration` 下以 `V1` ~ `V4` 記錄了表結構隨需求演進的過程（建表 → 加欄位 → 加關聯 → 補 NOT NULL 約束），練習用遷移腳本而非手動改 schema 的方式管理資料庫版本。

### 4. 測試策略分層
沒有把所有測試都寫成又重又慢的 `@SpringBootTest`，而是依測試目的分四層，各層只驗證自己最有價值的範圍（詳見 [`backend/TESTING.md`](backend/TESTING.md)）：

| 層級 | 測試類別 | 驗證重點 |
| --- | --- | --- |
| Unit | `TaskServiceTest` | Service 商業邏輯、例外處理（Mockito，不啟 Spring） |
| JPA slice | `JpaTaskRepositoryTest` | Entity mapping、owner scope、分頁排序 query |
| Web slice | `TaskControllerTest` | HTTP status、JSON、validation、Pageable binding |
| Full integration | `TaskApiIntegrationTest` | 註冊 → 登入 → 真 JWT → 建立任務 → 查詢，以及未登入 401 |
| Production DB | `MySqlRepositoryIntegrationTest` | Testcontainers 起真 MySQL，驗證 Flyway + Hibernate validate |

### 5. API 設計
- RESTful 資源設計（`/api/tasks`、`/api/auth`、`/api/users`）
- 分頁查詢（`Pageable`）並白名單限制可排序欄位，避免任意欄位排序造成的資安/效能疑慮
- Bean Validation（`@Valid`）+ 統一錯誤回應格式
- Task 依登入使用者做資料隔離（owner scope），確保不同使用者互相看不到彼此的任務

### 6. 前後端整合與部署
- Docker 多階段建置：第一階段建置 React 前端、第二階段將產物複製進 Spring Boot 的 `static` 資源目錄，最終只產出一個可獨立運行的 Docker image。
- 練習了將前端與後端整合成單一部署單元，而非分開兩個服務。

### 7. CI/CD（GitHub Actions → Northflank）
完整流程定義在 [`.github/workflows/ci.yml`](.github/workflows/ci.yml)：

1. **Test**：每次 push / PR 都會跑 `./mvnw clean verify`（含上述四層測試）。
2. **Build & Push**：測試通過且是 push 事件時，才用 Docker Buildx 建置 image，並以 `latest` 與 `sha-<commit>` 兩種 tag push 到 GitHub Container Registry（GHCR），同時啟用 `cache-from`/`cache-to` 加速 build。
3. **Deploy**：以剛 build 出的 `sha-<commit>` image 呼叫 `northflank/deploy-to-northflank` action，自動部署到 Northflank 上的正式環境。

練習點：測試沒過就不會 build/deploy（quality gate）、用 commit SHA 而非 `latest` 做部署以確保每次部署版本可追溯、機敏資訊（Northflank API Key 等）以 GitHub Secrets / Variables 管理不落地在程式碼中。

---

## 功能

- 使用者註冊 / 登入（JWT）
- 任務 CRUD（新增、查詢、編輯、刪除）
- 任務完成狀態切換（toggle）
- 任務分頁與排序查詢
- 任務描述欄位（選填）

---

## API 一覽

| Method | Endpoint | 說明 | 需要登入 |
| --- | --- | --- | --- |
| POST | `/api/users/register` | 註冊帳號 | 否 |
| POST | `/api/auth/login` | 登入取得 JWT | 否 |
| GET | `/api/auth/me` | 取得目前登入者資訊 | 是 |
| GET | `/api/tasks` | 分頁查詢任務（支援 `sort`） | 是 |
| POST | `/api/tasks` | 建立任務 | 是 |
| GET | `/api/tasks/{id}` | 查詢單一任務 | 是 |
| PUT | `/api/tasks/{id}` | 更新任務 | 是 |
| PATCH | `/api/tasks/{id}/toggle` | 切換完成狀態 | 是 |
| DELETE | `/api/tasks/{id}` | 刪除任務 | 是 |

---

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
        ├── api/              # API 呼叫
        ├── services/         # Token 儲存等前端服務
        └── utils/            # 錯誤處理等工具
```

---

## 本機執行

### 使用 Docker Compose（推薦，含 MySQL）

```powershell
cd backend
docker compose up --build
```

需先在 `backend/.env` 設定 `DB_PASSWORD`、`JWT_SECRET`（此檔已被 `.gitignore` 排除，不會進版控）。

### 分開執行前後端（開發模式）

Backend：

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Frontend：

```powershell
cd frontend
npm install
npm run dev
```

### 執行測試

```powershell
cd backend
.\mvnw.cmd test
```

（若本機有 Docker，`MySqlRepositoryIntegrationTest` 會自動用 Testcontainers 啟動 MySQL；沒有 Docker 則自動跳過該測試。）
