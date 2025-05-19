CREATE TABLE Usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    correo VARCHAR(100) UNIQUE,
    contraseña VARCHAR(100),
    rol ENUM('cliente', 'tecnico', 'admin')
);

CREATE TABLE Incidencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255),
    descripcion TEXT,
    estado ENUM('pendiente','asignada', 'en curso', 'resuelta') DEFAULT 'pendiente',
    cliente_id INT,
    tecnico_id INT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion DATETIME,
    resolucion TEXT,
    FOREIGN KEY (cliente_id) REFERENCES Usuario(id),
    FOREIGN KEY (tecnico_id) REFERENCES Usuario(id)
);


/*CREATE TABLE Incidencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255),
    descripcion TEXT,
    estado ENUM('pendiente', 'en curso', 'resuelta') DEFAULT 'pendiente',
    cliente_id INT,
    tecnico_id INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion DATETIME,
    resolucion TEXT,
    FOREIGN KEY (cliente_id) REFERENCES Usuario(id),
    FOREIGN KEY (tecnico_id) REFERENCES Usuario(id)
);*/



INSERT INTO Usuario (nombre, correo, contraseña, rol) VALUES
('Carlos Cliente', 'cliente@demo.com', '1234', 'cliente'),
('Teresa Tecnico', 'tecnico@demo.com', '1234', 'tecnico'),
('Ana Admin', 'admin@demo.com', '1234', 'admin');



INSERT INTO Incidencia (titulo, descripcion, estado, cliente_id, tecnico_id, resolucion)
VALUES
('Error en inicio de sesión', 'No puedo acceder a la plataforma.', 'pendiente', 1, NULL, NULL),
('Pantalla congelada', 'El sistema se queda bloqueado al abrir.', 'en curso', 1, 2, NULL),
('Correo no funciona', 'No se envían los correos automáticos.', 'resuelta', 1, 2, 'Se reinició el servicio SMTP.');