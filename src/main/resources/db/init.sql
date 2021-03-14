-- ----------------------------
-- table structure for t_biz_counter_user
-- ----------------------------
drop table if exists `t_biz_counter_user`;
create table `t_biz_counter_user`
(
    `id`            int(11) not null,
    `biz_id`        varchar(32) default null,
    `create_time`   datetime    default null,
    `deptment_name` varchar(32) default null,
    `name`          varchar(32) default null,
    `task_id`       varchar(32) default null,
    `user_name`     varchar(32) default null,
    primary key (`id`)
) engine=innodb default charset=utf8mb4;

-- ----------------------------
-- records of t_biz_counter_user
-- ----------------------------

-- ----------------------------
-- table structure for t_biz_file
-- ----------------------------
drop table if exists `t_biz_file`;
create table `t_biz_file`
(
    `id`               int(11) not null auto_increment,
    `create_time`      datetime    default null,
    `create_user`      longtext    not null,
    `description`      longtext,
    `filecatalog`      varchar(64) not null,
    `filetype`         varchar(64) not null,
    `name`             longtext    not null,
    `path`             longtext    not null,
    `task_id`          varchar(64) default null,
    `task_instance_id` varchar(64) default null,
    `task_name`        longtext,
    `biz_id`           varchar(64) default null,
    primary key (`id`),
    key                `fk34bt5oe9xt1ovakwe0nymup5m` (`biz_id`)
) engine=innodb auto_increment=4 default charset=utf8mb4;

-- ----------------------------
-- records of t_biz_file
-- ----------------------------
insert into `t_biz_file`
values ('1', '2019-02-21 10:27:29', 'admin', null, 'uploadfile', 'file', 'wx_kh_user.sql',
        '201902\\21\\9e6deeb91b2b42bb89808581fe17aa25.sql', '7513', null, '服务台处理', '16');
insert into `t_biz_file`
values ('2', '2019-02-21 10:33:42', 'admin', null, 'uploadfile', 'file', 'wx_kh_user.sql',
        '201902\\21\\7440ecf2879b41f483785203b89646cf.sql', '15007', null, '服务台处理', '16');
insert into `t_biz_file`
values ('3', '2019-02-21 10:33:42', 'admin', null, 'uploadfile', 'file', 'dubbo集群容错.txt',
        '201902\\21\\fb26ed13f3ba486daef33b8ac98e03d1.txt', '15007', null, '服务台处理', '16');

-- ----------------------------
-- table structure for t_biz_info
-- ----------------------------
drop table if exists `t_biz_info`;
create table `t_biz_info`
(
    `id`                    int(11) not null auto_increment,
    `work_num`              varchar(64) default null,
    `biz_type`              longtext     not null,
    `create_time`           datetime    default null,
    `create_user`           longtext     not null,
    `limit_time`            datetime    default null,
    `parent_id`             varchar(64) default null,
    `parent_taskname`       longtext,
    `process_definition_id` varchar(64)  not null,
    `process_instance_id`   varchar(64) default null,
    `source`                varchar(128) not null,
    `biz_status`            varchar(32)  not null,
    `task_assignee`         longtext,
    `task_def_key`          varchar(64) default null,
    `task_id`               longtext,
    `task_name`             longtext,
    `title`                 longtext,
    primary key (`id`)
) engine=innodb auto_increment=22 default charset=utf8mb4;

-- ----------------------------
drop table if exists `t_biz_info_conf`;
create table `t_biz_info_conf`
(
    `id`            int(11) not null auto_increment,
    `create_time`   datetime    default null,
    `task_assignee` longtext,
    `task_id`       varchar(64) default null,
    `biz_id`        varchar(64) default null,
    primary key (`id`),
    key             `fk4sit8xk90bn7gi75v5ag5aaoe` (`biz_id`)
) engine=innodb auto_increment=20 default charset=utf8mb4;

-- ----------------------------
-- table structure for t_biz_log
-- ----------------------------
drop table if exists `t_biz_log`;
create table `t_biz_log`
(
    `id`                 int(10) not null auto_increment,
    `create_time`        datetime    default null,
    `handle_description` longtext,
    `handle_name`        longtext    not null,
    `handle_result`      longtext    not null,
    `handle_user_name`   varchar(64) default null comment '操作人名称',
    `handle_user`        longtext    not null,
    `task_id`            varchar(64) not null,
    `task_name`          longtext,
    `user_dept`          varchar(64) default null,
    `user_phone`         varchar(64) default null,
    `biz_id`             varchar(64) default null,
    primary key (`id`),
    key                  `fkbsvvskjwl92mbq0o22sxin9xi` (`biz_id`)
) engine=innodb auto_increment=33 default charset=utf8mb4;

