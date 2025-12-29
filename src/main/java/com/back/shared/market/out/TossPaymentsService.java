package com.back.shared.market.out;

import com.back.global.exception.DomainException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
public class TossPaymentsService {

    private static final String TOSS_BASE_URL = "https://api.tosspayments.com";
    private static final String CONFIRM_PATH = "/v1/payments/confirm";

    private final RestClient tossRestClient;
    private final ObjectMapper objectMapper;

    @Value("${custom.market.toss.payments.secretKey}")
    private String tossSecretKey;

    public TossPaymentsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.tossRestClient = RestClient.builder()
                .baseUrl(TOSS_BASE_URL)
                .build();
    }

    @PostConstruct
    public void init() {
        if (tossSecretKey == null || tossSecretKey.isBlank()) {
            log.error("==================================================");
            log.error("🚨 [TossPayments] 시크릿 키가 로드되지 않았습니다!");
            log.error("👉 application.yml의 'custom.market.toss.payments.secretKey' 설정을 확인하세요.");
            log.error("==================================================");
        } else {
            // 보안상 키의 일부분만 마스킹해서 출력하여 확인
            String maskedKey = tossSecretKey.length() > 10
                    ? tossSecretKey.substring(0, 7) + "****"
                    : "****";
            log.info("✅ [TossPayments] 시크릿 키 로드 완료: {} (길이: {})", maskedKey, tossSecretKey.length());

            // 공백 실수 체크
            if (tossSecretKey.trim().length() != tossSecretKey.length()) {
                log.warn("⚠️ [TossPayments] 주의: 시크릿 키 앞뒤에 공백이 포함되어 있습니다!");
            }
        }
    }


    public Map<String, Object> confirmCardPayment(String paymentKey, String orderId, long amount) {
        TossPaymentsConfirmRequest requestBody = new TossPaymentsConfirmRequest(
                paymentKey,
                orderId,
                amount
        );

        try {
            ResponseEntity<Map> responseEntity = createConfirmRequest(requestBody)
                    .retrieve()
                    .toEntity(Map.class);

            int httpStatus = responseEntity.getStatusCode().value();
            Map<String, Object> responseBody = responseEntity.getBody();

            if (httpStatus != 200) {
                throw createDomainExceptionFromNon200(httpStatus, responseBody);
            }

            if (responseBody == null) {
                throw new DomainException("400-EMPTY_RESPONSE", "토스 결제 승인 응답 바디가 비었습니다.");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) responseBody;
            return casted;

        } catch (RestClientResponseException e) {
            throw createDomainExceptionFromHttpError(e);
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("400-TOSS_CALL_EXCEPTION", "토스 결제 승인 호출 중 예외: " + e.getMessage());
        }
    }

    private RestClient.RequestHeadersSpec<?> createConfirmRequest(TossPaymentsConfirmRequest requestBody) {
        return tossRestClient.post()
                .uri(CONFIRM_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBasicAuth(tossSecretKey, ""))
                .body(requestBody);
    }

    private DomainException createDomainExceptionFromNon200(int httpStatus, Map responseBody) {
        if (responseBody == null) {
            return new DomainException("400-HTTP_" + httpStatus, "토스 결제 승인 실패(응답 바디 없음), HTTP " + httpStatus);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseBody;

        String tossCode = extractStringOrDefault(body, "code", "HTTP_" + httpStatus);
        String tossMessage = extractStringOrDefault(body, "message", "토스 결제 승인 실패, HTTP " + httpStatus);

        return new DomainException("400-" + tossCode, tossMessage);
    }

    private DomainException createDomainExceptionFromHttpError(RestClientResponseException e) {
        int httpStatus = e.getStatusCode().value();
        String rawBody = e.getResponseBodyAsString(StandardCharsets.UTF_8);

        if (rawBody == null || rawBody.isBlank()) {
            return new DomainException("400-HTTP_" + httpStatus, "토스 결제 승인 실패(빈 바디), HTTP " + httpStatus);
        }

        try {
            Map<String, Object> errorBody = objectMapper.readValue(rawBody, new TypeReference<>() {
            });
            String tossCode = extractStringOrDefault(errorBody, "code", "HTTP_" + httpStatus);
            String tossMessage = extractStringOrDefault(errorBody, "message", "토스 결제 승인 실패, HTTP " + httpStatus);
            return new DomainException("400-" + tossCode, tossMessage);
        } catch (Exception parseFail) {
            return new DomainException("400-HTTP_" + httpStatus, "토스 결제 승인 실패, HTTP " + httpStatus + " / body=" + rawBody);
        }
    }

    private String extractStringOrDefault(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        if (value instanceof String s && !s.isBlank()) return s;
        return defaultValue;
    }

    public record TossPaymentsConfirmRequest(String paymentKey, String orderId, long amount) {
    }
}
