create database freeman_product;

use freeman_product;

create table tb_product_0
(
    `id`   varchar(255) primary key,
    `name` varchar(255)
);

insert into tb_product_0(id, name) values ('1', 'goods 01');
insert into tb_product_0(id, name) values ('2', 'goods 02');

-- user
create database freeman_user;

use freeman_user;

create table tb_user_0
(
    `id`   varchar(255) primary key,
    `name` varchar(255)
);
insert into tb_user_0(id, name) values ('2', 'freeman01');
insert into tb_user_0(id, name) values ('4', 'daniel03');


create table tb_user_1
(
    `id`   varchar(255) primary key,
    `name` varchar(255)
);
insert into tb_user_1(id, name) values ('1', 'freeman02');
insert into tb_user_1(id, name) values ('3', 'daniel04');