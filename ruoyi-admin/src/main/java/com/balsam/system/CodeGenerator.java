package com.balsam.system;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import com.balsam.system.common.core.domain.BaseEntity;

import java.nio.file.Paths;
import java.sql.Types;


public class CodeGenerator {

    //配置参考文档  https://mybatis.plus/config/generator-config.html   https://mybatis.plus/guide/crud-interface.html
    // 请修改为本地数据库连接信息
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ry?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&useAffectedRows=true&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=false";
        String username = "root";
        String password = "password";
        FastAutoGenerator.create(url, username, password)
                .dataSourceConfig(builder -> builder.databaseQueryClass(SQLQuery.class)
                        .typeConvert(new MySqlTypeConvert())
                        .dbQuery(new MySqlQuery())
                        .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
            if (typeCode == Types.SMALLINT) {
                // 自定义类型转换
                return DbColumnType.INTEGER;
            }
            return typeRegistry.getColumnType(metaInfo);
        })).globalConfig(builder -> builder
                        // 设置作者
                        .author("ruoyi")
                        // 输出目录
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                        .commentDate("yyyy-MM-dd HH:mm:ss").dateType((DateType.ONLY_DATE))
                        .enableSwagger()
                )
                .packageConfig(builder -> builder
                        // 设置父包名
                        .parent("com.balsam.system")
                        // 设置实体类包名
                        .entity("domain")
                        // 设置 Mapper 接口包名
                        .mapper("mapper")
                        // 设置 Service 接口包名
                        .service("service")
                        // 设置 Service 实现类包名
                        .serviceImpl("service.impl")
                        // 设置 Mapper XML 文件包名
                        .xml("mapper.xml")
                )
                .strategyConfig( builder  -> builder
                        .addInclude() // 设置需要生成的表名，如 addInclude("sys_user", "sys_role")
                        .entityBuilder()
                        .enableLombok()//添加Lombok注解
                        .enableTableFieldAnnotation()// 启用字段注解
                        .superClass(BaseEntity.class)
                        //.idType(IdType.ASSIGN_ID)
                        .addTableFills(new Column("create_time", FieldFill.INSERT),
                                new Column("update_time",FieldFill.INSERT_UPDATE))
                        .enableFileOverride()
                        //逻辑删除字段,业务表按需求添加
                        // .logicDeleteColumnName()
                        //不生成controller层
                        .mapperBuilder().enableBaseResultMap()
                ).templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
