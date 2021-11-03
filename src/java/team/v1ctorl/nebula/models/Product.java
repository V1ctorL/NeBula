/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.models;

/**
 *
 * @author asus
 */
public class Product {
    private Long id;
    private String name;
    private Float price;
    private Integer amountOfStock;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getAmountOfStock() {
        return amountOfStock;
    }

    public void setAmountOfStock(Integer amountOfStock) {
        this.amountOfStock = amountOfStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
