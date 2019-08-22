INSERT INTO users(username, enabled, password) VALUES ('admin', true, '{bcrypt}$2a$10$MfijVmidq73sKdVNthcchObA2WUg5tlkralABIqoLVx359AOcqda2');

INSERT INTO authorities(username, authority) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO authorities(username, authority) VALUES ('admin', 'ROLE_USER');