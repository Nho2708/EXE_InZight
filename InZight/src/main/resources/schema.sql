-- =====================================================
-- USERS
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     email VARCHAR(100) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     full_name VARCHAR(100),
                                     avatar_url VARCHAR(255),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- CATEGORIES (Income / Expense types)
-- =====================================================
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
                                          type ENUM('INCOME', 'EXPENSE') NOT NULL
);

-- =====================================================
-- WALLETS
-- =====================================================
CREATE TABLE IF NOT EXISTS wallets (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id BIGINT NOT NULL,
                                       name VARCHAR(100) NOT NULL,
                                       balance DECIMAL(15,2) DEFAULT 0,
                                       currency VARCHAR(10) DEFAULT 'VND',
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- TRANSACTIONS
-- =====================================================
CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            wallet_id BIGINT NOT NULL,
                                            category_id BIGINT NOT NULL,
                                            amount DECIMAL(15,2) NOT NULL,
                                            type ENUM('INCOME', 'EXPENSE') NOT NULL,
                                            note VARCHAR(255),
                                            transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
                                            FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- =====================================================
-- BUDGETS
-- =====================================================
CREATE TABLE IF NOT EXISTS budgets (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id BIGINT NOT NULL,
                                       category_id BIGINT NOT NULL,
                                       amount_limit DECIMAL(15,2) NOT NULL,
                                       start_date DATE NOT NULL,
                                       end_date DATE NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- =====================================================
-- FRIENDS
-- =====================================================
CREATE TABLE IF NOT EXISTS friends (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id BIGINT NOT NULL,
                                       friend_id BIGINT NOT NULL,
                                       status ENUM('PENDING','ACCEPTED','BLOCKED') DEFAULT 'PENDING',
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- POSTS
-- =====================================================
CREATE TABLE IF NOT EXISTS posts (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id BIGINT NOT NULL,
                                     content TEXT NOT NULL,
                                     image_url VARCHAR(255),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- COMMENTS
-- =====================================================
CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        post_id BIGINT NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        content TEXT NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- LIKES
-- =====================================================
CREATE TABLE IF NOT EXISTS likes (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     post_id BIGINT NOT NULL,
                                     user_id BIGINT NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     UNIQUE KEY unique_like (post_id, user_id),
                                     FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                                     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- SHARES
-- =====================================================
CREATE TABLE IF NOT EXISTS shares (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      post_id BIGINT NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
-- =====================================================
-- CHAT MESSAGES
-- =====================================================
CREATE TABLE IF NOT EXISTS chat_messages (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             sender_id BIGINT NOT NULL,
                                             receiver_id BIGINT NOT NULL,
                                             content TEXT NOT NULL,
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
                                             FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);

-- USERS
ALTER TABLE users
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    MODIFY updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- CATEGORIES (hiện tại không có created/updated, có thể thêm nếu muốn audit)
ALTER TABLE categories
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER type,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- WALLETS
ALTER TABLE wallets
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- TRANSACTIONS
ALTER TABLE transactions
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER transaction_date,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- BUDGETS
ALTER TABLE budgets
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- FRIENDS
ALTER TABLE friends
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- POSTS
ALTER TABLE posts
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- COMMENTS
ALTER TABLE comments
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- LIKES
ALTER TABLE likes
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- SHARES
ALTER TABLE shares
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

-- CHAT MESSAGES
ALTER TABLE chat_messages
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at;

