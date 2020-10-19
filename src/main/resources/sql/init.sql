DROP DATABASE IF EXISTS aqa-training-engine;
CREATE DATABASE aqa-training-engine;
GRANT ALL PRIVILEGES ON aqa-training-engine.* TO 'aqa'@'%' IDENTIFIED BY 'aqa';
GRANT ALL PRIVILEGES ON aqa-training-engine.* TO 'aqa'@'localhost' IDENTIFIED BY 'aqa';

USE aqa-training-engine;

CREATE TABLE IF NOT EXISTS `users` (
  `uuid` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE IF NOT EXISTS `tasks` (
  `uuid` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `deadline` datetime(6) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `state` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `users_tasks` (
  `user_uuid` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `task_uuid` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`user_uuid`,`task_uuid`),
  KEY `FK8t4sifu6rjeqib2dojyhb4e4m` (`task_uuid`),
  CONSTRAINT `FK8t4sifu6rjeqib2dojyhb4e4m` FOREIGN KEY (`task_uuid`) REFERENCES `tasks` (`uuid`),
  CONSTRAINT `FKpjp1a92qxw1xgxi7k8c1ny8ys` FOREIGN KEY (`user_uuid`) REFERENCES `users` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `notes` (
  `uuid` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `body` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_uuid` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`uuid`),
  KEY `FKghryswoip98kx0oh9fj3unpnt` (`user_uuid`),
  CONSTRAINT `FKghryswoip98kx0oh9fj3unpnt` FOREIGN KEY (`user_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
