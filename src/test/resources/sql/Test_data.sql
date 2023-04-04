# Заполнение таблицы pizzeria (для теста)
# use pizzeria_project;
insert into pizzeria_project.pizzeria (pr_name, pr_address)
values ('Pizzeria_01', 'Address_01'),
       ('Pizzeria_02', 'Address_02'),
       ('Pizzeria_03', 'Address_03'),
       ('Pizzeria_04', 'Address_04');

# Заполнение таблицы pizza
insert into pizzeria_project.pizza (p_name, p_description, p_base_price, p_photo_link)
values ('Pizza_name_01', 'Description_01', 10, 'url_01'),
       ('Pizza_name_02', 'Description_02', 11, 'url_02'),
       ('Pizza_name_03', 'Description_03', 9, 'url_03'),
       ('Pizza_name_04', 'Description_04', 12, 'url_04');

# Заполнение таблицы pizzeria_pizza
insert into pizzeria_project.pizzeria_pizza (pizzeria_id, pizza_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (2, 2),
       (2, 3),
       (3, 1),
       (3, 4),
       (4, 3);