CREATE TABLE link_click (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    short_link_id BIGINT                   NOT NULL,
    clicked_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    referer       VARCHAR(2048)            NULL,
    user_agent    VARCHAR(512)             NULL,
    CONSTRAINT fk_link_click_short_link FOREIGN KEY (short_link_id)
        REFERENCES short_link (id) ON DELETE CASCADE
);

-- Serves per-link statistics: filter by link, aggregate/order by time.
CREATE INDEX ix_link_click_short_link_clicked_at ON link_click (short_link_id, clicked_at);
