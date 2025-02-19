-- Drop tables if they exist to start fresh
DROP TABLE IF EXISTS operation_logs;
DROP TABLE IF EXISTS production_plan_details;
DROP TABLE IF EXISTS production_plans;
DROP TABLE IF EXISTS model_parts;
DROP TABLE IF EXISTS models;
DROP TABLE IF EXISTS parts;
DROP TABLE IF EXISTS projects;

-- Create Projects table
CREATE TABLE IF NOT EXISTS projects (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL UNIQUE,
                                        description TEXT NOT NULL,
                                        planning_type VARCHAR(50) NOT NULL DEFAULT 'fixed',
                                        active BOOLEAN NOT NULL DEFAULT true,
                                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                        updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create Parts table
CREATE TABLE IF NOT EXISTS parts (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL UNIQUE,
                                     active BOOLEAN NOT NULL DEFAULT true,
                                     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create Models table
CREATE TABLE IF NOT EXISTS models (
                                      id SERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL UNIQUE,
                                      project_id INTEGER NOT NULL,
                                      active BOOLEAN NOT NULL DEFAULT true,
                                      created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                      updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                      CONSTRAINT fk_model_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Create ModelParts table (bridging entity for Model and Part)
CREATE TABLE IF NOT EXISTS model_parts (
                                           id SERIAL PRIMARY KEY,
                                           model_id INTEGER NOT NULL,
                                           part_id INTEGER NOT NULL,
                                           quantity INTEGER NOT NULL,
                                           active BOOLEAN NOT NULL DEFAULT true,
                                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                           updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                           CONSTRAINT fk_modelpart_model FOREIGN KEY (model_id) REFERENCES models(id) ON DELETE CASCADE,
                                           CONSTRAINT fk_modelpart_part FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE CASCADE
);

-- Create ProductionPlans table
CREATE TABLE IF NOT EXISTS production_plans (
                                                id SERIAL PRIMARY KEY,
                                                project_id INTEGER NOT NULL,
                                                month VARCHAR(7), -- Stored as "YYYY-MM"
                                                total_production INTEGER NOT NULL,
                                                active BOOLEAN NOT NULL DEFAULT true,
                                                week VARCHAR(10), -- Stored as "YYYY-WW"
                                                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                CONSTRAINT fk_productionplan_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- Create ProductionPlanDetails table
CREATE TABLE IF NOT EXISTS production_plan_details (
                                                       id SERIAL PRIMARY KEY,
                                                       production_plan_id INTEGER NOT NULL,
                                                       model_id INTEGER NOT NULL,
                                                       percentage DOUBLE PRECISION NOT NULL,
                                                       quantity INTEGER NOT NULL,
                                                       active BOOLEAN NOT NULL DEFAULT true,
                                                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                       updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                       CONSTRAINT fk_ppd_production_plan FOREIGN KEY (production_plan_id) REFERENCES production_plans(id) ON DELETE CASCADE,
                                                       CONSTRAINT fk_ppd_model FOREIGN KEY (model_id) REFERENCES models(id) ON DELETE CASCADE
);

-- Create OperationLogs table
CREATE TABLE IF NOT EXISTS operation_logs (
                                              id SERIAL PRIMARY KEY,
                                              entity_name VARCHAR(255) NOT NULL,
                                              entity_id BIGINT,
                                              operation_type VARCHAR(50) NOT NULL,
                                              description TEXT,
                                              timestamp TIMESTAMP DEFAULT NOW()
);