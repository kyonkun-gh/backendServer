package com.my.backend.server.controller.api.v1;

import com.my.backend.server.dto.ApiResponseDto;
import com.my.backend.server.dto.response.DnsResponseDto;
import com.my.backend.server.exception.InvalidParamException;
import com.my.backend.server.service.DnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/dns")
@CrossOrigin(origins = "*")
public class DnsController {

    @Autowired
    private DnsService dnsService;

    /**
     * Endpoint to resolve DNS records for a given domain.
     *
     * @param domain The domain to resolve.
     * @param type   The type of DNS record (optional).
     * @return ApiResponseDto containing the DNS records or an error message.
     */
    @GetMapping("/resolve")
    public ApiResponseDto<?> getDnsRecords(@RequestParam String domain,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) String method) {
        // Logic to retrieve DNS records for the given domain
        if ( method != null && !method.equals("doh") && !method.equals("dig")) {
            throw new InvalidParamException("method 只允許 doh 或 dig");
        }

        DnsResponseDto dnsResponse = dnsService.resolveDns(domain, type, method);

        ApiResponseDto<DnsResponseDto> response = new ApiResponseDto<>();
        response.setCode(10000);
        response.setMessage("Success");
        response.setData(dnsResponse);
        response.setTimestamp(new Date().toString());

        return response;
    }
}
