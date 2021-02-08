
-- Query the most recent (greatest) modified date
SELECT 'max' as id, GREATEST(MAX(p.last_modified_date), MAX(c.last_modified_date)) AS lastImportInstant FROM pdiproduct p, pdicategory c;

SELECT GREATEST(MAX(c.last_modified_date), MAX(p.last_modified_date)) as LastProductImport
FROM pdicategory c JOIN pdiproduct p ON p.category_id=c.id;