
CREATE TABLE quiz (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(45) DEFAULT NULL,
  description VARCHAR(500) DEFAULT NULL,
  start_date DATE DEFAULT NULL,
  end_date DATE DEFAULT NULL,
  published TINYINT DEFAULT '0',
  PRIMARY KEY (id)
);

CREATE TABLE question (
  quiz_id INT NOT NULL,
  ques_id INT NOT NULL,
  question VARCHAR(100) DEFAULT NULL,
  type VARCHAR(45) DEFAULT NULL,
  required TINYINT DEFAULT '0',
  options VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (quiz_id, ques_id)
);

CREATE TABLE feedback (
  user_name VARCHAR(20) DEFAULT NULL,
  phone VARCHAR(15) DEFAULT NULL,
  email VARCHAR(45) NOT NULL,
  age INT DEFAULT '0',
  quiz_id INT NOT NULL DEFAULT '0',
  ques_id INT NOT NULL DEFAULT '0',
  answer VARCHAR(500) DEFAULT NULL,
  fillin_date DATE DEFAULT NULL,
  PRIMARY KEY (email, quiz_id, ques_id)
);
