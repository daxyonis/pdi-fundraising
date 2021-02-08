package com.poivredesiles.fundraising.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.product.ProductLastImport;

public interface ProductLastImportRepository extends JpaRepository<ProductLastImport, String> {

}
