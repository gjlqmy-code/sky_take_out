package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController//Controller和requestbody等的合体
@RequestMapping("/admin/common")
@Api(tags="通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){//file与前端保持一致，这是用来接收前段上传文件的对象
        log.info("文件上传：{}",file);

        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀                             从这个位置开始截，截到最后
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名，使用UUID保证唯一性
            String objectName = UUID.randomUUID().toString() + extension;//防止文件名冲突用uuid(通用唯一识别码，一串全世界几乎动不会重复的字符串，并与文件后缀拼接)重命名

            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
