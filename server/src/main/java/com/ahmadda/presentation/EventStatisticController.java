package com.ahmadda.presentation;

import com.ahmadda.application.EventStatisticService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.EventViewMetric;
import com.ahmadda.presentation.dto.EventViewMetricResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Event Statistic", description = "이벤트 통계 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/{eventId}/statistic")
public class EventStatisticController {

    private final EventStatisticService eventStatisticService;

    @Operation(summary = "이벤트 통계 조회", description = "특정 이벤트의 방문 통계를 조회합니다. 주최자만 조회가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventViewMetricResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보 입니다.",
                                              "instance": "/api/events/{eventId}/statistic"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "이벤트 주최자가 아님",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Forbidden",
                                              "status": 403,
                                              "detail": "이벤트의 주최자만 통계를 조회할 수 있습니다.",
                                              "instance": "/api/events/{eventId}/statistic"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 조직원",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 조직원입니다.",
                                                      "instance": "/api/events/{eventId}/statistic"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 이벤트",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다",
                                                      "instance": "/api/events/{eventId}/statistic"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 이벤트 조회수 정보",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "요청한 이벤트 조회수 정보가 존재하지 않습니다.",
                                                      "instance": "/api/events/{eventId}/statistic"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<EventViewMetricResponse>> eventStatisticResponses(
            @PathVariable final Long eventId,
            @AuthMember final LoginMember loginMember
    ) {
        List<EventViewMetric> metrics = eventStatisticService.getEventStatistic(eventId, loginMember);
        List<EventViewMetricResponse> metricResponses = metrics.stream()
                .map(EventViewMetricResponse::from)
                .toList();

        return ResponseEntity.ok(metricResponses);
    }
}
