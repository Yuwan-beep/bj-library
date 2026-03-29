package org.csu.bj.controller;

import lombok.extern.slf4j.Slf4j;
import org.csu.bj.api.vo.Result;
import org.csu.bj.utils.TencentCOSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import net.coobird.thumbnailator.Thumbnails;


@Slf4j
@RestController
public class UploadController {
    private int MAX_FILE_SIZE = 1024*1024;
    //依赖注入
    @Autowired
    private TencentCOSUtil tencentCOSUtil;
    @CrossOrigin(allowCredentials = "true")
    @PostMapping("api/upload")
    public Result upload(MultipartFile image) {
        try {
            log.info("正在上传，文件名{}", image.getOriginalFilename());

            // Check if the image needs compression (size > 1MB)
            if (image.getSize() > 1024 * 1024) {
                log.info("Compressing image as size exceeds 1MB");
                image = compressImage(image);
            }

            String url = tencentCOSUtil.upLoadFile(image);
            log.info("文件的Url：{}", url);

            return Result.success(url, "上传成功！");
        } catch (IOException e) {
            log.error("Image upload or compression failed", e);
            return Result.failed("上传失败！");
        }

    }

    private MultipartFile compressImage(MultipartFile originalFile) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Thumbnails.of(originalFile.getInputStream())
                    .scale(1.0)
                    .outputFormat("jpg")  // 强制转换为JPEG格式
                    .outputQuality(0.7)   // 初始质量设为70%
                    .toOutputStream(outputStream);

            byte[] compressedBytes = outputStream.toByteArray();

            // 如果仍然太大，逐步降低质量
            int attempts = 0;
            while (compressedBytes.length > MAX_FILE_SIZE && attempts < 3) {
                outputStream.reset();
                double quality = 0.7 / (attempts + 1);  // 每次降低质量
                Thumbnails.of(new ByteArrayInputStream(compressedBytes))
                        .scale(1.0)
                        .outputQuality(quality)
                        .toOutputStream(outputStream);
                compressedBytes = outputStream.toByteArray();
                attempts++;
            }

            return new CompressedMultipartFile(
                    originalFile.getName(),
                    originalFile.getOriginalFilename().replaceAll("\\.[^.]+$", ".jpg"), // 确保扩展名为.jpg
                    "image/jpeg",
                    compressedBytes
            );
        } catch (IOException e) {
            throw new IOException("图片压缩失败: " + e.getMessage(), e);
        }
    }

    // Simple implementation of MultipartFile for the compressed image
    private static class CompressedMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public CompressedMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            Files.write(dest.toPath(), content);
        }
    }
}
