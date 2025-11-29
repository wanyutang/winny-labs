package com.systemweb.swagger.controller;

import com.systemweb.swagger.constant.MessageConst;
import com.systemweb.swagger.model.BaseResponse;
import com.systemweb.swagger.model.StatusReq;
import com.systemweb.swagger.validator.EnumNamePatternValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "狀態驗證 API")
@RestController
@RequestMapping("/api")
public class StatusController extends BaseController {

    @Operation(summary = "驗證狀態欄位是否為合法 Enum")
    @PostMapping("/validate")
    public BaseResponse validateStatus(@org.springframework.web.bind.annotation.RequestBody StatusReq req) {
        boolean valid = EnumNamePatternValidator.isValidEnumName(MessageConst.StatusEnum.class, req.getStatus());
        if (valid) {
            return success(null, req.getStatus());
        } else {
            return fail("狀態不合法: " + req.getStatus());
        }
    }

    @Operation(
        summary = "設定狀態，並驗證 Enum 值",
        requestBody = @RequestBody(
            description = "狀態請求物件",
            required = true,
            content = @Content(schema = @Schema(implementation = StatusReq.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "成功回應", content = @Content(schema = @Schema(example = "{\"success\":true,\"status\":\"ACTIVE\"}"))),
            @ApiResponse(responseCode = "400", description = "錯誤回應", content = @Content(schema = @Schema(example = "{\"success\":false,\"message\":\"status 欄位錯誤\"}")))
        }
    )
    @PostMapping("/status")
    public ResponseEntity<BaseResponse> setStatus(@org.springframework.web.bind.annotation.RequestBody StatusReq req) {
        String status = req.getStatus();
        if (status == null) {
            return ResponseEntity.badRequest().body(fail("status 欄位不可為空"));
        }
        boolean valid = EnumNamePatternValidator.isValidEnumName(MessageConst.StatusEnum.class, status);
        if (!valid) {
            return ResponseEntity.badRequest().body(fail("status 欄位錯誤"));
        }
        return ResponseEntity.ok(success(null, status));
    }
}
