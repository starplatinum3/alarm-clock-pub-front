
 DROP TABLE IF EXISTS `Alarm_clock`;  
 CREATE TABLE `Alarm_clock` ( 
 `id` int(11) NOT NULL AUTO_INCREMENT, 
  
 `clock_id` null, 
 `hour` null, 
 `minute` null, 
 `ring_cycle` null, 
 `is_started` null, 
 `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL, 
 `ring_music_uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL, 
 `is_vibrated` null, 
 `stop_once` null, 
 PRIMARY KEY (`id`) USING BTREE,
 INDEX `id`(`id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci;
