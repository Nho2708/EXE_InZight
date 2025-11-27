
-- USERS
INSERT INTO users (username, email, password, full_name, avatar_url) VALUES
                                                                         ('alice', 'alice@example.com', '2a$10$7qTjZVr0t4A0fHw6/NlOUO4SgJHmbvC5FbyhpzVdT5a5eTzq7UwqC', 'Alice Nguyen', 'https://i.pravatar.cc/150?img=1'),
                                                                         ('bob',   'bob@example.com',   '2a$10$7qTjZVr0t4A0fHw6/NlOUO4SgJHmbvC5FbyhpzVdT5a5eTzq7UwqC', 'Bob Tran',    'https://i.pravatar.cc/150?img=2'),
                                                                         ('charlie','charlie@example.com','2a$10$7qTjZVr0t4A0fHw6/NlOUO4SgJHmbvC5FbyhpzVdT5a5eTzq7UwqC','Charlie Pham','https://i.pravatar.cc/150?img=3');

-- CATEGORIES
INSERT INTO categories (name, type) VALUES
                                        ('Ăn uống', 'EXPENSE'),
                                        ('Đi lại', 'EXPENSE'),
                                        ('Mua sắm', 'EXPENSE'),
                                        ('Giải trí', 'EXPENSE'),
                                        ('Lương', 'INCOME'),
                                        ('Thưởng', 'INCOME');

-- WALLETS
INSERT INTO wallets (user_id, name, balance, currency) VALUES
                                                           (1, 'Ví chính', 5000000, 'VND'),
                                                           (2, 'Ví tiết kiệm', 2000000, 'VND'),
                                                           (3, 'Ví đô la', 300, 'USD');

-- TRANSACTIONS
INSERT INTO transactions (wallet_id, category_id, amount, type, note) VALUES
                                                                          (1, 1, 120000, 'EXPENSE', 'Ăn sáng phở bò'),
                                                                          (1, 2, 50000,  'EXPENSE', 'Đi xe buýt'),
                                                                          (1, 5, 10000000, 'INCOME', 'Lương tháng 9'),
                                                                          (2, 3, 300000, 'EXPENSE', 'Mua sách'),
                                                                          (3, 6, 50, 'INCOME', 'Tiền thưởng dự án');

-- BUDGETS (có thêm budget_name)
INSERT INTO budgets (user_id, category_id, budget_name, amount_limit, start_date, end_date) VALUES
                                                                                                (1, 1, 'Ăn uống tháng 9', 2000000, '2025-09-01', '2025-09-30'),
                                                                                                (1, 2, 'Đi lại tháng 9',  1000000, '2025-09-01', '2025-09-30');

-- FRIENDS
INSERT INTO friends (user_id, friend_id, status) VALUES
                                                     (1, 2, 'ACCEPTED'),
                                                     (1, 3, 'PENDING'),
                                                     (2, 3, 'ACCEPTED');

-- POSTS
INSERT INTO posts (user_id, content, image_url) VALUES
                                                    (1, 'Hôm nay ăn phở ngon quá!', 'https://picsum.photos/200'),
                                                    (2, 'Đi du lịch Đà Lạt, cảnh đẹp quá!', 'https://picsum.photos/201'),
                                                    (3, 'Vừa mua sách mới để học Spring Boot.', NULL);

-- COMMENTS
INSERT INTO comments (post_id, user_id, content) VALUES
                                                     (1, 2, 'Phở ngon thật, ở đâu vậy bạn?'),
                                                     (2, 1, 'Đà Lạt mùa này mát không?'),
                                                     (3, 2, 'Spring Boot học hay lắm!');

-- LIKES
INSERT INTO likes (post_id, user_id) VALUES
                                         (1, 2),
                                         (1, 3),
                                         (2, 1);

-- SHARES
INSERT INTO shares (post_id, user_id) VALUES
                                          (1, 3),
                                          (2, 1);

-- CHAT MESSAGES
INSERT INTO chat_messages (sender_id, receiver_id, content) VALUES
                                                                (1, 2, 'Hello Bob, hôm nay thế nào?'),
                                                                (2, 1, 'Hi Alice, mình ổn. Còn bạn?'),
                                                                (3, 1, 'Alice ơi, giúp mình vụ Spring Boot với!');

INSERT INTO categories (name, type, icon_url) VALUES
                                                  ('Food', 'EXPENSE', 'https://cdn.inzightapp.com/icons/ic_food.png'),
                                                  ('Groceries', 'EXPENSE', 'https://cdn.inzightapp.com/icons/ic_groceries.png'),
                                                  ('Shopping', 'EXPENSE', 'https://cdn.inzightapp.com/icons/ic_shopping.png'),
                                                  ('Entertainment', 'EXPENSE', 'https://cdn.inzightapp.com/icons/ic_entertainment.png'),
                                                  ('Transfer', 'EXPENSE', 'https://cdn.inzightapp.com/icons/ic_transfer.png'),
                                                  ('Salary', 'INCOME', 'https://cdn.inzightapp.com/icons/ic_salary.png'),
                                                  ('Bonus', 'INCOME', 'https://cdn.inzightapp.com/icons/ic_bonus.png');

ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

# ALTER TABLE categories
#     ADD COLUMN icon_url VARCHAR(255);
#
# Update transactions SET category_id = 13
# WHERE category_id = 6;
#
# DELETE FROM categories WHERE id = 6;
#
# USE InZightApp;
#
# ALTER TABLE comment_likes
#     DROP FOREIGN KEY FK3wa5u7bs1p1o9hmavtgdgk1go,
#     ADD CONSTRAINT FK3wa5u7bs1p1o9hmavtgdgk1go
#         FOREIGN KEY (comment_id)
#             REFERENCES comments(id)
#             ON DELETE CASCADE;
#
# SHOW CREATE TABLE comment_likes;
# ALTER TABLE comment_likes
#     DROP FOREIGN KEY FK3wa5u7bs1p1o9hmavtgdgk1go;
# ALTER TABLE comment_likes
#     ADD CONSTRAINT FK3wa5u7bs1p1o9hmavtgdgk1go
#         FOREIGN KEY (comment_id)
#             REFERENCES comments(id)
#             ON DELETE CASCADE;
#
# ALTER TABLE comment_likes
#     DROP FOREIGN KEY FK6h3lbneryl5pyb9ykaju7werx;
#
# ALTER TABLE comment_likes
#     ADD CONSTRAINT FK6h3lbneryl5pyb9ykaju7werx
#         FOREIGN KEY (user_id)
#             REFERENCES users(id)
#             ON DELETE CASCADE;