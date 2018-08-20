CREATE TABLE users 
(user_id INT NOT NULL, 
username VARCHAR(50) NOT NULL, 
password VARCHAR(100) NOT NULL, 
enabled BIT NOT NULL, 
CONSTRAINT PK_users_user_id PRIMARY KEY (user_id));

CREATE TABLE authorities 
(user_id INT NOT NULL, 
authority VARCHAR(50) NOT NULL);
