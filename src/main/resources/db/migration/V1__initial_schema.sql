-- V1: Initial schema

-- users テーブル
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(200) NOT NULL,
    pair_id UUID,
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- pairs テーブル
CREATE TABLE pairs (
    id UUID PRIMARY KEY,
    join_code VARCHAR(255) NOT NULL UNIQUE,
    state VARCHAR(255) NOT NULL,
    user_id_1 UUID NOT NULL,
    user_id_2 UUID
);

-- condition_updates テーブル
CREATE TABLE condition_updates (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    main_condition VARCHAR(255) NOT NULL,
    sub_condition VARCHAR(255),
    note VARCHAR(200),
    created_at TIMESTAMP NOT NULL
);

-- tokens テーブル
CREATE TABLE tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(80) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- インデックス
CREATE INDEX idx_tokens_token ON tokens(token);
CREATE INDEX idx_tokens_user_id ON tokens(user_id);

