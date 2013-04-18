create table prizecrawler;

CREATE TABLE `lotcheckswitch` (
  `lotno` varchar(255) NOT NULL,
  `state` int(11) NOT NULL,
  PRIMARY KEY (`lotno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `tagency` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agencyname` varchar(40) DEFAULT NULL,
  `agencyno` varchar(15) DEFAULT NULL,
  `lotno` varchar(10) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `weight` double NOT NULL,
  `iscrawl` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `agencyno` (`agencyno`,`lotno`,`type`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `tagencyprizecode` (
  `agencyno` varchar(15) NOT NULL,
  `batchcode` varchar(15) NOT NULL,
  `lotno` varchar(10) NOT NULL,
  `checkstate` int(11) NOT NULL,
  `crawlstate` int(11) NOT NULL,
  `crawltimes` int(11) NOT NULL,
  `createdate` datetime DEFAULT NULL,
  `weight` double NOT NULL,
  `wincode` varchar(50) DEFAULT NULL,
  `codedate` datetime DEFAULT NULL,
  PRIMARY KEY (`agencyno`,`batchcode`,`lotno`),
  KEY `crawlstate_index` (`crawlstate`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8


CREATE TABLE `tagencyprizeinfo` (
  `agencyno` varchar(15) NOT NULL,
  `batchcode` varchar(15) NOT NULL,
  `lotno` varchar(10) NOT NULL,
  `checkstate` int(11) NOT NULL,
  `createdate` datetime DEFAULT NULL,
  `weight` double NOT NULL,
  `wininfo` varchar(300) DEFAULT NULL,
  `infodate` datetime DEFAULT NULL,
  PRIMARY KEY (`agencyno`,`batchcode`,`lotno`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8


CREATE TABLE `tnotification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agencynos` varchar(255) DEFAULT NULL,
  `batchcode` varchar(255) DEFAULT NULL,
  `info` varchar(400) DEFAULT NULL,
  `lotno` varchar(255) DEFAULT NULL,
  `noticedate` datetime DEFAULT NULL,
  `noticestate` int(11) NOT NULL,
  `noticetimes` int(11) NOT NULL,
  `threshold` double NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `winbasecode` varchar(255) DEFAULT NULL,
  `winspecialcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `noticestate_index` (`noticestate`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `tprizeinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batchcode` varchar(20) DEFAULT NULL,
  `crawlState` int(11) NOT NULL,
  `crawlnum` int(11) NOT NULL,
  `createdate` datetime DEFAULT NULL,
  `lotno` varchar(20) DEFAULT NULL,
  `noticeState` int(11) NOT NULL,
  `noticenum` int(11) NOT NULL,
  `winbasecode` varchar(50) DEFAULT NULL,
  `winspecialcode` varchar(20) DEFAULT NULL,
  `crawldate` datetime DEFAULT NULL,
  `noticedate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `batchcode_index` (`batchcode`),
  KEY `crawlState_index` (`crawlState`),
  KEY `noticeState_index` (`noticeState`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `tthreshold` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lotno` varchar(10) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `lotno` (`lotno`,`type`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `emailstate` (
  `lotno` varchar(10) NOT NULL,
  `ismail` int(11) default NULL,
  `crawltimes` int(11) default NULL,
  `noticetimes` int(11) default NULL,
  PRIMARY KEY  (`lotno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



insert into `lotcheckswitch` (`lotno`, `state`) values('F47102','0');
insert into `lotcheckswitch` (`lotno`, `state`) values('F47103','0');
insert into `lotcheckswitch` (`lotno`, `state`) values('F47104','0');
insert into `lotcheckswitch` (`lotno`, `state`) values('T01001','0');
insert into `lotcheckswitch` (`lotno`, `state`) values('T01002','0');
insert into `lotcheckswitch` (`lotno`, `state`) values('T01009','0');
insert into `lotcheckswitch` (`lotno`, `state`) values('T01011','0');
insert into `lotcheckswitch` (`lotno`, `state`) values('T01013','0');



insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('500万','I10001','T01001','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('澳客','I10002','T01001','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('淘宝','I10003','T01001','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('彩通','000004','T01001','code','1','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('大赢家','tc0001','T01001','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('彩通','000004','T01001','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('腾讯','tx0001','T01001','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('运维','H00001','T01001','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('客服','H00002','T01001','code','0.5','0');


insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('内蒙福彩','000002','F47103','info','1.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('内蒙福彩','000002','F47103','code','1','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('500万','I10001','F47103','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('澳客','I10002','F47103','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('淘宝','I10003','F47103','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('运维','H00001','F47103','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('客服','H00002','F47103','code','0.5','0');


insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('内蒙福彩','000002','F47104','info','1.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('内蒙福彩','000002','F47104','code','1','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('500万','I10001','F47104','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('澳客','I10002','F47104','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('淘宝','I10003','F47104','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('运维','H00001','F47104','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('客服','H00002','F47104','code','0.5','0');


insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('内蒙福彩','000002','F47102','info','1.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('内蒙福彩','000002','F47102','code','1','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('500万','I10001','F47102','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('澳客','I10002','F47102','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('淘宝','I10003','F47102','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('运维','H00001','F47102','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('客服','H00002','F47102','code','0.5','0');


insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('500万','I10001','T01002','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('澳客','I10002','T01002','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('淘宝','I10003','T01002','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('彩通','000004','T01002','code','1','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('大赢家','tc0001','T01002','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('彩通','000004','T01002','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('腾讯','tx0001','T01002','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('运维','H00001','T01002','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('客服','H00002','T01002','code','0.5','0');


insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('500万','I10001','T01011','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('澳客','I10002','T01011','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('淘宝','I10003','T01011','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('彩通','000004','T01011','code','1','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('大赢家','tc0001','T01011','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('彩通','000004','T01011','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('腾讯','tx0001','T01011','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('运维','H00001','T01011','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('客服','H00002','T01011','code','0.5','0');


insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('500万','I10001','T01009','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('澳客','I10002','T01009','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('淘宝','I10003','T01009','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('彩通','000004','T01009','code','1','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('大赢家','tc0001','T01009','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('彩通','000004','T01009','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('腾讯','tx0001','T01009','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('运维','H00001','T01009','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('客服','H00002','T01009','code','0.5','0');



insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('掌中弈','000009','T01013','code','1','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('掌中弈','000009','T01013','info','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('运维','H00001','T01013','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('客服','H00002','T01013','code','0.5','0');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('500万','I10001','T01013','code','0.5','1');
insert into `tagency` (`agencyname`, `agencyno`, `lotno`, `type`, `weight`, `iscrawl`) values('金彩子','I10004','T01013','code','0.5','1');


insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('1','T01001','1','code');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('2','T01001','0.7','info');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('3','F47104','1','code');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('4','F47104','0.5','info');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('5','F47103','1','code');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('6','F47103','0.5','info');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('7','F47102','1','code');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('8','F47102','0.5','info');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('9','T01002','1','code');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('10','T01002','0.5','info');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('11','T01011','1','code');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('12','T01011','0.5','info');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('13','T01009','1','code');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('14','T01009','0.5','info');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('15','T01013','1','code');
insert into `tthreshold` (`id`, `lotno`, `threshold`, `type`) values('16','T01013','0.5','info');


INSERT INTO `emailstate` (`lotno`, `ismail`, `crawltimes`,`noticetimes`) VALUES ('T01007', 1, 12,2);
INSERT INTO `emailstate` (`lotno`, `ismail`, `crawltimes`,`noticetimes`) VALUES ('T01010', 1, 12,2);
INSERT INTO `emailstate` (`lotno`, `ismail`, `crawltimes`,`noticetimes`) VALUES ('T01012', 1, 12,2);
INSERT INTO `emailstate` (`lotno`, `ismail`, `crawltimes`,`noticetimes`) VALUES ('T01014', 1, 12,2);


