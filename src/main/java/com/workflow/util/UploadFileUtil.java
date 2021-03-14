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
 * 文件上传工具
 * <p>
 * Title: workflowWeb <br>
 * Description: <br>
 * Copyright: eastcom Copyright (C) 2009 <br>
 *
 * @author <a href="mailto:liyoujun@eastcom-sw.com">李友均</a><br>
 * @version 1.0 <br>
 * @e-mail: liyoujun@eastcom-sw.com <br>
 * @creatdate 2015年5月10日 上午11:24:45 <br>
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
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bean != null) {
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
        }
        return bean;
    }
}
