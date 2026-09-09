package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.util.OssUtil;
import com.cnu.zyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private OssUtil ossUtil;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam("folder") String folder) {
        try {
            String url = ossUtil.upload(file, folder);
            return Result.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("文件上传失败");
        }
    }
}