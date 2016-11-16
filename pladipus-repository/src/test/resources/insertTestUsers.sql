use pladipus2;
-- Password1
INSERT INTO users (user_id, user_name, email, password, active) 
VALUES (1, 'test_user1', 'test@user.one', 'e1STQoRCV9yok7U+mHMVo3oZTHo51VpL', true);
INSERT INTO user_roles(user_id, role_id)
VALUES (1, 1);
-- Password2
INSERT INTO users (user_id, user_name, email, password, active)
VALUES (2, 'test_user2', 'test@user.two', '/5UFZQgurz0Z5pBxlCj/jGIXWHsFka4O', false);
INSERT INTO user_roles(user_id, role_id)
VALUES (2, 2);