package com.ahmadda.presentation;

import com.ahmadda.application.EventTemplateService;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.domain.EventTemplate;
import com.ahmadda.presentation.dto.TemplateCreateRequest;
import com.ahmadda.presentation.dto.TemplateCreateResponse;
import com.ahmadda.presentation.dto.TemplateResponse;
import com.ahmadda.presentation.dto.TemplateTitleResponse;
import com.ahmadda.presentation.resolver.AuthMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Tag(name = "Template", description = "템플릿 관련 API")
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final EventTemplateService eventTemplateService;

    @Operation(summary = "템플릿 생성", description = "로그인한 회원이 새 템플릿을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            schema = @Schema(implementation = TemplateCreateResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(examples = @ExampleObject(
                            value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Not Found",
                                      "status": 404,
                                      "detail": "존재하지 않는 회원입니다.",
                                      "instance": "/api/templates"
                                    }
                                    """
                    ))
            )
    })
    @PostMapping
    public ResponseEntity<TemplateCreateResponse> createTemplate(
            @AuthMember final LoginMember loginMember,
            @RequestBody @Valid final TemplateCreateRequest templateCreateRequest
    ) {
        EventTemplate eventTemplate = eventTemplateService.createTemplate(loginMember, templateCreateRequest);

        TemplateCreateResponse response = TemplateCreateResponse.from(eventTemplate);

        return ResponseEntity.created(URI.create("/api/eventTemplates/" + eventTemplate.getId()))
                .body(response);
    }

    @Operation(summary = "내 템플릿 목록 조회", description = "로그인한 회원이 소유한 모든 템플릿의 제목 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = TemplateTitleResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 회원입니다.",
                                              "instance": "/api/templates"
                                            }
                                            """
                            ))
            )
    })
    @GetMapping
    public ResponseEntity<List<TemplateTitleResponse>> getMyTemplates(@AuthMember final LoginMember loginMember) {
        List<EventTemplate> eventTemplates = eventTemplateService.getTemplates(loginMember);

        List<TemplateTitleResponse> responses = eventTemplates.stream()
                .map(TemplateTitleResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "내 템플릿 단건 조회", description = "로그인한 회원이 소유한 템플릿을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TemplateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Forbidden",
                                              "status": 403,
                                              "detail": "본인이 작성한 템플릿이 아닙니다.",
                                              "instance": "/api/templates/{templateId}"
                                            }
                                            """
                            ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "회원 없음",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 회원입니다.",
                                              "instance": "/api/templates/{templateId}"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "템플릿 없음",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 템플릿입니다.",
                                              "instance": "/api/templates/{templateId}"
                                            }
                                            """
                            )
                    })
            )
    })
    @GetMapping("/{templateId}")
    public ResponseEntity<TemplateResponse> getMyTemplate(
            @AuthMember final LoginMember loginMember,
            @PathVariable final Long templateId
    ) {
        EventTemplate eventTemplate = eventTemplateService.getTemplate(loginMember, templateId);

        TemplateResponse response = TemplateResponse.from(eventTemplate);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "템플릿 삭제", description = "로그인한 회원이 소유한 템플릿을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204"
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(examples = @ExampleObject(
                            value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Forbidden",
                                      "status": 403,
                                      "detail": "본인이 작성한 템플릿이 아닙니다.",
                                      "instance": "/api/templates/{templateId}"
                                    }
                                    """
                    ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "회원 없음",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 회원입니다.",
                                              "instance": "/api/templates/{templateId}"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "템플릿 없음",
                                    value = """
                                            {
                                              "type": "about:blank",
                                              "title": "Not Found",
                                              "status": 404,
                                              "detail": "존재하지 않는 템플릿입니다.",
                                              "instance": "/api/templates/{templateId}"
                                            }
                                            """
                            )
                    })
            )
    })
    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(
            @AuthMember final LoginMember loginMember,
            @PathVariable final Long templateId
    ) {
        eventTemplateService.deleteTemplate(loginMember, templateId);

        return ResponseEntity.noContent()
                .build();
    }
}