-- ----------------------------
-- table structure for t_biz_process_instance
-- ----------------------------
drop table if exists `t_biz_process_instance`;
create table `t_biz_process_instance`
(
    `id`                  int(10) not null auto_increment,
    `biz_id`              varchar(64)  not null,
    `create_time`         datetime    default null,
    `process_instance_id` varchar(64) default null,
    `value`               longtext     not null,
    `process_variable_id` varchar(64) default null,
    `task_id`             varchar(32)  not null,
    `handle_user`         varchar(128) not null,
    `variable_alias`      varchar(32)  not null,
    `variable_name`       varchar(32)  not null,
    `view_component`      varchar(32) default null,
    primary key (`id`),
    key                   `fkm1ungk6wgwapfog91iscp9sod` (`process_variable_id`)
) engine=innodb auto_increment=47 default charset=utf8mb4;

-- ----------------------------
-- table structure for t_biz_process_variable
-- ----------------------------
drop table if exists `t_biz_process_variable`;
create table `t_biz_process_variable`
(
    `id`                    int(11) not null auto_increment,
    `alias`                 longtext    not null,
    `group_name`            longtext,
    `group_order`           int(11) default null,
    `name`                  longtext    not null,
    `name_order`            int(11) default null,
    `process_definition_id` varchar(64) not null,
    `is_process_variable`   bit(1)      default null,
    `ref_param`             longtext,
    `ref_variable`          varchar(64) default null,
    `is_required`           bit(1)      not null,
    `version`               int(11) not null,
    `view_component`        longtext,
    `view_datas`            longtext,
    `view_params`           longtext,
    `task_id`               varchar(32) default null,
    `view_url`              longtext,
    primary key (`id`)
) engine=innodb auto_increment=24 default charset=utf8mb4;

-- ----------------------------
-- records of t_biz_process_variable
-- ----------------------------
insert into `t_biz_process_variable`
values ('3', '处理方式', '', '1', 'handletype', '1', 'eventmanagement:3:2508', '\0', '', '', '\0', '3', 'treatment', '', '',
        'vendorhandle', null);
insert into `t_biz_process_variable`
values ('6', '处理意见', '', '1', 'handlemessage', '1', 'eventmanagement:3:2508', '\0', '', '', '\0', '3', 'textarea', '',
        '', 'start', null);
insert into `t_biz_process_variable`
values ('7', '处理方式', '', '1', 'handletype', '1', 'eventmanagement:3:2508', '\0', '', '7', '\0', '3', 'treatment', '',
        '', 'servicehandle', null);
insert into `t_biz_process_variable`
values ('8', '下拉', '', '1', 'ddd', '1', 'eventmanagement:3:2508', '\0', '', '', '\0', '3', 'dictcombobox', '1,2', '1',
        'start', null);
insert into `t_biz_process_variable`
values ('9', '处理方式', '', '1', 'handletype', '1', 'eventmanagement:3:2508', '\0', '', '', '\0', '3', 'treatment', '', '',
        'vendorhandle', null);
insert into `t_biz_process_variable`
values ('11', '工时', '', '1', 'worktime', '2', 'eventmanagement:3:2508', '\0', '', '', '', '3', 'number', '', '',
        'servicehandle', null);
insert into `t_biz_process_variable`
values ('12', '处理人', '', '1', 'handleuser', '2', 'eventmanagement:3:2508', '', '', '', '', '3', 'text', '', '',
        'start', null);
insert into `t_biz_process_variable`
values ('23', '事件级别', '', '1', 'level', '1', 'eventmanagement:3:2508', '\0', '', '', '\0', '3', 'text', '', '', 'start',
        null);

-- ----------------------------
-- table structure for t_biz_template_file
-- ----------------------------
drop table if exists `t_biz_template_file`;
create table `t_biz_template_file`
(
    `id`          int(10) not null auto_increment,
    `create_time` datetime    not null,
    `create_user` varchar(64) default null,
    `file_name`   varchar(64) default null,
    `flow_name`   varchar(32) not null,
    `full_name`   varchar(64) default null,
    primary key (`id`)
) engine=innodb auto_increment=6 default charset=utf8mb4;

-- ----------------------------
-- table structure for t_counter_sign
-- ----------------------------
drop table if exists `t_counter_sign`;
create table `t_counter_sign`
(
    `id`                   int(10) not null auto_increment,
    `create_time`          datetime default null,
    `is_complete`          int(11) not null,
    `processdefinition_id` varchar(128) not null,
    `processinstance_id`   varchar(128) not null,
    `result_type`          int(11) not null,
    `task_assignee`        varchar(32)  not null,
    `task_id`              varchar(32)  not null,
    `biz_id`               varchar(32)  not null,
    primary key (`id`)
) engine=innodb default charset=utf8mb4;

-- ----------------------------
-- records of t_counter_sign
-- ----------------------------

