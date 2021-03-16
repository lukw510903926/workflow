package com.workflow.util;

import com.workflow.entity.BizFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/16 22:55
 */
public class UploadFileUtil {
    /**
     * 将上传的文件保存到磁盘，文件目录为yyyyMM/dd/yyyyMMddHHmmssSSS
     *
     * @param file
     * @return
     */
    public static BizFile saveFile(MultipartFile file, String bizFileRootPath) {
        if (file == null || file.getSize() == 0) {
            return null;
        }
        Date date = new Date();
        String filePath2 = DateUtils.formatDate(date, "yyyyMM") + File.separator + DateUtils.formatDate(date, "dd") + File.separator;
        filePath2 = filePath2 + UUID.randomUUID().toString().replaceAll("-", "");
        String dp = file.getOriginalFilename();
        dp = dp.substring(dp.lastIndexOf(".") + 1);
        filePath2 = filePath2 + "." + dp;
        String filePath = bizFileRootPath + filePath2;
        File pFile = new File(filePath);
        pFile.getParentFile().mkdirs();
        BizFile bean = new BizFile();
        try {
            file.transferTo(pFile);
            bean.setCreateDate(date);
            bean.setPath(filePath2);
            bean.setName(file.getOriginalFilename());
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        //检测是否为图片
        try {
            Image image = ImageIO.read(pFile);
            if (image == null) {
                bean.setFileType("FILE");
            } else {
                bean.setFileType("IMAGE");
            }
        } catch (IOException ex) {
        }
        return bean;
    }
}
