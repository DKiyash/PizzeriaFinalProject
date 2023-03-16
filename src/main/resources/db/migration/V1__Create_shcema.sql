DROP DATABASE if exists pizzeria_project;
create schema if not exists pizzeria_project;
use pizzeria_project;
# Создание таблицы pizzeria
create table if not exists pizzeria_project.pizzeria
(
    pr_id integer primary key auto_increment,
    pr_name varchar(64) not null,
    pr_address varchar(128) not null
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
    pr_id integer not null,
    p_id integer not null,
    foreign key (pr_id) references pizzeria(pr_id),
    foreign key (p_id) references pizza(p_id)
);






#------------------------------------------------------------------
# для размера (вариант)
# Создание таблицы size
# create table if not exists size
# (
# #     size_id  integer primary key auto_increment,
#     s_name enum('small', 'standard', 'large') primary key,
#     s_price_k numeric(3, 2) check (s_price_k between 1 and 2) not null
# );
# # Создание таблицы pizza
# create table if not exists pizza
# (
#     p_id integer primary key auto_increment,
#     p_name varchar(64) not null,
#     p_description varchar(255) not null,
#     p_base_price numeric(5, 2) check (p_base_price between 0 and 100) not null,
#     p_size enum('small', 'standard', 'large'),
#     p_photo_link varchar(2048) not null,
#     foreign key (p_size) references size(s_name),
#     # имени пиццы с одинаковым размером не должно быть
#     constraint uc_pizza unique (p_name, p_size)
# );
# Заполнение таблицы size
# insert into size (s_name, s_price_k)
# values ('small', 1.00),
#        ('standard', 1.50),
#        ('large', 1.75);
# Заполнение таблицы pizza
# insert into pizza (p_name, p_description, p_base_price, p_size, p_photo_link)
# values ('Pizza Manhattan', 'Peperoni, Mozarella, sauce, Mushrooms', 10, 'standard', 'url Pizza Manhattan'),
#        ('Pizza Margarita', 'Mozarella(x2), sauce', 11, 'small', 'url Pizza Margarita'),
#        ('Pizza Pepperoni With Tomatoes', 'Tomatoes, Peperoni, BBQ sauce, Mozarella', 9, 'large', 'url Pizza Pepperoni With Tomatoes');
