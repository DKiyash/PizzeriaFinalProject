# Удаление ранее созданных таблиц
drop table if exists pizzeria_project.pizzeria;
drop table if exists pizzeria_project.pizza;
drop table if exists pizzeria_project.pizzeria_pizza;
drop table if exists pizzeria_project.flyway_schema_history;

# Создание таблицы pizzeria
create table if not exists pizzeria_project.pizzeria
(
    pr_id integer primary key auto_increment,
    pr_name varchar(64) unique not null,
    pr_address varchar(128) unique not null
);

# Создание таблицы pizza
create table if not exists pizzeria_project.pizza
(
    p_id integer primary key auto_increment,
    p_name varchar(64) unique not null,
    p_description varchar(255) unique not null,
    p_base_price numeric(5, 2) check (p_base_price between 0 and 100) not null,
    p_photo_link varchar(2048) not null
);

# Создание таблицы pizzeria_pizza
create table if not exists pizzeria_project.pizzeria_pizza
(
    pizzeria_id integer references pizzeria(pr_id),
    pizza_id integer references pizza(p_id),
    primary key (pizzeria_id, pizza_id)
);

# Заполнение таблицы pizzeria (для теста)
# use pizzeria_project;
insert into pizzeria_project.pizzeria (pr_name, pr_address)
values ('Pizzeria_01', 'Address_01'),
       ('Pizzeria_02', 'Address_02'),
       ('Pizzeria_03', 'Address_03');

# Заполнение таблицы pizza
insert into pizzeria_project.pizza (p_name, p_description, p_base_price, p_photo_link)
values ('Pizza_name_01', 'Description_01', 10, 'url_01'),
       ('Pizza_name_02', 'Description_02', 11, 'url_02'),
       ('Pizza_name_03', 'Description_03', 9, 'url_03');

# Заполнение таблицы pizzeria_pizza
insert into pizzeria_project.pizzeria_pizza (pizzeria_id, pizza_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (2, 2),
       (2, 3),
       (3, 1),
       (3, 3);