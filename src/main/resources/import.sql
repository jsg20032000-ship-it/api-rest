INSERT INTO user_entity (id, username, email, password, fullname, role) VALUES (1, 'admin', 'admin@todo.com', '$2a$10$blmJbdtAgE/.nV5vPOfcSuoIez6s1h86WxMro6GwjQfmFvKQPlhgu', 'Administrador', 'ADMIN');
INSERT INTO user_entity (id, username, email, password, fullname, role) VALUES (2, 'gestor', 'gestor@todo.com', '$2a$10$blmJbdtAgE/.nV5vPOfcSuoIez6s1h86WxMro6GwjQfmFvKQPlhgu', 'Gestor', 'GESTOR');
INSERT INTO user_entity (id, username, email, password, fullname, role) VALUES (3, 'user', 'user@todo.com', '$2a$10$blmJbdtAgE/.nV5vPOfcSuoIez6s1h86WxMro6GwjQfmFvKQPlhgu', 'Usuario Normal', 'USER');

INSERT INTO category (id, title) VALUES (1, 'Personal');
INSERT INTO category (id, title) VALUES (2, 'Trabajo');
INSERT INTO category (id, title) VALUES (3, 'Estudios');

INSERT INTO task (id, created_at, deadline, title, description, completed, priority, author_id, category_id) VALUES (1, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'Comprar alimentos', 'Hacer una lista de compras para el supermercado.', false, 'MEDIUM', 3, 1);
INSERT INTO task (id, created_at, deadline, title, description, completed, priority, author_id, category_id) VALUES (2, NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), 'Pagar facturas', 'Pagar la factura de electricidad antes de la fecha límite.', false, 'HIGH', 3, 2);
INSERT INTO task (id, created_at, deadline, title, description, completed, priority, author_id, category_id) VALUES (3, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'Estudiar para el examen', 'Revisar los temas para el próximo examen.', false, 'HIGH', 3, 3);
INSERT INTO task (id, created_at, deadline, title, description, completed, priority, author_id, category_id) VALUES (4, NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), 'Llamar al doctor', 'Agendar una cita con el médico para el chequeo anual.', false, 'LOW', 3, 1);
INSERT INTO task (id, created_at, deadline, title, description, completed, priority, author_id, category_id) VALUES (5, NOW(), DATE_ADD(NOW(), INTERVAL 20 DAY), 'Leer un libro', 'Terminar de leer Cien años de soledad.', false, 'LOW', 3, 1);