-- ----------------------------
-- table structure for t_counter_user
-- ----------------------------
drop table if exists `t_counter_user`;
create table `t_counter_user`
(
    `id`            int(10) not null,
    `bizid`         varchar(32) default null,
    `create_time`   datetime    default null,
    `deptment_name` varchar(32) default null,
    `name`          varchar(32) default null,
    `taskid`        varchar(32) default null,
    `user_name`     varchar(32) default null,
    primary key (`id`)
) engine=innodb default charset=utf8mb4;

-- ----------------------------
-- records of t_counter_user
-- ----------------------------
-- ----------------------------
-- table structure for t_dict_type
-- ----------------------------
drop table if exists `t_dict_type`;
create table `t_dict_type`
(
    `id`          int(10) not null auto_increment,
    `create_time` datetime     default null,
    `creator`     varchar(255) default null,
    `modified`    datetime     default null,
    `modifier`    varchar(255) default null,
    `name`        varchar(255) default null,
    primary key (`id`)
) engine=innodb auto_increment=7 default charset=utf8mb4;

-- ----------------------------
-- records of t_dict_type
-- ----------------------------
insert into `t_dict_type`
values ('1', '2018-01-29 13:59:42', '1', '2018-01-29 13:59:48', '1', '性别');
insert into `t_dict_type`
values ('4', '2019-02-15 06:50:49', 'admin', '2019-02-15 06:50:49', 'admin', 'banmart');
insert into `t_dict_type`
values ('5', '2019-02-15 06:50:55', 'admin', '2019-02-15 06:50:55', 'admin', '亲橙里');
insert into `t_dict_type`
values ('6', '2019-02-15 06:51:00', 'admin', '2019-02-15 07:03:56', '1', '亲橙里编辑');

-- ----------------------------
-- table structure for t_dict_value
-- ----------------------------
drop table if exists `t_dict_value`;
create table `t_dict_value`
(
    `id`           int(10) not null auto_increment,
    `code`         varchar(255) default null,
    `create_time`  datetime     default null,
    `creator`      varchar(255) default null,
    `dict_type_id` varchar(255) default null,
    `modified`     datetime     default null,
    `modifier`     varchar(255) default null,
    `name`         varchar(255) default null,
    primary key (`id`)
) engine=innodb auto_increment=9 default charset=utf8mb4;

-- ----------------------------
-- records of t_dict_value
-- ----------------------------
insert into `t_dict_value`
values ('1', '1', null, null, '1', null, null, '男');
insert into `t_dict_value`
values ('2', '2', null, null, '1', '2019-02-15 08:28:46', 'admin', '女');
insert into `t_dict_value`
values ('8', '0', '2019-02-15 08:28:57', 'admin', '1', '2019-02-15 08:28:57', 'admin', '未知');

-- ----------------------------
-- table structure for t_sys_role
-- ----------------------------
drop table if exists `t_sys_role`;
create table `t_sys_role`
(
    `id`          int(10) not null auto_increment,
    `create_time` datetime    default null,
    `name_cn`     varchar(64) default null,
    `name_en`     varchar(64) default null,
    primary key (`id`)
) engine=innodb default charset=utf8mb4;

-- ----------------------------
-- records of t_sys_role
-- ----------------------------

-- ----------------------------
-- table structure for t_sys_user
-- ----------------------------
drop table if exists `t_sys_user`;
create table `t_sys_user`
(
    `id`              int(64) not null auto_increment,
    `create_time`     datetime    default null,
    `email`           varchar(64) default null,
    `last_login_time` datetime    default null,
    `name`            varchar(64) default null,
    `password`        varchar(64) default null,
    `status`          int(11) default null,
    `username`        varchar(64) default null,
    primary key (`id`)
) engine=innodb auto_increment=2 default charset=utf8mb4;

-- ----------------------------
-- records of t_sys_user
-- ----------------------------
insert into `t_sys_user`
values ('1', '2017-12-02 10:36:50', null, null, '超级管理员', 'admin', '1', 'admin');

-- ----------------------------
-- table structure for t_sys_user_role
-- ----------------------------
drop table if exists `t_sys_user_role`;
create table `t_sys_user_role`
(
    `role_id` int(64) not null,
    `user_id` int(64) not null,
    `id`      int(10) not null,
    primary key (`id`)
) engine=innodb default charset=utf8mb4;

-- ----------------------------
-- records of t_sys_user_role
-- ----------------------------

-- ----------------------------
-- table structure for t_timed_task
-- ----------------------------
drop table if exists `t_timed_task`;
create table `t_timed_task`
(
    `id`           int(10) not null,
    `biz_id`       int(10) default null,
    `button_id`    varchar(32)  default null,
    `create_time`  datetime     default null,
    `end_time`     varchar(255) default null,
    `task_def_key` varchar(64)  default null,
    `task_id`      varchar(64)  default null,
    `task_name`    varchar(64)  default null,
    primary key (`id`)
) engine=innodb default charset=utf8mb4;
