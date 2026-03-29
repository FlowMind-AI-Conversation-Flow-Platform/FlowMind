CREATE TABLE IF NOT EXISTS bootstrap_marker (
    marker_key VARCHAR(64) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO bootstrap_marker (marker_key, description)
VALUES ('phase-1-bootstrap', 'Initial FlowMind backend bootstrap baseline')
ON CONFLICT (marker_key) DO NOTHING;

