package com.tools.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器。
 * <p>
 * 配合实体类上的 {@code @TableField(fill = FieldFill.INSERT)} 等注解，
 * 在插入和更新时自动填充时间字段，无需在业务代码中手动设置。
 * <p>
 * 使用 {@code strictInsertFill} / {@code strictUpdateFill} 而非直接 set 值：
 * 只有当目标字段为 null 时才会填充，避免覆盖业务代码显式设置的值。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充 createdAt 和 updatedAt 为当前时间。
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新时自动填充 updatedAt 为当前时间。
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
