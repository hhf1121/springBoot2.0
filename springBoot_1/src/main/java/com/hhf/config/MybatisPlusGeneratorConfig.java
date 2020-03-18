//package com.hhf.config;
//
//import com.baomidou.mybatisplus.generator.AutoGenerator;
//import com.baomidou.mybatisplus.generator.InjectionConfig;
//import com.baomidou.mybatisplus.generator.config.ConstVal;
//import com.baomidou.mybatisplus.generator.config.TemplateConfig;
//import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
//import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
//import java.io.File;
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Map.Entry;
//
//public class MybatisPlusGeneratorConfig {
//
//    static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//
//    public static void main(String[] args) throws Exception {
//        String path="D:\\gitLocal\\springBoot2.0\\springBoot_1\\src\\main\\resources\\config\\generator\\mybatis-plus-generator.yaml";
//        GenerateConfig config = getConfig(path);
//        defaultTemplate(config);
//
//        AutoGenerator generator = new AutoGenerator();
//
//        generator.setGlobalConfig(config);
//        InjectionConfig injection = new InjectionConfig() {
//            @Override
//            public void initMap() {
//
//            }
//        };
//        generator.setCfg(injection);
//        generator.setDataSource(config.getDataSource());
//        generator.setStrategy(config.getStrategy());
//        generator.setTemplate(config.getTemplate());
//        generator.setPackageInfo(config.getPackageInfo());
//        generator.setConfig(config.newConfigBuilder(injection));
//
//        // set freemarker engine
//        generator.setTemplateEngine(new FreemarkerTemplateEngine());
//        // 切换Controller生成目录到xxx-ms中
//        changeControllerDir(generator, config);
//        generator.execute();
//    }
//
//    static GenerateConfig getConfig(String path) throws IOException {
//        File yamlFile = new File(path);
//        return objectMapper.readValue(yamlFile, GenerateConfig.class);
//    }
//
//    static void defaultTemplate(GenerateConfig config) {
//        TemplateConfig template = config.getTemplate();
//        template.setEntity("/template/entity.java");
//        template.setMapper("/template/mapper.java");
//        template.setXml("/template/mapper.xml");
//        template.setService("/template/service.java");
//        template.setServiceImpl("/template/serviceImpl.java");
//        template.setController("/template/controller.java");
//    }
//
//    static void changeControllerDir(AutoGenerator generator, GenerateConfig config) {
//        ConfigBuilder builder = generator.getConfig();
//        Map<String, String> pathInfo = builder.getPathInfo();
//        String path = pathInfo.get(ConstVal.CONTROLLER_PATH);
//        for (Map.Entry<String, String> entry : pathInfo.entrySet()) {
////            System.out.println(String.format("key=%s,val=%s", entry.getKey(), entry.getValue()));
//        }
//        if (isNotEmpty(path) && isNotEmpty(config.getControllerModule())) {
//            String newPath = String.format("../%s/%s", config.getControllerModule(), path);
//            pathInfo.put(ConstVal.CONTROLLER_PATH, newPath);
//        }
//        path = pathInfo.get(ConstVal.XML_PATH);
//        if (isNotEmpty(path)) {
//            pathInfo.put(ConstVal.XML_PATH, "src/main/resources/com/hhf/mapper");
//        }
//    }
//
//    static boolean isNotEmpty(String text) {
//        return text != null && !text.isEmpty();
//    }
//}
