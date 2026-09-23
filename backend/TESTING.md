# Testing Strategy

目前測試分成四層，各自只驗證自己最有價值的範圍，避免同一件事在不同層重複測太多次。

| 層級 | 測試類別 | 主要目的 | DB / Spring |
| --- | --- | --- | --- |
| Unit | `TaskServiceTest` | Service 業務邏輯、例外與 dependency interaction | Mockito；不啟 Spring、不碰 DB |
| JPA slice | `JpaTaskRepositoryTest` | Entity mapping、owner scope、pagination / sorting query | `@DataJpaTest` + H2 |
| Web slice | `TaskControllerTest` | HTTP status、JSON、validation、Authentication username、Pageable binding | `@WebMvcTest`；Security filters 關閉；`TaskService` mock |
| Full integration | `TaskApiIntegrationTest` | Register → Login → 真 JWT → Create Task → Query Task，以及未登入 401 | `@SpringBootTest` + H2 |
| Production DB integration | `SqlServerRepositoryIntegrationTest` | Flyway + Hibernate validate + Repository query 在真 SQL Server 上可執行 | Testcontainers + SQL Server |

## 保留原則

- Service 的 success / failure business path 放在 Unit Test。
- Repository 只測自訂 query 與 owner / pagination 等資料存取語意，不重測 Spring Data 本身的每個 Page 行為。
- Controller 詳細測 HTTP、validation、binding 與 username 傳遞；不重新測 Service business logic，也不在 slice test 重測 Security filter chain。
- 真正的 JWT 驗證與 401 security boundary 放在 Full Integration Test。
- Full integration 只留少量高價值 happy path，確認整條 wiring 能運作。
- SQL Server Testcontainers 只留少量 production-database-specific 測試，確認 Flyway 與 SQL dialect；不複製整套 H2 測試。

## 執行

一般測試：

```powershell
.\mvnw.cmd test
```

若 Docker 可用，`SqlServerRepositoryIntegrationTest` 會啟動 SQL Server container；若 Docker 不可用，該測試會自動跳過。

只跑 Service Unit Test：

```powershell
.\mvnw.cmd -Dtest=TaskServiceTest test
```

只跑 Web slice：

```powershell
.\mvnw.cmd -Dtest=TaskControllerTest test
```

只跑 SQL Server Testcontainers：

```powershell
.\mvnw.cmd -Dtest=SqlServerRepositoryIntegrationTest test
```
