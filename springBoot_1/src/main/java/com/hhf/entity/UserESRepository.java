package com.hhf.entity;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserESRepository extends ElasticsearchRepository<UserES, Long> {

}
