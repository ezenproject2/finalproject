package com.ezen.books.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String uploadNotice(MultipartFile file) {
        // 공지사항에서 받을 파일
        UP_DIR = "D:\\project_workspace\\fileUpload\\notice\\";

        LocalDate date = LocalDate.now();
        String today = date.toString().replace("-", "/");

        File folders = new File(UP_DIR, today);
        if(!folders.exists()){
            folders.mkdirs();
        }

        // saveDir
        String saveDir = today;

        // fileName
        // 일반적으로 file.name이 경로를 포함하는 경우가 많다. 경로가 붙은 파일 이름의 끝 \\ 찾아서 그 뒤의 이름을 사용
        String originalFileName = file.getOriginalFilename();
        String onlyFileName = originalFileName.substring((originalFileName.lastIndexOf(File.separator)+1));

        // uuid
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();

        // 실제 파일 저장
        String fileName = uuidStr + "_" + onlyFileName;
        File storeFile = new File(folders, fileName);

        try {
            file.transferTo(storeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 저장한 경로
        String fileAddr = "notice/" + saveDir + "/" + fileName;
        log.info(">>>> 실제 경로 > {}", storeFile.getAbsoluteFile());
        return fileAddr;
    }

    // Content에서 file 주소 리스트로 추출
    public List<String> extractUuids(String content) {
        List<String> uuidList = new ArrayList<>();

        // img 태그에서 src 속성을 추출하는 정규 표현식
        Pattern pattern = Pattern.compile("<img[^>]*src=[\"']([^\"']+)[\"'][^>]*>");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            // 이미지 경로 추출
            String imagePath = matcher.group(1);

            // UUID만 추출
            String uuid = extractUuidFromPath(imagePath);

            if (uuid != null) {
                uuidList.add(uuid);
            }
        }

        return uuidList;
    }

    // 경로에서 UUID만 추출 (파일명에서 첫 번째 "_" 앞의 문자열)
    private String extractUuidFromPath(String imagePath) {
        // 마지막 '/' 이후부터 첫 번째 '_'까지의 문자열을 UUID로 추출
        String fileNamePart = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        String uuid = fileNamePart.split("_")[0];  // UUID 부분만 추출
        return uuid;
    }

}
