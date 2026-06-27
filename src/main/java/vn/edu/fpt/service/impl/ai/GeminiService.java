package vn.edu.fpt.service.impl.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.fpt.modelview.response.homepage.RecommendationDTO;
import vn.edu.fpt.modelview.response.homepage.UserRecommendationProfile;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String SYSTEM_CONTEXT = """
        Bạn là module gợi ý sự kiện trong hệ thống EventHub.
        Nhiệm vụ duy nhất: phân tích hồ sơ sở thích của user và viết lý do \
        gợi ý ngắn gọn bằng tiếng Việt cho từng sự kiện được cung cấp.
        """;

    private static final String PROMPT_TEMPLATE = """
        === HỒ SƠ SỞ THÍCH USER ===
        Thành phố hiện tại: %s
        Thể loại quan tâm: %s
        Các sự kiện đã từng tham dự:
        %s

        === DANH SÁCH SỰ KIỆN CẦN ĐÁNH GIÁ ===
        %s

        === NGUYÊN TẮC VIẾT LÝ DO ===
        1. Dựa vào tên sự kiện đã tham dự để chỉ ra điểm tương đồng cụ thể
           VD: "Tương tự Rock Night bạn từng đến, sự kiện này..."
        2. Nếu sự kiện cùng thành phố với user → ưu tiên đề cập
           VD: "Diễn ra ngay tại Hà Nội, thuận tiện cho bạn"
        3. Nếu khác thành phố → đề cập tự nhiên, không gượng ép
           VD: "Sự kiện đặc biệt tại TP.HCM dành cho tín đồ Jazz"
        4. KHÔNG bịa thêm thông tin không có trong input
        5. KHÔNG dùng câu mở đầu chung chung như "Đây là sự kiện tuyệt vời..."

        === QUY TẮC OUTPUT — ĐỌC KỸ TRƯỚC KHI TRẢ LỜI ===
        - Chọn ra ĐÚNG 5 sự kiện phù hợp nhất với user
        - Sắp xếp theo thứ tự ưu tiên giảm dần (phù hợp nhất ở vị trí đầu)
        - Số phần tử trong array PHẢI ĐÚNG BẰNG 5
        - Trả về JSON array DUY NHẤT
        - Array bắt đầu bằng ký tự "[" và kết thúc bằng ký tự "]"
        - Số phần tử trong array PHẢI BẰNG số sự kiện trong input (không bỏ sót, không thêm)
        - Mỗi phần tử gồm ĐÚNG 2 field:
            + "eventId": kiểu NUMBER (ví dụ: 5), KHÔNG phải string (không phải "5")
            + "reason" : chuỗi tiếng Việt, tối đa 100 từ, KHÔNG xuống dòng
        - TUYỆT ĐỐI KHÔNG có ký tự markdown (không có ```json, không có ```)
        - TUYỆT ĐỐI KHÔNG có bất kỳ text nào ngoài JSON array
          (không có "Dưới đây là:", "Kết quả:", "Sure!", hay bất kỳ lời giải thích nào)

        === VÍ DỤ MINH HỌA ===
        >> Input user:
        Thành phố: Hà Nội
        Thể loại: ["Concert","Âm nhạc"]
        Đã tham dự: [{"title":"Rock Night 2024","category":"Concert","city":"Hà Nội"}]
        >> Candidates:
        [
          {"eventId":5,"title":"Jazz & Rock Festival 2025","category":"Concert","date":"2025-08-10","city":"Hà Nội"},
          {"eventId":9,"title":"Classical Music Evening","category":"Âm nhạc","date":"2025-09-01","city":"TP.HCM"}
        ]
        >> Output ĐÚNG:
        [
          {"eventId":5,"reason":"Tương tự Rock Night bạn đã tham dự, festival này diễn ra ngay tại Hà Nội."},
          {"eventId":9,"reason":"Dành cho người yêu âm nhạc cổ điển như bạn, sự kiện đặc biệt tại TP.HCM."}
        ]
        >> Output SAI:
        [WRONG-1] ```json\\n[{"eventId":5,...}]```      → có markdown
        [WRONG-2] Dưới đây là kết quả: [{"eventId":5}]  → có text thừa
        [WRONG-3] [{"eventId":"5","reason":"..."}]      → eventId là string
        [WRONG-4] [{"eventId":5,"reason":"Đây là\\nsự kiện"}] → có newline

        Bây giờ hãy xử lý danh sách sự kiện ở trên và chỉ trả về JSON array:
        """;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public Map<Long, String> generateReasons(
            List<RecommendationDTO> candidateEvents,
            UserRecommendationProfile profile
    ) {
        log.info("Candidate events size = {}", candidateEvents.size());
        if(candidateEvents.isEmpty()) return Map.of();
        try {
            String filledPrompt = buildPrompt(candidateEvents, profile);

            Map<String, Object> requestBody = buildRequestBody(filledPrompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL + apiKey, new HttpEntity<>(requestBody, headers), Map.class);
            log.info(
                    "Gemini Response:\n{}",
                    objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(response.getBody())
            );
            return parseGeminiResponse(response.getBody());
        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage());
            return Map.of();
        }
    }



    private String buildPrompt(
            List<RecommendationDTO> candidateEvents,
            UserRecommendationProfile profile
    ) throws JsonProcessingException {
        String cityStr = profile.getUserCity() != null
                ? profile.getUserCity()
                : "Không xác định";

        String categoriesJson = objectMapper.writeValueAsString(profile.getPreferredCategories());

        String attendeeJson = objectMapper.writeValueAsString(profile.getAttendedEvents());

        String candidatesJson = buildCandidatesJson(candidateEvents);

        return PROMPT_TEMPLATE.formatted(cityStr, categoriesJson, attendeeJson, candidatesJson);
    }

    private String buildCandidatesJson(List<RecommendationDTO> candidates) {
        try {

            List<Map<String, Object>> list = candidates.stream()
                    .map(e -> Map.<String, Object>of(
                            "eventId", e.getEventId(),
                            "title", e.getTitle(),
                            "category", e.getCategoryName(),
                            "date", e.getDate().toString(),
                            "city", e.getCityName() != null ? e.getCityName() : "Không xác định"
                    )).toList();
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize candidates: {}", e.getMessage());
            return "[]";
        }
    }

    private Map<String, Object> buildRequestBody(String filledPrompt) {
        return Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", SYSTEM_CONTEXT))
                ),
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", filledPrompt)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.3,
                        "topP", 0.8,
                        "responseMimeType", "application/json"
                )
        );

    }



    @SuppressWarnings("unchecked")
    private Map<Long, String> parseGeminiResponse(Map<?, ?> responseBody) {
        try {
            List<Map<?, ?>> candidates = (List<Map<?, ?>>) responseBody.get("candidates");

            Map<?, ?> content = (Map<?, ?>) candidates.get(0).get("content");

            List<Map<?, ?>> parts = (List<Map<?, ?>>) content.get("parts");

            String rawText = (String) parts.get(0).get("text");

            String cleanedText = rawText
                    .replaceAll("(?s)```json\\s*|```", "")
                    .trim();
            log.info("========== RAW GEMINI ==========");
            log.info(rawText);
            log.info("================================");

            log.info("========== CLEANED ==========");
            log.info(cleanedText);
            log.info("=============================");
            List<Map<String, Object>> items = objectMapper.readValue(
                    cleanedText,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, Map.class)
            );

            Map<Long, String> result = new HashMap<>();
            for (Map<String, Object> item : items) {

                Long   eventId = ((Number) item.get("eventId")).longValue();
                String reason  = (String) item.get("reason");

                if (eventId != null && reason != null && !reason.isBlank()) {
                    result.put(eventId, reason);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", e.getMessage());
            return Map.of();
        }
    }
}
