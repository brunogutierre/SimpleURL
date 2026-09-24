CREATE TABLE short_link (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code       VARCHAR(16)              NOT NULL,
    target_url VARCHAR(2048)            NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_short_link_code UNIQUE (code)
);
