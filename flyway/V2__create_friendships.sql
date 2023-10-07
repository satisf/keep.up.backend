CREATE TABLE friendships(
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      user_id INT,
                      friend_id INT,
                      state VARCHAR(12),
                      FOREIGN KEY (user_id) REFERENCES users(id),
                      FOREIGN KEY (friend_id) REFERENCES users(id)
);