package com.cylinder.sales.model;

import com.cylinder.products.model.Product;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "quote_product_lookup", schema = "sale")
public class ProductQuote implements Serializable {

    /**
     * The identifyer of the quote-product relation.
     */
    @Getter
    @Setter
    @Id
    @Column(name = "quote_product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quoteProductId;

    /**
     * The identifyer of the product.
     */
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * The identifyer of the quote.
     */
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "quote_id")
    private Quote quote;

    /**
     * The quantity of the product on the quote.
     */
    @Getter
    @Setter
    @Column
    private Long quantity;

    /**
     * The discount on the product on the quote.
     */
    @Getter
    @Setter
    @Column
    @DecimalMax(value = "0.99", message = "Please enter a valid discount.")
    @DecimalMin(value = "0.00", message = "Please enter a valid discount.")
    private float discount;

    /**
     * Calculate a total for the item
     *
     * @return the total of the item's price
     */
    public BigDecimal getUnitTotal() {
        float multiplyer = (1 - discount) * quantity;
        BigDecimal unitTotal = product.getUnitPrice().multiply(new BigDecimal(multiplyer));
        unitTotal = unitTotal.setScale(2, RoundingMode.CEILING);
        return unitTotal;
    }
}
