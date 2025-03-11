SELECT CONSTRAINT_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'orderitem'
  AND COLUMN_NAME = 'product_id';

-- Remove the FK and unique index constraints  
ALTER TABLE orderitem DROP FOREIGN KEY FKafs843452l2x1org5d0nj16h3;
ALTER TABLE orderitem DROP INDEX UKeixp9p5b0u0vqagyfe0c0ar6h;

-- Recreate the non-unique index 
CREATE INDEX idx_orderitem_product ON orderitem (product_id);
-- Re-add the foreign key constraint:
ALTER TABLE orderitem
ADD CONSTRAINT fk_orderitem_product
FOREIGN KEY (product_id) REFERENCES pdiproduct(id);

