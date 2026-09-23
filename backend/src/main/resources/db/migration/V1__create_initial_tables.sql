CREATE TABLE tasks
(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    completed BIT NOT NULL,
    created_at DATETIME2 NOT NULL
);

CREATE TABLE users
(
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    username NVARCHAR(50) NOT NULL,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL,

    CONSTRAINT uq_users_username UNIQUE (username)
);