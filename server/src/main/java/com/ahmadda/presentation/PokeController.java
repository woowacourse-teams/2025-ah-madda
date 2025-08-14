package com.ahmadda.presentation;

import com.ahmadda.application.PokeService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.presentation.dto.PokeRequest;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Poke", description = "콕찌르기 관련 API")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class PokeController {

    private final PokeService pokeService;

    @Operation(summary = "포키 발송", description = "포키를 통해 참여자가 특정 참여자에게 푸시 알림을 보낼 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Unauthorized",
                                              "status": 401,
                                              "detail": "유효하지 않은 인증 정보입니다.",
                                              "instance": "/api/events/{eventId}/notify-poke"
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
                                            name = "이벤트 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 이벤트입니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "조직원 없음",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Not Found",
                                                      "status": 404,
                                                      "detail": "존재하지 않는 조직원입니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),

            @ApiResponse(
                    responseCode = "422",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "포키 횟수 제한",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "투다님에게 너무 많은 포키를 보냈어요 🫠 30분 뒤에 찌를 수 있어요!",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "주최자에게 전송 불가",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "주최자에게 포키를 보낼 수 없습니다",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "셀프 포키 불가",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "스스로에게 포키를 보낼 수 없습니다",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "발신자가 조직원 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "포키를 보내려면 해당 조직에 참여하고 있어야 합니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "수신자가 조직원 아님",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "포키 대상이 해당 조직에 참여하고 있어야 합니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 참여한 조직원에게 전송 불가",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "이미 이벤트에 참여한 조직원에게 포키를 보낼 수 없습니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "알림 수신 거부",
                                            value = """
                                                    {
                                                      "type": "about:blank",
                                                      "title": "Unprocessable Entity",
                                                      "status": 422,
                                                      "detail": "알림을 받지 않는 조직원입니다.",
                                                      "instance": "/api/events/{eventId}/notify-poke"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/{eventId}/poke")
    public ResponseEntity<Void> poke(
            @PathVariable final Long eventId,
            @RequestBody @Valid final PokeRequest notifyPokeRequest,
            @AuthMember final LoginMember loginMember
    ) {
        pokeService.poke(eventId, notifyPokeRequest, loginMember);

        return ResponseEntity.ok()
                .build();
    }
}
