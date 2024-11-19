MERGE INTO users (username, password, enabled)
KEY(username)
VALUES ('admin', '$2a$10$Zk1h9A7NuN0TnNfUc.YOEOG09odFviO9JPoZOHUKJvSgmlmFZsBm6', TRUE);

INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_ADMIN');