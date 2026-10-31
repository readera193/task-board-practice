-- Demo 體驗帳號：以 demo 旗標辨識（不靠帳號前綴，避免一般使用者自取 demo- 開頭的名稱被清除），
-- created_at 供排程判斷過期。既有使用者的 created_at 以遷移當下時間為準，且 demo = FALSE，不會被清除。
ALTER TABLE users
    ADD COLUMN demo BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
