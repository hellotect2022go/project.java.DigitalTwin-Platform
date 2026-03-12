package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.sop.*;
import com.mpole.hdt.digitaltwin.service.SopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sop")
@RequiredArgsConstructor
@Slf4j
public class SopController {

    private final SopService sopService;

    /**
     * SOP 템플릿 목록 조회
     * GET /api/sop/templates
     */
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<SopTemplateResponse>>> getTemplates(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        List<SopTemplateResponse> templates = sopService.getTemplates(eventType, activeOnly);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", templates));
    }

    /**
     * SOP 템플릿 개별 조회
     * GET /api/sop/templates/{id}
     */
    @GetMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<SopTemplateResponse>> getTemplate(@PathVariable Long id) {
        SopTemplateResponse template = sopService.getTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", template));
    }

    /**
     * SOP 템플릿 상세 조회 (Step/Item 포함)
     * GET /api/sop/templates/{id}/detail
     */
    @GetMapping("/templates/{id}/detail")
    public ResponseEntity<ApiResponse<SopTemplateDetailResponse>> getTemplateDetail(@PathVariable Long id) {
        SopTemplateDetailResponse template = sopService.getTemplateDetail(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", template));
    }

    /**
     * SOP 템플릿 생성
     * POST /api/sop/templates
     */
    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<SopTemplateResponse>> createTemplate(
            @RequestBody SopTemplateRequest request) {
        SopTemplateResponse template = sopService.createTemplate(request);
        return ResponseEntity.ok(ApiResponse.success("생성 성공", template));
    }

    /**
     * SOP 템플릿 수정
     * PUT /api/sop/templates/{id}
     */
    @PutMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<SopTemplateResponse>> updateTemplate(
            @PathVariable Long id,
            @RequestBody SopTemplateRequest request) {
        SopTemplateResponse template = sopService.updateTemplate(id, request);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", template));
    }

    /**
     * SOP Step 목록 조회
     * GET /api/sop/templates/{templateId}/steps
     */
    @GetMapping("/templates/{templateId}/steps")
    public ResponseEntity<ApiResponse<List<SopStepResponse>>> getSteps(
            @PathVariable Long templateId) {
        List<SopStepResponse> steps = sopService.getStepsByTemplate(templateId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", steps));
    }

    /**
     * SOP Step 생성
     * POST /api/sop/steps
     */
    @PostMapping("/steps")
    public ResponseEntity<ApiResponse<SopStepResponse>> createStep(
            @RequestBody SopStepRequest request) {
        SopStepResponse step = sopService.createStep(request);
        return ResponseEntity.ok(ApiResponse.success("생성 성공", step));
    }

    /**
     * SOP Step 수정
     * PUT /api/sop/steps/{id}
     */
    @PutMapping("/steps/{id}")
    public ResponseEntity<ApiResponse<SopStepResponse>> updateStep(
            @PathVariable Long id,
            @RequestBody SopStepRequest request) {
        SopStepResponse step = sopService.updateStep(id, request);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", step));
    }

    /**
     * SOP Step 삭제
     * DELETE /api/sop/steps/{id}
     */
    @DeleteMapping("/steps/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStep(@PathVariable Long id) {
        sopService.deleteStep(id);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }

    /**
     * SOP Item 목록 조회
     * GET /api/sop/steps/{stepId}/items
     */
    @GetMapping("/steps/{stepId}/items")
    public ResponseEntity<ApiResponse<List<SopItemResponse>>> getItems(
            @PathVariable Long stepId) {
        List<SopItemResponse> items = sopService.getItemsByStep(stepId);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", items));
    }

    /**
     * SOP Item 생성
     * POST /api/sop/items
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<SopItemResponse>> createItem(
            @RequestBody SopItemRequest request) {
        SopItemResponse item = sopService.createItem(request);
        return ResponseEntity.ok(ApiResponse.success("생성 성공", item));
    }

    /**
     * SOP Item 수정
     * PUT /api/sop/items/{id}
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<SopItemResponse>> updateItem(
            @PathVariable Long id,
            @RequestBody SopItemRequest request) {
        SopItemResponse item = sopService.updateItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", item));
    }

    /**
     * SOP Item 삭제
     * DELETE /api/sop/items/{id}
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        sopService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }


    /**
     * SOP 인스턴스들 조회
     * GET /api/sop/instances
     */
    @GetMapping("/instances")
    public ResponseEntity<ApiResponse<List<SopInstanceResponse>>> getInstances() {
        List<SopInstanceResponse> instances = sopService.getInstances();

        return ResponseEntity.ok(ApiResponse.success("조회 성공", instances));
    }

    /**
     * SOP 인스턴스 생성
     * POST /api/sop/instances
     */
    @PostMapping("/instances")
    public ResponseEntity<ApiResponse<SopInstanceResponse>> createInstance(
            @RequestBody SopInstanceCreateRequest request) {
        SopInstanceResponse instance = sopService.createInstance(request);
        return ResponseEntity.ok(ApiResponse.success("생성 성공", instance));
    }

    /**
     * SOP 인스턴스 조회
     * GET /api/sop/instances/{id}
     */
    @GetMapping("/instances/{id}")
    public ResponseEntity<ApiResponse<SopInstanceResponse>> getInstance(@PathVariable Long id) {
        SopInstanceResponse instance = sopService.getInstance(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", instance));
    }

    /**
     * 🚀 SOP 인스턴스 전체 정보 조회 (최적화 API)
     * GET /api/sop/instances/{id}/full
     * 
     * 한 번의 호출로 다음을 모두 제공:
     * - 인스턴스 정보
     * - 템플릿 구조 (Steps + Items + Options)
     * - 현재 선택 결과
     * - Step 활성화 상태 (자동 계산)
     * - 완료 가능 여부
     */
    @GetMapping("/instances/{id}/full")
    public ResponseEntity<ApiResponse<SopInstanceFullResponse>> getInstanceFull(@PathVariable Long id) {
        SopInstanceFullResponse fullData = sopService.getInstanceFull(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", fullData));
    }

    /**
     * 🚀 SOP Item 결과 일괄 저장 (최적화 API)
     * PUT /api/sop/instances/{instanceId}/batch
     * 
     * 여러 Item 결과를 한 번에 저장하고 자동으로 재평가
     * 응답으로 전체 정보(상태 포함)를 반환
     */
    @PutMapping("/instances/{instanceId}/batch")
    public ResponseEntity<ApiResponse<SopInstanceFullResponse>> saveItemResultsBatch(
            @PathVariable Long instanceId,
            @RequestBody SopItemResultBatchRequest request) {
        SopInstanceFullResponse fullData = sopService.saveItemResultsBatch(instanceId, request);
        return ResponseEntity.ok(ApiResponse.success("저장 및 재평가 성공", fullData));
    }

    /**
     * SOP Instance 완료 처리
     * POST /api/sop/instances/{instanceId}/complete
     */
    @PostMapping("/instances/{instanceId}/complete")
    public ResponseEntity<ApiResponse<SopInstanceResponse>> completeInstance(
            @PathVariable Long instanceId) {
        SopInstanceResponse instance = sopService.completeInstance(instanceId);
        return ResponseEntity.ok(ApiResponse.success("SOP 완료 처리 성공", instance));
    }

    // ========== Rule CRUD ==========

    /**
     * SOP Rule 목록 조회
     * GET /api/sop/rules
     */
    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<SopRuleResponse>>> getRules(
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) String ruleType) {
        List<SopRuleResponse> rules = sopService.getRules(templateId, ruleType);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", rules));
    }

    /**
     * SOP Rule 상세 조회
     * GET /api/sop/rules/{id}
     */
    @GetMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<SopRuleResponse>> getRule(@PathVariable Long id) {
        SopRuleResponse rule = sopService.getRule(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", rule));
    }

    /**
     * SOP Rule 생성
     * POST /api/sop/rules
     */
    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<SopRuleResponse>> createRule(
            @RequestBody SopRuleRequest request) {
        SopRuleResponse rule = sopService.createRule(request);
        return ResponseEntity.ok(ApiResponse.success("생성 성공", rule));
    }

    /**
     * SOP Rule 수정
     * PUT /api/sop/rules/{id}
     */
    @PutMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<SopRuleResponse>> updateRule(
            @PathVariable Long id,
            @RequestBody SopRuleRequest request) {
        SopRuleResponse rule = sopService.updateRule(id, request);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", rule));
    }

    /**
     * SOP Rule 삭제
     * DELETE /api/sop/rules/{id}
     */
    @DeleteMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        sopService.deleteRule(id);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }
}
