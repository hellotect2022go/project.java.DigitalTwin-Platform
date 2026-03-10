package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.sop.*;
import com.mpole.hdt.digitaltwin.application.repository.sop.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SopService {

    private final SopTemplateRepository templateRepository;
    private final SopStepRepository stepRepository;
    private final SopItemRepository itemRepository;
    private final SopItemOptionRepository itemOptionRepository;
    private final SopRuleRepository ruleRepository;
    private final SopRuleConditionRepository ruleConditionRepository;
    private final SopRuleActionRepository ruleActionRepository;
    private final SopInstanceRepository instanceRepository;
    private final SopItemResultRepository itemResultRepository;
    private final SopStepStatusRepository stepStatusRepository;

    @Transactional(readOnly = true)
    public List<SopTemplateResponse> getTemplates(String eventType, Boolean activeOnly) {
        List<SopTemplate> templates;

        if (eventType != null && Boolean.TRUE.equals(activeOnly)) {
            templates = templateRepository.findByEventTypeAndIsActiveTrue(eventType);
        } else if (eventType != null) {
            templates = templateRepository.findByEventType(eventType);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            templates = templateRepository.findByIsActiveTrue();
        } else {
            templates = templateRepository.findAll();
        }

        return templates.stream()
                .map(this::toTemplateResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SopTemplateResponse getTemplate(Long id) {
        SopTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP 템플릿을 찾을 수 없습니다: " + id));
        return toTemplateResponse(template);
    }

    @Transactional
    public SopTemplateResponse createTemplate(SopTemplateRequest request) {
        SopTemplate template = SopTemplate.builder()
                .eventType(request.getEventType())
                .name(request.getName())
                .version(request.getVersion())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        template = templateRepository.save(template);
        log.info("SOP 템플릿 생성: id={}, eventType={}", template.getId(), template.getEventType());
        return toTemplateResponse(template);
    }

    @Transactional
    public SopTemplateResponse updateTemplate(Long id, SopTemplateRequest request) {
        SopTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP 템플릿을 찾을 수 없습니다: " + id));

        if (request.getEventType() != null) {
            template.setEventType(request.getEventType());
        }
        if (request.getName() != null) {
            template.setName(request.getName());
        }
        if (request.getVersion() != null) {
            template.setVersion(request.getVersion());
        }
        if (request.getIsActive() != null) {
            template.setIsActive(request.getIsActive());
        }

        template = templateRepository.save(template);
        log.info("SOP 템플릿 수정: id={}", template.getId());
        return toTemplateResponse(template);
    }

    @Transactional(readOnly = true)
    public SopTemplateDetailResponse getTemplateDetail(Long id) {
        SopTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP 템플릿을 찾을 수 없습니다: " + id));

        List<SopStep> steps = stepRepository.findByTemplate_IdOrderByStepOrderAsc(id);
        List<SopStepDetailResponse> stepResponses = steps.stream()
                .map(step -> SopStepDetailResponse.builder()
                        .id(step.getId())
                        .templateId(step.getTemplate().getId())
                        .stepOrder(step.getStepOrder())
                        .title(step.getTitle())
                        .description(step.getDescription())
                        .activationRuleId(step.getActivationRule() != null ? step.getActivationRule().getId() : null)
                        .completionRuleId(step.getCompletionRule() != null ? step.getCompletionRule().getId() : null)
                        .items(step.getItems().stream()
                                .map(this::toItemResponse)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return SopTemplateDetailResponse.builder()
                .id(template.getId())
                .eventType(template.getEventType())
                .name(template.getName())
                .version(template.getVersion())
                .isActive(template.getIsActive())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .steps(stepResponses)
                .build();
    }

    @Transactional
    public SopStepResponse createStep(SopStepRequest request) {
        SopTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("SOP 템플릿을 찾을 수 없습니다: " + request.getTemplateId()));

        SopRule activationRule = request.getActivationRuleId() != null
                ? ruleRepository.findById(request.getActivationRuleId())
                        .orElseThrow(() -> new IllegalArgumentException("SOP 룰을 찾을 수 없습니다: " + request.getActivationRuleId()))
                : null;

        SopRule completionRule = request.getCompletionRuleId() != null
                ? ruleRepository.findById(request.getCompletionRuleId())
                        .orElseThrow(() -> new IllegalArgumentException("SOP 룰을 찾을 수 없습니다: " + request.getCompletionRuleId()))
                : null;

        // stepOrder 검증: 무조건 1 이상
        Integer stepOrder = request.getStepOrder();
        if (stepOrder == null || stepOrder < 1) {
            stepOrder = 1;
            log.warn("stepOrder가 null이거나 0 이하입니다. 기본값 1로 설정합니다.");
        }

        SopStep step = SopStep.builder()
                .template(template)
                .stepOrder(stepOrder)
                .title(request.getTitle())
                .description(request.getDescription())
                .activationRule(activationRule)
                .completionRule(completionRule)
                .build();

        step = stepRepository.save(step);
        log.info("SOP Step 생성: id={}, templateId={}, stepOrder={}", step.getId(), template.getId(), step.getStepOrder());
        return toStepResponse(step);
    }

    @Transactional
    public SopStepResponse updateStep(Long id, SopStepRequest request) {
        SopStep step = stepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP Step을 찾을 수 없습니다: " + id));

        if (request.getStepOrder() != null) {
            // stepOrder 검증: 무조건 1 이상
            Integer stepOrder = request.getStepOrder();
            if (stepOrder < 1) {
                stepOrder = 1;
                log.warn("stepOrder가 0 이하입니다. 기본값 1로 설정합니다.");
            }
            step.setStepOrder(stepOrder);
        }
        if (request.getTitle() != null) {
            step.setTitle(request.getTitle());
        }
        step.setDescription(request.getDescription());

        if (request.getActivationRuleId() != null) {
            SopRule activationRule = ruleRepository.findById(request.getActivationRuleId())
                    .orElseThrow(() -> new IllegalArgumentException("SOP 룰을 찾을 수 없습니다: " + request.getActivationRuleId()));
            step.setActivationRule(activationRule);
        } else if (request.getActivationRuleId() == null) {
            step.setActivationRule(null);
        }

        if (request.getCompletionRuleId() != null) {
            SopRule completionRule = ruleRepository.findById(request.getCompletionRuleId())
                    .orElseThrow(() -> new IllegalArgumentException("SOP 룰을 찾을 수 없습니다: " + request.getCompletionRuleId()));
            step.setCompletionRule(completionRule);
        } else if (request.getCompletionRuleId() == null) {
            step.setCompletionRule(null);
        }

        step = stepRepository.save(step);
        log.info("SOP Step 수정: id={}", step.getId());
        return toStepResponse(step);
    }

    @Transactional
    public void deleteStep(Long id) {
        SopStep step = stepRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP Step을 찾을 수 없습니다: " + id));
        stepRepository.delete(step);
        log.info("SOP Step 삭제: id={}", id);
    }

    @Transactional
    public SopItemResponse createItem(SopItemRequest request) {
        SopStep step = stepRepository.findById(request.getStepId())
                .orElseThrow(() -> new IllegalArgumentException("SOP Step을 찾을 수 없습니다: " + request.getStepId()));

        SopRule activationRule = request.getActivationRuleId() != null
                ? ruleRepository.findById(request.getActivationRuleId())
                        .orElseThrow(() -> new IllegalArgumentException("SOP 룰을 찾을 수 없습니다: " + request.getActivationRuleId()))
                : null;

        // Type 검증
        String itemType = request.getItemType();
        if (!"SINGLE_BUTTON".equals(itemType) && !"MULTI_BUTTON".equals(itemType)) {
            itemType = "SINGLE_BUTTON"; // 기본값
        }

        // SINGLE_BUTTON일 때 actionLabel 기본값
        String actionLabel = request.getActionLabel();
        if ("SINGLE_BUTTON".equals(itemType) && (actionLabel == null || actionLabel.isBlank())) {
            actionLabel = "완료";
        }

        SopItem item = SopItem.builder()
                .step(step)
                .title(request.getTitle())
                .itemType(itemType)
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : false)
                .groupKey(request.getGroupKey())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .uiStyle(request.getUiStyle())
                .actionLabel(actionLabel)
                .activationRule(activationRule)
                .build();

        applyOptions(item, request.getOptions());
        item = itemRepository.save(item);
        log.info("SOP Item 생성: id={}, stepId={}, itemType={}", item.getId(), step.getId(), item.getItemType());
        return toItemResponse(item);
    }

    @Transactional
    public SopItemResponse updateItem(Long id, SopItemRequest request) {
        SopItem item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP Item을 찾을 수 없습니다: " + id));

        if (request.getTitle() != null) {
            item.setTitle(request.getTitle());
        }
        if (request.getItemType() != null) {
            // Type 검증
            String itemType = request.getItemType();
            if (!"SINGLE_BUTTON".equals(itemType) && !"MULTI_BUTTON".equals(itemType)) {
                itemType = "SINGLE_BUTTON";
            }
            item.setItemType(itemType);
        }
        if (request.getIsRequired() != null) {
            item.setIsRequired(request.getIsRequired());
        }
        item.setGroupKey(request.getGroupKey());
        if (request.getDisplayOrder() != null) {
            item.setDisplayOrder(request.getDisplayOrder());
        }
        item.setUiStyle(request.getUiStyle());
        
        // SINGLE_BUTTON일 때 actionLabel 기본값
        String actionLabel = request.getActionLabel();
        if ("SINGLE_BUTTON".equals(item.getItemType()) && (actionLabel == null || actionLabel.isBlank())) {
            actionLabel = "완료";
        }
        item.setActionLabel(actionLabel);

        if (request.getActivationRuleId() != null) {
            SopRule activationRule = ruleRepository.findById(request.getActivationRuleId())
                    .orElseThrow(() -> new IllegalArgumentException("SOP 룰을 찾을 수 없습니다: " + request.getActivationRuleId()));
            item.setActivationRule(activationRule);
        } else if (request.getActivationRuleId() == null) {
            item.setActivationRule(null);
        }

        if (request.getOptions() != null) {
            applyOptions(item, request.getOptions());
        }

        item = itemRepository.save(item);
        log.info("SOP Item 수정: id={}", item.getId());
        return toItemResponse(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        SopItem item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP Item을 찾을 수 없습니다: " + id));
        itemRepository.delete(item);
        log.info("SOP Item 삭제: id={}", id);
    }

    @Transactional(readOnly = true)
    public List<SopStepResponse> getStepsByTemplate(Long templateId) {
        return stepRepository.findByTemplate_IdOrderByStepOrderAsc(templateId).stream()
                .map(this::toStepResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SopItemResponse> getItemsByStep(Long stepId) {
        return itemRepository.findByStep_Id(stepId).stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    // ========== Rule CRUD ==========

    @Transactional(readOnly = true)
    public List<SopRuleResponse> getRules(Long templateId, String ruleType) {
        List<SopRule> rules;
        
        if (templateId != null && ruleType != null) {
            rules = ruleRepository.findByTemplate_IdAndRuleType(templateId, ruleType);
        } else if (templateId != null) {
            rules = ruleRepository.findByTemplate_Id(templateId);
        } else if (ruleType != null) {
            rules = ruleRepository.findByRuleType(ruleType);
        } else {
            rules = ruleRepository.findAll();
        }
        
        return rules.stream()
                .map(this::toRuleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SopRuleResponse getRule(Long id) {
        SopRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP Rule을 찾을 수 없습니다: " + id));
        return toRuleResponse(rule);
    }

    @Transactional
    public SopRuleResponse createRule(SopRuleRequest request) {
        SopTemplate template = null;
        if (request.getTemplateId() != null) {
            template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new IllegalArgumentException("SOP 템플릿을 찾을 수 없습니다: " + request.getTemplateId()));
        }
        
        SopRule rule = SopRule.builder()
                .template(template)
                .ruleType(request.getRuleType())
                .description(request.getDescription())
                .expression("") // deprecated, 빈 문자열
                .build();

        rule = ruleRepository.save(rule);

        // Conditions 생성
        if (request.getConditions() != null) {
            for (SopRuleConditionRequest condReq : request.getConditions()) {
                SopRuleCondition condition = SopRuleCondition.builder()
                        .rule(rule)
                        .itemId(condReq.getItemId())
                        .optionId(condReq.getOptionId())
                        .build();
                rule.getConditions().add(condition);
            }
        }

        // Actions 생성
        if (request.getActions() != null) {
            for (SopRuleActionRequest actReq : request.getActions()) {
                SopRuleAction action = SopRuleAction.builder()
                        .rule(rule)
                        .stepId(actReq.getStepId())
                        .build();
                rule.getActions().add(action);
            }
        }

        rule = ruleRepository.save(rule);
        log.info("SOP Rule 생성: id={}, templateId={}, ruleType={}", rule.getId(), template != null ? template.getId() : null, rule.getRuleType());
        return toRuleResponse(rule);
    }

    @Transactional
    public SopRuleResponse updateRule(Long id, SopRuleRequest request) {
        SopRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP Rule을 찾을 수 없습니다: " + id));

        if (request.getTemplateId() != null) {
            SopTemplate template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new IllegalArgumentException("SOP 템플릿을 찾을 수 없습니다: " + request.getTemplateId()));
            rule.setTemplate(template);
        }
        
        if (request.getRuleType() != null) {
            rule.setRuleType(request.getRuleType());
        }
        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
        }

        // Conditions 갱신
        if (request.getConditions() != null) {
            rule.getConditions().clear();
            for (SopRuleConditionRequest condReq : request.getConditions()) {
                SopRuleCondition condition = SopRuleCondition.builder()
                        .rule(rule)
                        .itemId(condReq.getItemId())
                        .optionId(condReq.getOptionId())
                        .build();
                rule.getConditions().add(condition);
            }
        }

        // Actions 갱신
        if (request.getActions() != null) {
            rule.getActions().clear();
            for (SopRuleActionRequest actReq : request.getActions()) {
                SopRuleAction action = SopRuleAction.builder()
                        .rule(rule)
                        .stepId(actReq.getStepId())
                        .build();
                rule.getActions().add(action);
            }
        }

        rule = ruleRepository.save(rule);
        log.info("SOP Rule 수정: id={}", rule.getId());
        return toRuleResponse(rule);
    }

    @Transactional
    public void deleteRule(Long id) {
        SopRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP Rule을 찾을 수 없습니다: " + id));
        ruleRepository.delete(rule);
        log.info("SOP Rule 삭제: id={}", id);
    }

    @Transactional
    public SopInstanceResponse createInstance(SopInstanceCreateRequest request) {
        SopTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("SOP 템플릿을 찾을 수 없습니다: " + request.getTemplateId()));

        SopInstance instance = SopInstance.builder()
                .eventId(request.getEventId())
                .template(template)
                .status("IN_PROGRESS")
                .startedAt(OffsetDateTime.now())
                .build();

        instance = instanceRepository.save(instance);
        log.info("SOP 인스턴스 생성: id={}, eventId={}", instance.getId(), instance.getEventId());
        return toInstanceResponse(instance);
    }

    @Transactional(readOnly = true)
    public List<SopInstanceResponse> getInstances() {
        List<SopInstance> instances = instanceRepository.findAll();

        return instances.stream().map(this::toInstanceResponse).toList();
    }


    @Transactional(readOnly = true)
    public SopInstanceResponse getInstance(Long id) {
        SopInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SOP 인스턴스를 찾을 수 없습니다: " + id));
        return toInstanceResponse(instance);
    }

    /**
     * 인스턴스 전체 정보 조회 (최적화된 단일 API)
     * - 인스턴스 정보
     * - 템플릿 구조 (Steps + Items)
     * - 현재 선택 결과
     * - Step 활성화 상태
     * - 완료 가능 여부
     */
    @Transactional
    public SopInstanceFullResponse getInstanceFull(Long instanceId) {
        // 1. 인스턴스 조회
        SopInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SOP 인스턴스를 찾을 수 없습니다: " + instanceId));

        SopTemplate template = instance.getTemplate();

        // 2. 템플릿의 Steps 조회 (Items 포함)
        List<SopStep> steps = stepRepository.findByTemplate_IdOrderByStepOrderAsc(template.getId());
        List<SopStepWithItemsResponse> stepResponses = steps.stream()
                .map(this::toStepWithItemsResponse)
                .collect(Collectors.toList());

//        // 3. 현재 선택 결과 조회
//        List<SopItemResultResponse> itemResults = itemResultRepository.findByInstance_Id(instanceId).stream()
//                .map(this::toItemResultResponse)
//                .collect(Collectors.toList());
//
//        // 4. Step 상태 평가 (자동 활성화 계산)
//        List<SopStepStatusResponse> stepStatuses = evaluateInstanceStatuses(instanceId);

        // 5. 완료 가능 여부 체크
        //boolean canComplete = canCompleteInstance(instanceId);

        return SopInstanceFullResponse.builder()
                .id(instance.getId())
                .eventId(instance.getEventId())
                .templateId(template.getId())
                .status(instance.getStatus())
                .startedAt(instance.getStartedAt())
                .completedAt(instance.getCompletedAt())
                .createdAt(instance.getCreatedAt())
                .updatedAt(instance.getUpdatedAt())
                .templateName(template.getName())
                .eventType(template.getEventType())
                .templateVersion(template.getVersion())
                .steps(stepResponses)
//                .itemResults(itemResults)
//                .stepStatuses(stepStatuses)
                //.canComplete(canComplete)
                .build();
    }

    /**
     * 여러 Item 결과를 일괄 저장 + 자동 재평가
     */
    @Transactional
    public SopInstanceFullResponse saveItemResultsBatch(Long instanceId, SopItemResultBatchRequest request) {
        SopInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SOP 인스턴스를 찾을 수 없습니다: " + instanceId));

        if (request.getResults() != null && !request.getResults().isEmpty()) {
            for (SopItemResultBatchRequest.ItemResultInput input : request.getResults()) {
                saveItemResultInternal(instance, input.getItemId(), input.getOptionId(), 
                        input.getSelected(), input.getSelectedBy());
            }
        }

        log.info("SOP Item 결과 일괄 저장: instanceId={}, count={}", 
                instanceId, request.getResults() != null ? request.getResults().size() : 0);

        // 저장 후 전체 정보 재조회 (상태 자동 반영)
        return getInstanceFull(instanceId);
    }

    /**
     * 단일 Item 결과 저장 (내부용)
     */
    private void saveItemResultInternal(SopInstance instance, Long itemId, Long optionId, 
                                       Boolean selected, String selectedBy) {
        SopItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("SOP Item을 찾을 수 없습니다: " + itemId));

        SopItemOption option = null;
        if (optionId != null) {
            option = itemOptionRepository.findById(optionId)
                    .orElseThrow(() -> new IllegalArgumentException("SOP Item 옵션을 찾을 수 없습니다: " + optionId));
        }

        SopItemResult result = itemResultRepository.findByInstance_IdAndItem_Id(instance.getId(), itemId)
                .orElseGet(() -> SopItemResult.builder()
                        .instance(instance)
                        .item(item)
                        .build());

        boolean isSelected = selected != null ? selected : false;
        result.setSelected(isSelected);
        result.setSelectedBy(selectedBy);
        result.setSelectedAt(OffsetDateTime.now());
        result.setOption(isSelected ? option : null);

        itemResultRepository.save(result);
    }

    @Transactional
    public SopItemResultResponse saveItemResult(Long instanceId, Long itemId, SopItemResultRequest request) {
        SopInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SOP 인스턴스를 찾을 수 없습니다: " + instanceId));
        SopItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("SOP Item을 찾을 수 없습니다: " + itemId));
        SopItemOption option = null;
        if (request.getOptionId() != null) {
            option = itemOptionRepository.findById(request.getOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("SOP Item 옵션을 찾을 수 없습니다: " + request.getOptionId()));
        }

        SopItemResult result = itemResultRepository.findByInstance_IdAndItem_Id(instanceId, itemId)
                .orElseGet(() -> SopItemResult.builder()
                        .instance(instance)
                        .item(item)
                        .build());

        boolean selected = request.getSelected() != null ? request.getSelected() : false;
        result.setSelected(selected);
        result.setSelectedBy(request.getSelectedBy());
        result.setSelectedAt(OffsetDateTime.now());
        result.setOption(selected ? option : null);

        result = itemResultRepository.save(result);
        log.info("SOP Item 결과 저장: instanceId={}, itemId={}, selected={}",
                instanceId, itemId, selected);
        return toItemResultResponse(result);
    }

    @Transactional(readOnly = true)
    public List<SopItemResultResponse> getItemResults(Long instanceId) {
        return itemResultRepository.findByInstance_Id(instanceId).stream()
                .map(this::toItemResultResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<SopStepStatusResponse> evaluateInstance(Long instanceId) {
        return evaluateInstanceStatuses(instanceId);
    }

    /**
     * 인스턴스의 Step 상태 평가 (내부/외부 공용)
     */
    @Transactional
    public List<SopStepStatusResponse> evaluateInstanceStatuses(Long instanceId) {
        SopInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SOP 인스턴스를 찾을 수 없습니다: " + instanceId));

        Long templateId = instance.getTemplate().getId();
        List<SopStep> steps = stepRepository.findByTemplate_IdOrderByStepOrderAsc(templateId);
        Map<Long, SopItemResult> resultMap = itemResultRepository.findByInstance_Id(instanceId)
                .stream()
                .collect(Collectors.toMap(r -> r.getItem().getId(), r -> r));

        // Step 활성화 로직
        // 1. Step 1은 항상 활성화
        // 2. activationRule이 없는 Step은 기본 활성화 (제약 없음)
        // 3. activationRule이 있는 Step은 조건부 활성화
        Set<Long> activeStepIds = new HashSet<>();
        
        for (SopStep step : steps) {
            Integer stepOrder = step.getStepOrder();
            log.debug("Step id={}, stepOrder={}, hasRule={}", 
                step.getId(), stepOrder, step.getActivationRule() != null);
            
            // Step 1은 무조건 활성화
            if (stepOrder != null && stepOrder == 1) {
                activeStepIds.add(step.getId());
                log.info("✅ Step 1 활성화: stepId={}", step.getId());
            }
            // activationRule이 없으면 기본 활성화 (제약 없음)
            else if (step.getActivationRule() == null) {
                activeStepIds.add(step.getId());
                log.info("✅ Step {} 기본 활성화 (Rule 없음): stepId={}", stepOrder, step.getId());
            }
        }

        // activationRule이 있는 Step들은 조건 평가
        for (SopStep step : steps) {
            SopRule rule = step.getActivationRule();
            if (rule != null) {
                if (evaluateRuleStructured(rule, resultMap)) {
                    Set<Long> ruleActivatedSteps = getActivateStepIdsFromRule(rule);
                    activeStepIds.addAll(ruleActivatedSteps);
                    log.info("✅ Rule {}에 의해 Step 활성화: {}", rule.getId(), ruleActivatedSteps);
                } else {
                    log.debug("❌ Rule {} 조건 불만족", rule.getId());
                }
            }
        }

        // Item 또는 ItemOption의 activationRule 평가
        for (SopItemResult result : resultMap.values()) {
            if (!Boolean.TRUE.equals(result.getSelected())) {
                continue;
            }
            
            // ItemOption의 activationRule
            SopItemOption option = result.getOption();
            if (option != null && option.getActivationRule() != null) {
                SopRule rule = option.getActivationRule();
                if (evaluateRuleStructured(rule, resultMap)) {
                    activeStepIds.addAll(getActivateStepIdsFromRule(rule));
                }
            }
            
            // Item의 activationRule
            SopItem item = result.getItem();
            if (item.getActivationRule() != null) {
                SopRule rule = item.getActivationRule();
                if (evaluateRuleStructured(rule, resultMap)) {
                    activeStepIds.addAll(getActivateStepIdsFromRule(rule));
                }
            }
        }

        log.info("Active Step IDs: {}", activeStepIds);

        // 각 Step의 활성/완료 상태 저장
        List<SopStepStatusResponse> responses = new ArrayList<>();
        for (SopStep step : steps) {
            boolean isActive = activeStepIds.contains(step.getId());
            boolean isCompleted = isStepCompleted(step, instanceId, resultMap);

            SopStepStatus status = stepStatusRepository.findByInstance_IdAndStep_Id(instanceId, step.getId())
                    .orElseGet(() -> SopStepStatus.builder()
                            .instance(instance)
                            .step(step)
                            .build());

            status.setIsActive(isActive);
            status.setIsCompleted(isCompleted);
            status = stepStatusRepository.save(status);
            
            log.debug("Step id={}, isActive={}, isCompleted={}", step.getId(), isActive, isCompleted);

            responses.add(toStepStatusResponse(status));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public boolean canCompleteStep(Long instanceId, Long stepId) {
        SopStep step = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("SOP Step을 찾을 수 없습니다: " + stepId));
        Map<Long, SopItemResult> resultMap = itemResultRepository.findByInstance_Id(instanceId)
                .stream()
                .collect(Collectors.toMap(r -> r.getItem().getId(), r -> r));
        return isStepCompleted(step, instanceId, resultMap);
    }

    @Transactional
    public SopInstanceResponse completeInstance(Long instanceId) {
        SopInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SOP 인스턴스를 찾을 수 없습니다: " + instanceId));

        // 이미 완료된 경우
        if ("COMPLETED".equals(instance.getStatus())) {
            throw new IllegalStateException("이미 완료된 SOP 인스턴스입니다.");
        }

        // 모든 활성 Step이 완료되었는지 확인
        Long templateId = instance.getTemplate().getId();
        List<SopStep> steps = stepRepository.findByTemplate_IdOrderByStepOrderAsc(templateId);
        Map<Long, SopItemResult> resultMap = itemResultRepository.findByInstance_Id(instanceId)
                .stream()
                .collect(Collectors.toMap(r -> r.getItem().getId(), r -> r));

        // 현재 활성화된 Step 목록 가져오기
        List<SopStepStatus> stepStatuses = stepStatusRepository.findByInstance_Id(instanceId);
        Set<Long> activeStepIds = stepStatuses.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .map(s -> s.getStep().getId())
                .collect(Collectors.toSet());

        // 활성화된 모든 Step이 완료되었는지 확인
        for (SopStep step : steps) {
            if (activeStepIds.contains(step.getId())) {
                boolean isCompleted = isStepCompleted(step, instanceId, resultMap);
                if (!isCompleted) {
                    throw new IllegalStateException("완료되지 않은 필수 항목이 있습니다: " + step.getTitle());
                }
            }
        }

        // SOP 완료 처리
        instance.setStatus("COMPLETED");
        instance.setCompletedAt(OffsetDateTime.now());
        instance = instanceRepository.save(instance);

        log.info("SOP 인스턴스 완료: id={}, eventId={}", instance.getId(), instance.getEventId());
        return toInstanceResponse(instance);
    }

    @Transactional(readOnly = true)
    public boolean canCompleteInstance(Long instanceId) {
        SopInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("SOP 인스턴스를 찾을 수 없습니다: " + instanceId));

        // 이미 완료된 경우
        if ("COMPLETED".equals(instance.getStatus())) {
            return false;
        }

        // 모든 활성 Step이 완료되었는지 확인
        Long templateId = instance.getTemplate().getId();
        List<SopStep> steps = stepRepository.findByTemplate_IdOrderByStepOrderAsc(templateId);
        Map<Long, SopItemResult> resultMap = itemResultRepository.findByInstance_Id(instanceId)
                .stream()
                .collect(Collectors.toMap(r -> r.getItem().getId(), r -> r));

        // 현재 활성화된 Step 목록 가져오기
        List<SopStepStatus> stepStatuses = stepStatusRepository.findByInstance_Id(instanceId);
        Set<Long> activeStepIds = stepStatuses.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .map(s -> s.getStep().getId())
                .collect(Collectors.toSet());

        // 활성화된 모든 Step이 완료되었는지 확인
        for (SopStep step : steps) {
            if (activeStepIds.contains(step.getId())) {
                boolean isCompleted = isStepCompleted(step, instanceId, resultMap);
                if (!isCompleted) {
                    return false;
                }
            }
        }

        return true;
    }

    private SopTemplateResponse toTemplateResponse(SopTemplate template) {
        return SopTemplateResponse.builder()
                .id(template.getId())
                .eventType(template.getEventType())
                .name(template.getName())
                .version(template.getVersion())
                .isActive(template.getIsActive())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }

    private SopInstanceResponse toInstanceResponse(SopInstance instance) {
        SopTemplate template = instance.getTemplate();
        return SopInstanceResponse.builder()
                .id(instance.getId())
                .eventId(instance.getEventId())
                .templateId(template.getId())
                .templateName(template.getName())
                .eventType(template.getEventType())
                .status(instance.getStatus())
                .startedAt(instance.getStartedAt())
                .completedAt(instance.getCompletedAt())
                .createdAt(instance.getCreatedAt())
                .updatedAt(instance.getUpdatedAt())
                .build();
    }

    private SopItemResultResponse toItemResultResponse(SopItemResult result) {
        return SopItemResultResponse.builder()
                .id(result.getId())
                .instanceId(result.getInstance().getId())
                .itemId(result.getItem().getId())
                .optionId(result.getOption() != null ? result.getOption().getId() : null)
                .selected(result.getSelected())
                .selectedBy(result.getSelectedBy())
                .selectedAt(result.getSelectedAt())
                .build();
    }

    private SopStepResponse toStepResponse(SopStep step) {
        return SopStepResponse.builder()
                .id(step.getId())
                .templateId(step.getTemplate().getId())
                .stepOrder(step.getStepOrder())
                .title(step.getTitle())
                .description(step.getDescription())
                .activationRuleId(step.getActivationRule() != null ? step.getActivationRule().getId() : null)
                .completionRuleId(step.getCompletionRule() != null ? step.getCompletionRule().getId() : null)
                .build();
    }

    private SopStepWithItemsResponse toStepWithItemsResponse(SopStep step) {
        List<SopItemResponse> items = step.getItems().stream()
                .sorted(Comparator.comparing(i -> Optional.ofNullable(i.getDisplayOrder()).orElse(0)))
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return SopStepWithItemsResponse.builder()
                .id(step.getId())
                .stepOrder(step.getStepOrder())
                .title(step.getTitle())
                .description(step.getDescription())
                .activationRuleId(step.getActivationRule() != null ? step.getActivationRule().getId() : null)
                .completionRuleId(step.getCompletionRule() != null ? step.getCompletionRule().getId() : null)
                .items(items)
                .build();
    }

    private SopItemResponse toItemResponse(SopItem item) {
        return SopItemResponse.builder()
                .id(item.getId())
                .stepId(item.getStep().getId())
                .title(item.getTitle())
                .itemType(item.getItemType())
                .isRequired(item.getIsRequired())
                .groupKey(item.getGroupKey())
                .displayOrder(item.getDisplayOrder())
                .uiStyle(item.getUiStyle())
                .actionLabel(item.getActionLabel())
                .activationRuleId(item.getActivationRule() != null ? item.getActivationRule().getId() : null)
                .options(item.getOptions().stream()
                        .sorted(Comparator.comparing(o -> Optional.ofNullable(o.getDisplayOrder()).orElse(0)))
                        .map(this::toItemOptionResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private void applyOptions(SopItem item, List<SopItemOptionRequest> options) {
        item.getOptions().clear();
        if (options == null || options.isEmpty()) {
            return;
        }
        for (SopItemOptionRequest option : options) {
            if (option.getLabel() == null || option.getLabel().isBlank()) {
                continue;
            }
            SopRule activationRule = option.getActivationRuleId() != null
                    ? ruleRepository.findById(option.getActivationRuleId())
                            .orElseThrow(() -> new IllegalArgumentException("SOP 룰을 찾을 수 없습니다: " + option.getActivationRuleId()))
                    : null;

            SopItemOption entity = SopItemOption.builder()
                    .item(item)
                    .label(option.getLabel())
                    .uiStyle(option.getUiStyle())
                    .displayOrder(option.getDisplayOrder() != null ? option.getDisplayOrder() : 0)
                    .activationRule(activationRule)
                    .build();
            item.getOptions().add(entity);
        }
    }

    private SopItemOptionResponse toItemOptionResponse(SopItemOption option) {
        return SopItemOptionResponse.builder()
                .id(option.getId())
                .label(option.getLabel())
                .uiStyle(option.getUiStyle())
                .displayOrder(option.getDisplayOrder())
                .activationRuleId(option.getActivationRule() != null ? option.getActivationRule().getId() : null)
                .build();
    }

    private SopStepStatusResponse toStepStatusResponse(SopStepStatus status) {
        return SopStepStatusResponse.builder()
                .id(status.getId())
                .instanceId(status.getInstance().getId())
                .stepId(status.getStep().getId())
                .isActive(status.getIsActive())
                .isCompleted(status.getIsCompleted())
                .updatedAt(status.getUpdatedAt())
                .build();
    }

    private SopRuleResponse toRuleResponse(SopRule rule) {
        List<SopRuleConditionResponse> conditionResponses = rule.getConditions().stream()
                .map(this::toRuleConditionResponse)
                .collect(Collectors.toList());

        List<SopRuleActionResponse> actionResponses = rule.getActions().stream()
                .map(this::toRuleActionResponse)
                .collect(Collectors.toList());

        return SopRuleResponse.builder()
                .id(rule.getId())
                .templateId(rule.getTemplate() != null ? rule.getTemplate().getId() : null)
                .ruleType(rule.getRuleType())
                .description(rule.getDescription())
                .conditions(conditionResponses)
                .actions(actionResponses)
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }

    private SopRuleConditionResponse toRuleConditionResponse(SopRuleCondition condition) {
        String itemLabel = null;
        String optionLabel = null;

        // Item 정보 조회
        if (condition.getItemId() != null) {
            itemRepository.findById(condition.getItemId()).ifPresent(item -> {
                // itemLabel을 설정하려면 람다 외부에서 처리해야 함
            });
            SopItem item = itemRepository.findById(condition.getItemId()).orElse(null);
            if (item != null) {
                itemLabel = item.getTitle();
                
                // Option 정보 조회
                if (condition.getOptionId() != null) {
                    SopItemOption option = itemOptionRepository.findById(condition.getOptionId()).orElse(null);
                    if (option != null) {
                        optionLabel = option.getLabel();
                    }
                }
            }
        }

        return SopRuleConditionResponse.builder()
                .id(condition.getId())
                .ruleId(condition.getRule().getId())
                .itemId(condition.getItemId())
                .optionId(condition.getOptionId())
                .itemLabel(itemLabel)
                .optionLabel(optionLabel)
                .build();
    }

    private SopRuleActionResponse toRuleActionResponse(SopRuleAction action) {
        String stepTitle = null;

        // Step 정보 조회
        if (action.getStepId() != null) {
            SopStep step = stepRepository.findById(action.getStepId()).orElse(null);
            if (step != null) {
                stepTitle = step.getTitle();
            }
        }

        return SopRuleActionResponse.builder()
                .id(action.getId())
                .ruleId(action.getRule().getId())
                .stepId(action.getStepId())
                .stepTitle(stepTitle)
                .build();
    }

    /**
     * 구조화된 Rule 평가 (Condition + Action 기반)
     */
    private boolean evaluateRuleStructured(SopRule rule, Map<Long, SopItemResult> resultMap) {
        if (rule == null) {
            return false;
        }

        // 모든 Condition이 만족되어야 true
        List<SopRuleCondition> conditions = rule.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }

        for (SopRuleCondition condition : conditions) {
            Long itemId = condition.getItemId();
            Long optionId = condition.getOptionId();

            SopItemResult result = resultMap.get(itemId);
            if (result == null || !Boolean.TRUE.equals(result.getSelected())) {
                return false;
            }

            // optionId가 지정되어 있으면 해당 옵션이 선택되었는지 확인
            if (optionId != null) {
                if (result.getOption() == null || !optionId.equals(result.getOption().getId())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Rule의 Action에서 활성화할 Step ID 목록 추출
     */
    private Set<Long> getActivateStepIdsFromRule(SopRule rule) {
        Set<Long> stepIds = new HashSet<>();
        if (rule == null) {
            return stepIds;
        }

        List<SopRuleAction> actions = rule.getActions();
        if (actions != null) {
            for (SopRuleAction action : actions) {
                stepIds.add(action.getStepId());
            }
        }

        return stepIds;
    }

    /**
     * 기존 expression 기반 Rule 평가 (deprecated, 하위호환용)
     */
    @Deprecated
    private boolean evaluateRule(SopRule rule, Long instanceId, Map<Long, SopItemResult> resultMap) {
        String expression = rule.getExpression();
        if (expression == null || expression.isBlank()) {
            return false;
        }

        String left = expression.split("->")[0].trim();
        Matcher itemSelected = Pattern.compile("itemSelected\\(itemId=(\\d+)(,\\s*optionId=(\\d+))?\\)").matcher(left);
        if (itemSelected.find()) {
            Long itemId = Long.parseLong(itemSelected.group(1));
            Long optionId = itemSelected.group(3) != null ? Long.parseLong(itemSelected.group(3)) : null;
            SopItemResult result = resultMap.get(itemId);
            if (result == null || !Boolean.TRUE.equals(result.getSelected())) {
                return false;
            }
            if (optionId == null) {
                return true;
            }
            return result.getOption() != null && optionId.equals(result.getOption().getId());
        }

        Matcher requiredAll = Pattern.compile("requiredAll\\(stepId=(\\d+)\\)").matcher(left);
        if (requiredAll.find()) {
            Long stepId = Long.parseLong(requiredAll.group(1));
            return areRequiredItemsSelected(stepId, resultMap);
        }

        Matcher groupAny = Pattern.compile("groupAny\\(stepId=(\\d+),\\s*groupKey=\"([^\"]+)\"\\)").matcher(left);
        if (groupAny.find()) {
            Long stepId = Long.parseLong(groupAny.group(1));
            String groupKey = groupAny.group(2);
            return isGroupAnySelected(stepId, groupKey, resultMap);
        }

        return false;
    }

    /**
     * 기존 expression에서 stepId 추출 (deprecated, 하위호환용)
     */
    @Deprecated
    private Set<Long> extractActivateStepIds(String expression) {
        Set<Long> stepIds = new HashSet<>();
        if (expression == null || expression.isBlank()) {
            return stepIds;
        }
        Matcher matcher = Pattern.compile("activate\\(stepId=(\\d+)\\)").matcher(expression);
        while (matcher.find()) {
            stepIds.add(Long.parseLong(matcher.group(1)));
        }
        return stepIds;
    }

    private boolean isStepCompleted(SopStep step, Long instanceId, Map<Long, SopItemResult> resultMap) {
        // completionRule이 있으면 구조화된 방식으로 평가
        if (step.getCompletionRule() != null) {
            return evaluateRuleStructured(step.getCompletionRule(), resultMap);
        }

        // 기본 로직: 필수 아이템이 모두 선택되었는지 확인
        Long stepId = step.getId();
        List<SopItem> items = itemRepository.findByStep_Id(stepId);

        boolean requiredItemsOk = items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsRequired()) && item.getGroupKey() == null)
                .allMatch(item -> isItemSelected(item.getId(), resultMap));

        Map<String, List<SopItem>> requiredGroups = items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsRequired()) && item.getGroupKey() != null)
                .collect(Collectors.groupingBy(SopItem::getGroupKey));

        boolean groupOk = requiredGroups.entrySet().stream()
                .allMatch(entry -> entry.getValue().stream()
                        .anyMatch(item -> isItemSelected(item.getId(), resultMap)));

        return requiredItemsOk && groupOk;
    }

    private boolean areRequiredItemsSelected(Long stepId, Map<Long, SopItemResult> resultMap) {
        List<SopItem> items = itemRepository.findByStep_Id(stepId);
        return items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsRequired()))
                .allMatch(item -> isItemSelected(item.getId(), resultMap));
    }

    private boolean isGroupAnySelected(Long stepId, String groupKey, Map<Long, SopItemResult> resultMap) {
        List<SopItem> items = itemRepository.findByStep_Id(stepId);
        return items.stream()
                .filter(item -> groupKey.equals(item.getGroupKey()))
                .anyMatch(item -> isItemSelected(item.getId(), resultMap));
    }

    private boolean isItemSelected(Long itemId, Map<Long, SopItemResult> resultMap) {
        SopItemResult result = resultMap.get(itemId);
        return result != null && Boolean.TRUE.equals(result.getSelected());
    }
}
