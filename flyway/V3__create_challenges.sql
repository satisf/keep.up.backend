CREATE TABLE challenges(
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      owner_id INT,
                      name VARCHAR(256),
                      times INT,
                      duration VARCHAR(256),
                      start_date DATE,
                      FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE participants(
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           user_id INT,
                           challenge_id INT,
                           FOREIGN KEY (user_id) REFERENCES users(id),
                           FOREIGN KEY (challenge_id) REFERENCES  challenges(id)
);
CREATE TABLE repetitions(
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           participant_id INT,
                           completion_time DATETIME,
                           FOREIGN KEY (participant_id) REFERENCES participants(id)
);