package com.example.serviceback.controller;

import com.example.serviceback.exception.MyErrorEnum;
import com.example.serviceback.exception.MyException;
import com.example.serviceback.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author CJW
 * @since 2024/3/26
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${file.baseDir}")
    private String dir;

    @PostMapping("/upload")
    public Result<List<String>> upload(@RequestParam MultipartFile[] files) throws IOException {
        if (files.length == 0) {
            throw new MyException(MyErrorEnum.FILE_EMPTY);
        }
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = upload(file);
            fileNames.add(fileName);
        }
        return Result.success(fileNames);
    }

    @GetMapping("/download/{fileName}")
    public Result<Void> download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        if (fileName == null || fileName.length() == 0) {
            throw new MyException(MyErrorEnum.FILE_EMPTY);
        }
        File file = new File(dir + fileName);
        //设置文件名
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        //创建输入流
        FileInputStream inputStream = new FileInputStream(file);
        BufferedInputStream buffInputStream = new BufferedInputStream(inputStream);
        //创建输出流
        ServletOutputStream outputStream = response.getOutputStream();
        BufferedOutputStream buffOutputStream = new BufferedOutputStream(outputStream);
        //循环读取数据并写入到响应输出流中
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = buffInputStream.read(buffer)) != -1) {
            buffOutputStream.write(buffer, 0, len);
        }
        //关闭流
        buffOutputStream.flush();
        buffOutputStream.close();
        outputStream.flush();
        outputStream.close();
        buffInputStream.close();
        inputStream.close();
        return Result.success();
    }

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 数据库存储的文件名称
     * @throws IOException
     */
    public String upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new MyException(MyErrorEnum.FILE_EMPTY);
        }
        //获取文件名后缀并修改全局唯一文件名称
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        File dest = new File(dir + fileName);
        //检查目录是否存在
        if (!dest.exists()) {
            dest.mkdirs();
        }
        file.transferTo(dest);
        return fileName;
    }

    public void deleteFile(String fileName) {
        File file = new File(dir + fileName);
        file.delete();
    }
}
