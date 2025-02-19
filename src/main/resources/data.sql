-- Insert sample projects
INSERT INTO projects (name, description, planning_type, active, created_at, updated_at) VALUES
                                                                                            ('Project A', 'First test project', 'fixed', true, NOW(), NOW()),
                                                                                            ('Project B', 'Second test project', 'monthly', true, NOW(), NOW()),
                                                                                            ('Project C', 'Third test project', 'weekly', true, NOW(), NOW());

-- Insert sample parts
INSERT INTO parts (name, active, created_at, updated_at) VALUES
                                                             ('Engine', true, NOW(), NOW()),
                                                             ('Wheel', true, NOW(), NOW()),
                                                             ('Door', true, NOW(), NOW());

-- Insert sample models (Project A: Fixed, Project B: Monthly, Project C: Weekly)
INSERT INTO models (name, project_id, active, created_at, updated_at) VALUES
                                                                          ('Model X', 1, true, NOW(), NOW()),
                                                                          ('Model Y', 1, true, NOW(), NOW()),
                                                                          ('Model Z', 2, true, NOW(), NOW()),
                                                                          ('Model W', 3, true, NOW(), NOW());

-- Insert sample model_parts (relationship between models and parts)
INSERT INTO model_parts (model_id, part_id, quantity, created_at, updated_at) VALUES
                                                                                  (1, 1, 1, NOW(), NOW()),  -- Model X: 1 Engine (part id 1)
                                                                                  (1, 2, 4, NOW(), NOW()),  -- Model X: 4 Wheels (part id 2)
                                                                                  (2, 3, 2, NOW(), NOW()),  -- Model Y: 2 Doors (part id 3)
                                                                                  (3, 2, 4, NOW(), NOW()),  -- Model Z: 4 Wheels (part id 2)
                                                                                  (4, 1, 2, NOW(), NOW());  -- Model W: 2 Engines (part id 1)

-- Insert a sample production plan for Project A (Fixed) in March 2025
INSERT INTO production_plans (project_id, month, week, total_production, created_at, updated_at) VALUES
    (1, '2025-03', NULL, 1000, NOW(), NOW());

-- Insert a sample production plan for Project B (Monthly) in April 2025
INSERT INTO production_plans (project_id, month, week, total_production, created_at, updated_at) VALUES
    (2, '2025-04', NULL, 1500, NOW(), NOW());

-- Insert a sample production plan for Project C (Weekly) in the 10th week of 2025
INSERT INTO production_plans (project_id, month, week, total_production, created_at, updated_at) VALUES
    (3, NULL, '2025-10', 2000, NOW(), NOW());

-- Insert sample production plan details for each production plan
INSERT INTO production_plan_details (production_plan_id, model_id, percentage, quantity, created_at, updated_at) VALUES
                                                                                                                     -- Project A (Fixed - Monthly)
                                                                                                                     (1, 1, 0.5, 1, NOW(), NOW()),
                                                                                                                     (1, 2, 0.5, 1, NOW(), NOW()),

                                                                                                                     -- Project B (Monthly)
                                                                                                                     (2, 3, 0.6, 2, NOW(), NOW()),

                                                                                                                     -- Project C (Weekly)
                                                                                                                     (3, 4, 0.8, 1, NOW(), NOW());

-- Insert sample operation logs (optional)
INSERT INTO operation_logs (entity_name, operation_type, entity_id, timestamp, description) VALUES
                                                                                                ('Project', 'CREATE', 1, NOW(), 'Project A created'),
                                                                                                ('Project', 'CREATE', 2, NOW(), 'Project B created'),
                                                                                                ('Project', 'CREATE', 3, NOW(), 'Project C created'),
                                                                                                ('Part', 'CREATE', 1, NOW(), 'Engine created'),
                                                                                                ('Model', 'CREATE', 1, NOW(), 'Model X created'),
                                                                                                ('Model', 'CREATE', 2, NOW(), 'Model Y created');
