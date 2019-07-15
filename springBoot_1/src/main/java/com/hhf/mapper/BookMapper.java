package com.hhf.mapper;

import com.hhf.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * mapper类继承jpa
 */

@Repository
public interface BookMapper extends JpaRepository<Book,Long> {

}
