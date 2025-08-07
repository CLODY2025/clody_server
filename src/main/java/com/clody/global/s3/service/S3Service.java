package kr.co.fitpass.fitpassbackendv2.infrastructure.aws.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.clody.global.s3.dto.S3UrlResponseDTO;
import com.clody.global.s3.exception.S3ErrorCode;
import com.clody.global.s3.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //파일 업로드(presigned url 사용 x)
    @Transactional
    public String uploadFile(MultipartFile file, String key) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        //s3에 파일 업로드
        amazonS3Client.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), metadata));

        return amazonS3Client.getUrl(bucket, key).toString();
    }


    // 파일 업로드(put)용 URL 생성하는 메소드
    public S3UrlResponseDTO getPutGeneratePresignedUrlRequest(String fileName, String domain) {
        String key = domain + "/" + UUID.randomUUID() + "/" + fileName;
        Date expiration = getExpiration();
        GeneratePresignedUrlRequest request
                = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        request.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());

        URL url = amazonS3Client.generatePresignedUrl(request);

        return S3UrlResponseDTO.builder()
                .preSignedUrl(url.toExternalForm())
                .key(key)
                .build();
    }

    // 파일 조회(get)용 URL 생성 메소드
    public S3UrlResponseDTO getGetGeneratePresignedUrlRequest(String key) {
        //key null 이면 url 발급 x
        if (key == null || key.trim().isEmpty()) {
            return S3UrlResponseDTO.builder()
                    .preSignedUrl(null)
                    .key(null)
                    .build();
        }
        Date expiration = getExpiration();
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = amazonS3Client.generatePresignedUrl(request);

        return S3UrlResponseDTO.builder()
                .preSignedUrl(url.toString())
                .key(key)
                .build();
    }

    // url 유효기간 생성
    private static Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 15; // 15분
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    //파일 삭제
    @Transactional
    public void deleteFile(String key) {
        if (key != null && !key.equals("none")) {
            if (amazonS3Client.doesObjectExist(bucket, key)) {
                amazonS3Client.deleteObject(bucket, key);
                log.info("Deleted file with key: " + key);
            } else {
                log.error("File not found in S3: " + key);
                throw new S3Exception(S3ErrorCode.NOT_FOUND);
            }

        } else {
            log.info("No picture to delete for key: " + key);
        }

    }

    //파일 다운로드
    public byte[] downloadFileFromS3(String key) {
        try {
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucket, key));
            try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
                return IOUtils.toByteArray(inputStream);
            }
        } catch (Exception e) {
            throw new S3Exception(S3ErrorCode.DOWNLOAD_FAILED);
        }
    }
}