package com.hhf.entity;

/**
 * book类，jpa操作对象
 */

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@Entity(name="book")//jpa对象的托管对象：book
public class Book {
    @Id//加id标注
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String name;
    private String author;
    private Integer count;
}
