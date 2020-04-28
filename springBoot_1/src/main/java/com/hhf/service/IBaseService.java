package com.hhf.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IBaseService {

    Map<String, Object> getDistrict(String code, String level);

    Map<String, Object> getSelectDistrictByLevel(String level) throws ExecutionException, InterruptedException;
}
