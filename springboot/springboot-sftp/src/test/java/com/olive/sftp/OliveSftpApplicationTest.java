package com.olive.sftp;

import com.olive.sftp.service.FileSystemService;
import com.olive.sftp.util.ZipUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dongtangqiang
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OliveSftpApplicationTest {

    @Resource
    private FileSystemService fileSystemService;

    @Test
    public void uploadFile() throws Exception {
        File file = new File("/Users/clare/Documents/drawio/test.drawio");
        InputStream inputStream = new FileInputStream(file);

        boolean uploadFile = fileSystemService.uploadFile("document/" + file.getName(), inputStream);
        if (uploadFile) {
            System.out.println("success.....");
        } else {
            System.out.println("failure.....");
        }

        inputStream.close();
    }

    @Test
    public void zipUpload() throws Exception {
        File file = new File("/Users/clare/Documents/drawio/test.drawio");
        List<File> fileList = new ArrayList<>();
        fileList.add(file);

        String zipFileName = "hello.zip";
        String zipPath = ZipUtil.makeZip(zipFileName, fileList);
        InputStream inputStream = new FileInputStream(zipPath);

        boolean uploadFile = fileSystemService.uploadFile("document/" + zipFileName, inputStream);
        if (uploadFile) {
            System.out.println("success.....");
        } else {
            System.out.println("failure.....");
        }

        inputStream.close();

    }

    @Test
    public void download() throws Exception {
        File file = fileSystemService.downloadFile("document/test.drawio");
        if (file == null) {
            throw new FileNotFoundException("File not found!");
        }
        System.out.println(file.getName());

        file.delete();
    }


    @Test
    public void deleteFile() throws Exception {
        System.out.println(fileSystemService.deleteFile("document/test.drawio"));
    }

}
