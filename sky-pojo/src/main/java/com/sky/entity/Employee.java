package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//serializable:告诉JVM：这个类的对象，可以做序列化、反序列化
//- 序列化：Java对象 → 字节数组（可以存文件、Redis、网络传输）
//- 反序列化：字节数组 → 恢复成Java对象最常见：
// 实体类Entity、POJO存入Redis的时候，必须实现Serializable
//Redis存Java对象底层需要序列化，如果不实现会直接报错。
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

}
