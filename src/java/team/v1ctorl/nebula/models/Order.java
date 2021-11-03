/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team.v1ctorl.nebula.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.Date;
import java.util.List;

/**
 *
 * @author asus
 */
public class Order {
    private Long id;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date datetime;
    
    @JsonProperty(access = Access.WRITE_ONLY)
    private Long userID;
    
    private Float totalPrice;
    
    private List<ProductInAnOrder> productList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<ProductInAnOrder> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductInAnOrder> productList) {
        this.productList = productList;
    }
}
