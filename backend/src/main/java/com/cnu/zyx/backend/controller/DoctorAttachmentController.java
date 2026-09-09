package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.entity.DoctorAttachment;
import com.cnu.zyx.backend.service.DoctorAttachmentService;
import com.cnu.zyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctorAttachment")
public class DoctorAttachmentController {

    @Autowired
    private DoctorAttachmentService doctorAttachmentService;

    // 文件上传接口，返回访问地址
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }
        // 固定路径：D:/code-project/smartdiag_upload/
        String basePath = "D:/code-project/smartdiag_upload/";
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成唯一文件名，防止重名覆盖
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + suffix;
        File saveFile = new File(basePath, newFileName);
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("上传失败");
        }
        // 返回浏览器可访问url
        String url = "/upload/" + newFileName;
        return Result.success(url);
    }

    // 保存附件记录，绑定医生资质id
    @PostMapping("/save")
    public Result<Integer> save(@RequestBody DoctorAttachment attachment) {
        int res = doctorAttachmentService.addAttachment(attachment);
        return Result.success(res);
    }

    // 根据doctorId查询全部附件列表
    @GetMapping("/list/{doctorId}")
    public Result<List<DoctorAttachment>> list(@PathVariable Long doctorId) {
        List<DoctorAttachment> list = doctorAttachmentService.getListByDoctorId(doctorId);
        return Result.success(list);
    }

    // 删除单条附件
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id) {
        int res = doctorAttachmentService.removeAttachmentById(id);
        return Result.success(res);
    }

    // 删除该资质全部附件
    @DeleteMapping("/all/{doctorId}")
    public Result<Integer> deleteAll(@PathVariable Long doctorId) {
        int res = doctorAttachmentService.removeAllByDoctorId(doctorId);
        return Result.success(res);
    }
}