package com.my.backend.server.service.impl;

import com.my.backend.server.dto.request.CertificateRequestDto;
import com.my.backend.server.dto.response.CertificateResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class CertificateServiceImplTests {

    @Test
    void testParseCertificateValidCert() throws Exception {
        String validCert = "MIID4TCCA2egAwIBAgISBWhc5U62FJ31NuKHysvjOTbQMAoGCCqGSM49BAMDMDIxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MQswCQYDVQQDEwJFNjAeFw0yNTA2MDUxMjE0MjNaFw0yNTA5MDMxMjE0MjJaMBoxGDAWBgNVBAMTD2xldHNlbmNyeXB0Lm9yZzBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABBNGGYsmAtRWejpydkKInCkfgq8G9wYuPWRbDnPTfXm0OKUsetr8/DUrNOjVFLN6OUj1UeHu1PKGFWkk1qZEcImjggJzMIICbzAOBgNVHQ8BAf8EBAMCB4AwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFAyZcKTVgawi//VreJDwcpHoPqbuMB8GA1UdIwQYMBaAFJMnRpgDqVFojpjWxEJI2yO/WJTSMDIGCCsGAQUFBwEBBCYwJDAiBggrBgEFBQcwAoYWaHR0cDovL2U2LmkubGVuY3Iub3JnLzBvBgNVHREEaDBmgglsZW5jci5vcmeCD2xldHNlbmNyeXB0LmNvbYIPbGV0c2VuY3J5cHQub3Jngg13d3cubGVuY3Iub3JnghN3d3cubGV0c2VuY3J5cHQuY29tghN3d3cubGV0c2VuY3J5cHQub3JnMBMGA1UdIAQMMAowCAYGZ4EMAQIBMC0GA1UdHwQmMCQwIqAgoB6GHGh0dHA6Ly9lNi5jLmxlbmNyLm9yZy84MS5jcmwwggEFBgorBgEEAdZ5AgQCBIH2BIHzAPEAdwDd3Mo0ldfhFgXnlTL6x5/4PRxQ39sAOhQSdgosrLvIKgAAAZdAORpxAAAEAwBIMEYCIQDSY09yT+mnWWXlt3UvbdNT8ab9lqonivyiXKVq+P+cOwIhAJAfN0ry+/rm7HjF7MfOxmjzq5VHaNnS/n0A3UeKanQKAHYAzPsPaoVxCWX+lZtTzumyfCLphVwNl422qX5UwP5MDbAAAAGXQDkiHQAABAMARzBFAiEA6vlM7NPRjVxKSpz2P53BE8f9vNIHZdSKYYsItbxLSQQCIG/CNNp9yNpIO5Nc3dunHLj5ZGrMU4XFMtGTy66Wfr4hMAoGCCqGSM49BAMDA2gAMGUCMHNBoscUTIJ1ekggJMyH5qXvhSUyg4TLze+bZZbojFSh4TG1z+1GgIWFRK4JwkOYWQIxANvbA05DlQ7edb81WSDedyVb0wYwx5LfuokE/KULyToxZLGUf/2yy3LSrvWE/N4YCQ==";
        CertificateRequestDto request = new CertificateRequestDto(validCert);

        CertificateServiceImpl service = new CertificateServiceImpl();
        CertificateResponseDto response = service.parseCertificate(request);


        Assertions.assertEquals( "5685CE54EB6149DF536E287CACBE33936D0", response.getSerialNumber() );
        Assertions.assertEquals( "SHA384withECDSA", response.getSigAlgName() );

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH);
        Assertions.assertEquals( sdf.parse("Thu Jun 05 20:14:23 CST 2025"), response.getNotBefore() );
        Assertions.assertEquals( sdf.parse("Thu Sep 03 20:14:22 CST 2025"), response.getNotAfter() );
        Assertions.assertEquals( List.of("1.3.6.1.5.5.7.3.1", "1.3.6.1.5.5.7.3.2"), response.getExtendedKeyUsage() );

        List<List<Object>> san = List.of(
            List.of(2, "lencr.org"),
            List.of(2, "letsencrypt.com"),
            List.of(2, "letsencrypt.org"),
            List.of(2, "www.lencr.org"),
            List.of(2, "www.letsencrypt.com"),
            List.of(2, "www.letsencrypt.org")
        );
        Assertions.assertIterableEquals( san, response.getSubjectAlternativeNames() );

        boolean[] keyUsage = {true, false, false, false, false, false, false, false, false};
        Assertions.assertArrayEquals( keyUsage, response.getKeyUsage() );
    }

    @Test
    void testParseCertificateNotBase64() throws Exception {
        String notBase64 = "NotBase64EncodedString";
        CertificateRequestDto request = new CertificateRequestDto(notBase64);

        CertificateServiceImpl service = new CertificateServiceImpl();

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.parseCertificate(request);
        });
    }

    @Test
    void testParseCertificateNotCert() throws Exception {
        String notCert = "Tm90IGEgY2VydGlmaWNhdGU=";
        CertificateRequestDto request = new CertificateRequestDto(notCert);

        CertificateServiceImpl service = new CertificateServiceImpl();

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.parseCertificate(request);
        });
    }
}
