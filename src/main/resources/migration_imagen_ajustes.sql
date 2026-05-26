-- =========================================
-- Migración: Editor de imágenes (no destructivo)
-- Agrega 6 columnas a cada tabla que tiene imagen, para soportar
-- punto focal (posX, posY), zoom (escala), rotación y volteos.
--
-- NOTA: spring.jpa.hibernate.ddl-auto=update ya agrega las columnas
-- automáticamente al iniciar la app. Este archivo se conserva como
-- referencia y para entornos donde se prefiera correr la migración
-- manualmente.
--
-- Todas las columnas son nullable y se interpretan en el frontend
-- como "sin transformación" si vienen NULL — por eso las imágenes
-- existentes mantienen exactamente el comportamiento actual.
-- =========================================

-- Tabla: imagenes_producto
ALTER TABLE imagenes_producto
  ADD COLUMN IF NOT EXISTS pos_x DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS pos_y DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS escala DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS rotacion INTEGER,
  ADD COLUMN IF NOT EXISTS volteo_h BOOLEAN,
  ADD COLUMN IF NOT EXISTS volteo_v BOOLEAN;

-- Tabla: imagen (proyectos)
ALTER TABLE imagen
  ADD COLUMN IF NOT EXISTS pos_x DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS pos_y DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS escala DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS rotacion INTEGER,
  ADD COLUMN IF NOT EXISTS volteo_h BOOLEAN,
  ADD COLUMN IF NOT EXISTS volteo_v BOOLEAN;

-- Tabla: categorias_producto
ALTER TABLE categorias_producto
  ADD COLUMN IF NOT EXISTS pos_x DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS pos_y DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS escala DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS rotacion INTEGER,
  ADD COLUMN IF NOT EXISTS volteo_h BOOLEAN,
  ADD COLUMN IF NOT EXISTS volteo_v BOOLEAN;

-- Tabla: categoria (proyectos)
ALTER TABLE categoria
  ADD COLUMN IF NOT EXISTS pos_x DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS pos_y DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS escala DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS rotacion INTEGER,
  ADD COLUMN IF NOT EXISTS volteo_h BOOLEAN,
  ADD COLUMN IF NOT EXISTS volteo_v BOOLEAN;

-- Tabla: procesos_producto
ALTER TABLE procesos_producto
  ADD COLUMN IF NOT EXISTS pos_x DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS pos_y DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS escala DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS rotacion INTEGER,
  ADD COLUMN IF NOT EXISTS volteo_h BOOLEAN,
  ADD COLUMN IF NOT EXISTS volteo_v BOOLEAN;

-- Tabla: proceso_diseno (procesos de proyecto)
ALTER TABLE proceso_diseno
  ADD COLUMN IF NOT EXISTS pos_x DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS pos_y DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS escala DOUBLE PRECISION,
  ADD COLUMN IF NOT EXISTS rotacion INTEGER,
  ADD COLUMN IF NOT EXISTS volteo_h BOOLEAN,
  ADD COLUMN IF NOT EXISTS volteo_v BOOLEAN;
