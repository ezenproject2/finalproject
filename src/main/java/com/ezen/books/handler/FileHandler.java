package com.ezen.books.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
public class FileHandler {

    private String UP_DIR = "D:\\project_workspace\\fileUpload\\";

    public String uploadReview(MultipartFile file) {
        // 리뷰 파일 저장하고 경로 반환함
        log.info(">>>> file.size > {}", file.getSize());
        UP_DIR = "D:\\project_workspace\\fileUpload\\review\\";

        LocalDate date = LocalDate.now();
        // 2024-11-15를 2024\\11\\15로
        String today = date.toString().replace("-", "/");

        File folders = new File(UP_DIR, today);
        if(!folders.exists()){
            // **월 **일의 폴더가 없다면 생성하자
            folders.mkdirs();
        }

        // saveDir
        String saveDir = today; //*

        // fileName
        // 일반적으로 file.name이 경로를 포함하는 경우가 많다. 경로가 붙은 파일 이름의 끝 \\ 찾아서 그 뒤의 이름을 사용
        String originalFileName = file.getOriginalFilename();
        String onlyFileName = originalFileName.substring((originalFileName.lastIndexOf(File.separator)+1)); //*

        // uuid
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString(); //*

        // 실제 파일 저장
        String fileName = uuidStr + "_" + onlyFileName;
        File storeFile = new File(folders, fileName);

        try {
            file.transferTo(storeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 저장한 경로
        String fileAddr = "review/" + saveDir + "/" + fileName;
        log.info(">>>> 실제 경로 > {}", storeFile.getAbsoluteFile());
        return fileAddr;
    }

    public String uploadInquiry(MultipartFile file) {
        // 리뷰 파일 저장하고 경로 반환함
        log.info(">>>> file.size > {}", file.getSize());
        UP_DIR = "D:\\project_workspace\\fileUpload\\inquiry\\";

        LocalDate date = LocalDate.now();
        // 2024-11-15를 2024\\11\\15로
        String today = date.toString().replace("-", "/");

        File folders = new File(UP_DIR, today);
        if(!folders.exists()){
            // **월 **일의 폴더가 없다면 생성하자
            folders.mkdirs();
        }

        // saveDir
        String saveDir = today; //*

        // fileName
        // 일반적으로 file.name이 경로를 포함하는 경우가 많다. 경로가 붙은 파일 이름의 끝 \\ 찾아서 그 뒤의 이름을 사용
        String originalFileName = file.getOriginalFilename();
        String onlyFileName = originalFileName.substring((originalFileName.lastIndexOf(File.separator)+1)); //*

        // uuid
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString(); //*

        // 실제 파일 저장
        String fileName = uuidStr + "_" + onlyFileName;
        File storeFile = new File(folders, fileName);

        try {
            file.transferTo(storeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 저장한 경로
        String fileAddr = "inquiry/" + saveDir + "/" + fileName;
        log.info(">>>> 실제 경로 > {}", storeFile.getAbsoluteFile());
        return fileAddr;
    }

}
