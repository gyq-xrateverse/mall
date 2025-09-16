import io.minio.*;

public class TestMinioPolicy {
    public static void main(String[] args) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9090")
                    .credentials("minioadmin", "minioadmin")
                    .build();

            String bucketName = "test-mall";

            // 检查存储桶是否存在
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (isExist) {
                System.out.println("存储桶 " + bucketName + " 已存在");

                // 设置公共读取策略
                String policy = String.format("""
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": {
                                    "AWS": "*"
                                },
                                "Action": [
                                    "s3:GetObject"
                                ],
                                "Resource": [
                                    "arn:aws:s3:::%s/*"
                                ]
                            }
                        ]
                    }
                    """, bucketName);

                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build());

                System.out.println("✅ 已为存储桶 " + bucketName + " 设置公共读取策略");

                // 验证策略设置
                String currentPolicy = minioClient.getBucketPolicy(GetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .build());
                System.out.println("当前策略: " + currentPolicy);

            } else {
                System.out.println("存储桶 " + bucketName + " 不存在");
            }

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}