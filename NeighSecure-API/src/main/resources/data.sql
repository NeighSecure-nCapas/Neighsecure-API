--CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO rol (rol_id, rol) VALUES ('8f14e45f-e2c1-4e23-91e5-7b93e11a7b55','Administrador') ON CONFLICT DO NOTHING;
INSERT INTO rol (rol_id, rol) VALUES ('e0f26b84-8d29-4f57-8b4f-80b41c3a6a1d','Visitante') ON CONFLICT DO NOTHING;
INSERT INTO rol (rol_id, rol) VALUES ('c0d63d5a-97a7-4ff4-9b7a-2d6167e1c5f3','Residente') ON CONFLICT DO NOTHING;
INSERT INTO rol (rol_id, rol) VALUES ('d8fbb674-5a8d-4f53-a870-2b9d4743a418','Encargado') ON CONFLICT DO NOTHING;
INSERT INTO rol (rol_id, rol) VALUES ('1f9c7e2e-9d3b-43a8-ae8f-62147b7e1c34','Vigilante') ON CONFLICT DO NOTHING;

INSERT INTO terminal (terminal_id, tipo_entrada) VALUES ('d0c2b3e2-d4f0-4a7d-b582-4356e8ec3ff9','Vehicular') ON CONFLICT DO NOTHING;
INSERT INTO terminal (terminal_id, tipo_entrada) VALUES ('1c03d183-8f0a-4f1c-b6a8-9a19e2e7c508','Peatonal') ON CONFLICT DO NOTHING;

-- INSERT INTO usuario (usuario_id, nombre_completo, correo, telefono, casa_id, dui, estado_user)
-- VALUES ('6e4d596e-3a4f-4a24-92ec-d7b02dd657d7','fernando', 'fernando@gmail.com', '1234-5678', null, '0000000-1', true)
-- ON CONFLICT DO NOTHING;

--INSERT INTO usuario_rol_id (users_usuario_id, rol_id_rol_id) VALUES ('6e4d596e-3a4f-4a24-92ec-d7b02dd657d7', '1f9c7e2e-9d3b-43a8-ae8f-62147b7e1c34') ON CONFLICT DO NOTHING;

--INSERT INTO usuario_rol_id (users_usuario_id, rol_id_rol_id) VALUES (1, 1);
