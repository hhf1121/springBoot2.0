package com.hhf.controller;


import com.hhf.entity.Book;
import com.hhf.entity.MyMoney;
import com.hhf.mapper.BookMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springbootstart.service.TokenService;

import java.util.Optional;

@RestController
public class MoneyController {

    //自定义starter、中的service
//    @Autowired
//    private TokenService tokenService;

    //注入多参数对象
    @Autowired
    private MyMoney money;


    //注入jpa对象
    @Autowired
    private BookMapper bookMapper;

    @GetMapping("/moneyInfo")
    public String getInfo(){
        return "信息"+money.getDescription();
    }


    //操作jpa对象
    @PostMapping("/create")
    public Book createBook(@Param("name")String name,@Param("author") String author,@Param("count") Integer count){
        Book book=new Book();
        book.setAuthor(author);
        book.setName(name);
        book.setCount(count);
       return bookMapper.save(book);
    }

    @GetMapping("/findById/{id}")
    public Book findBook(@PathVariable("id")Long id){
        Optional<Book> book=  bookMapper.findById(id);
        if(book.isPresent()){
            return book.get();
        }
        return null;
    }

//    @GetMapping("/getStarter")
//    public String getStarter(){
//        return tokenService.getToken();
//    }

}

