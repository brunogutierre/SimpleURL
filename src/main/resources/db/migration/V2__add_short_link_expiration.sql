-- NULL means the link never expires.
ALTER TABLE short_link ADD COLUMN expires_at TIMESTAMP WITH TIME ZONE NULL;
