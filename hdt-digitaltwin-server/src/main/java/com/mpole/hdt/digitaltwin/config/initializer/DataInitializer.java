package com.mpole.hdt.digitaltwin.config.initializer;

import com.mpole.hdt.digitaltwin.persistence.sop.*;
import com.mpole.hdt.digitaltwin.persistence.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    private final PasswordEncoder passwordEncoder;

    
    // SOP 관련 Repository
    private final SopTemplateRepository sopTemplateRepository;
    private final SopRuleRepository sopRuleRepository;
    private final SopRuleConditionRepository sopRuleConditionRepository;
    private final SopRuleActionRepository sopRuleActionRepository;
    private final SopStepRepository sopStepRepository;
    private final SopItemRepository sopItemRepository;
    private final SopItemOptionRepository sopItemOptionRepository;

    private final MenuDataCreate menuDataCreate;

    @Override
    public void run(String... args) {
        initializeRole(); // 사용자 권한 초기화
        initializeUsers(); // 사용자 초기화
        initializeSopSampleData(); // SOP 샘플 데이터 초기화

        //menuDataCreate.initializeMenus();
        //menuDataCreate.initializeRoleMenu();
    }

    private void initializeRole() {
        if (roleRepo.count() > 0) {
            log.info("권한 데이타가 이미 존재합니다. 초기화를 건너띕니다.");
            return;
        }
        List<Role> roleList = new ArrayList<>();
        roleList.add(Role.builder().roleName("ADMIN").description("관리자").build());
        roleList.add(Role.builder().roleName("MANAGER").description("매니저").build());
        roleList.add(Role.builder().roleName("USER").description("일반 사용자").build());
        roleList.add(Role.builder().roleName("VIEWER").description("뷰어").build());

        roleRepo.saveAll(roleList);
    }

    private List<Map<String,String>> USERS_DUMMY_DATA = List.of(
            Map.of("role","ADMIN", "loginId","admin","username","시스템관리자"),
            Map.of("role","MANAGER", "loginId","manager","username","운영 매니저"),
            Map.of("role","USER", "loginId","unity","username","Unity 테스트")
    );

    private void initializeUsers() {
        if (userRepo.count() > 0) {
            log.info("사용자 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }
        log.info("===== 테스트 사용자 데이터 초기화 시작 =====");
        log.info("테스트 계정:");
        USERS_DUMMY_DATA.forEach(map->{

            String role_ = map.get("role");
            String loginId = map.get("loginId");
            String username = map.get("username");

            Role role = roleRepo.findByRoleName(role_)
                    .orElseThrow(() -> new RuntimeException(role_ +" 권한을 찾을 수 없습니다."));

            User user = User.builder()
                    .loginId(loginId)
                    .passwordHash(passwordEncoder.encode("1q2w3e"))
                    .email(loginId+"@mpole.co.kr")
                    .username(username)
                    .active(true)
                    .build();

            UserRole userRole = UserRole.builder().user(user).role(role).build();
            user.getUserRoles().add(userRole);
            userRepo.save(user);
            log.info("  - {}/ 1q2w3e (관리자)",loginId);
        });
//        Users admin = Users.builder()
//                .loginId("admin")
//                .password(passwordEncoder.encode("1q2w3e"))
//                .email("dhhan@mpole.co.kr")
//                .name("시스템 관리자")
//                .role(UserRole.ROLE_ADMIN)
//                .enabled(true)
//                .accountNonLocked(true)
//                .failedLoginAttempts(0)
//                .lastPasswordChangeDate(LocalDateTime.now())
//                .createdAt(LocalDateTime.now())
//                .build();
//        userRepository.save(admin);
//
//        // 매니저 계정
//        Users manager = Users.builder()
//                .loginId("manager")
//                .password(passwordEncoder.encode("1q2w3e"))
//                .email("dhhan@mpole.co.kr")
//                .name("운영 매니저")
//                .role(UserRole.ROLE_MANAGER)
//                .enabled(true)
//                .accountNonLocked(true)
//                .failedLoginAttempts(0)
//                .lastPasswordChangeDate(LocalDateTime.now())
//                .createdAt(LocalDateTime.now())
//                .build();
//        userRepository.save(manager);
//
//        // Unity 테스트 계정
//        Users unityUser = Users.builder()
//                .loginId("unity")
//                .password(passwordEncoder.encode("1q2w3e"))
//                .email("dhhan@mpole.co.kr")
//                .name("Unity 테스트")
//                .role(UserRole.ROLE_USER)
//                .enabled(true)
//                .accountNonLocked(true)
//                .failedLoginAttempts(0)
//                .lastPasswordChangeDate(LocalDateTime.now())
//                .createdAt(LocalDateTime.now())
//                .build();
//        userRepository.save(unityUser);

//        // 비밀번호 만료 테스트 계정 (90일 이전에 변경)
//        Users expiredUser = Users.builder()
//                .loginId("expired")
//                .password(passwordEncoder.encode("1q2w3e"))
//                .email("expired@hanadream.com")
//                .name("만료 테스트")
//                .role(UserRole.ROLE_USER)
//                .enabled(true)
//                .accountNonLocked(true)
//                .failedLoginAttempts(0)
//                .lastPasswordChangeDate(LocalDateTime.now().minusDays(91))
//                .createdAt(LocalDateTime.now())
//                .build();
//        userRepository.save(expiredUser);
//
//        log.info("===== 테스트 사용자 {} 건 초기화 완료 =====", userRepository.count());
//        log.info("테스트 계정:");
//        log.info("  - admin / 1q2w3e (관리자)");
//        log.info("  - manager / 1q2w3e (매니저)");
//        log.info("  - unity / 1q2w3e (일반 사용자)");
//        log.info("  - expired / 1q2w3e (비밀번호 만료 테스트)");
    }
    
    /**
     * SOP 샘플 데이터 초기화
     * data.sql의 내용을 JPA로 구현
     */
    private void initializeSopSampleData() {
        if (sopTemplateRepository.count() > 0) {
            log.info("SOP 샘플 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("===== SOP 샘플 데이터 초기화 시작 =====");

        // 1. 화재감지 SOP 템플릿 생성
        createFireDetectionSop();

        // 2. 지진감지 SOP 템플릿 생성
        createEarthquakeSop();

        log.info("===== SOP 샘플 데이터 초기화 완료 =====");
        log.info("  - 화재감지 SOP 템플릿 (ID: 1)");
        log.info("  - 지진감지 SOP 템플릿 (ID: 2)");
    }

    /**
     * 화재감지 SOP 샘플 데이터 생성
     */
    private void createFireDetectionSop() {
        // 1. SOP Template 생성
        SopTemplate fireTemplate = SopTemplate.builder()
                .eventType("FIRE")
                .name("화재감지 SOP 샘플")
                .version(1)
                .isActive(true)
                .build();
        fireTemplate = sopTemplateRepository.save(fireTemplate);
        log.info("화재감지 SOP 템플릿 생성 완료 (ID: {})", fireTemplate.getId());

        // 2. SOP Rules 생성 (조건과 액션을 위한 룰)
        SopRule rule100 = SopRule.builder()
                .template(fireTemplate)
                .ruleType("ACTIVATE_STEP")
                .expression("itemSelected(itemId=3, optionId=1) -> activate(stepId=2); activate(stepId=3); activate(stepId=4)")
                .description("화재발생 선택 시 STEP2~4 활성화")
                .build();
        rule100 = sopRuleRepository.save(rule100);

        SopRule rule200 = SopRule.builder()
                .template(fireTemplate)
                .ruleType("ACTIVATE_STEP")
                .expression("itemSelected(itemId=3, optionId=2) -> activate(stepId=2); activate(stepId=4)")
                .description("비상상황 선택 시 STEP2,4 활성화")
                .build();
        rule200 = sopRuleRepository.save(rule200);

        SopRule rule300 = SopRule.builder()
                .template(fireTemplate)
                .ruleType("ACTIVATE_STEP")
                .expression("itemSelected(itemId=3, optionId=3) -> activate(stepId=4)")
                .description("오작동 확인 선택 시 STEP4 활성화")
                .build();
        rule300 = sopRuleRepository.save(rule300);

        // 3. Steps 생성
        SopStep step1 = SopStep.builder()
                .template(fireTemplate)
                .stepOrder(1)
                .title("화재감지 인식")
                .description("오작동 여부 확인 단계")
                .build();
        step1 = sopStepRepository.save(step1);

        SopStep step2 = SopStep.builder()
                .template(fireTemplate)
                .stepOrder(2)
                .title("화재 상황전파")
                .description("실제 화재 시 전파 단계")
                .build();
        step2 = sopStepRepository.save(step2);

        SopStep step3 = SopStep.builder()
                .template(fireTemplate)
                .stepOrder(3)
                .title("현장 출동 및 신고")
                .description("초동 조치 및 신고 단계")
                .build();
        step3 = sopStepRepository.save(step3);

        SopStep step4 = SopStep.builder()
                .template(fireTemplate)
                .stepOrder(4)
                .title("상황처리")
                .description("상황 종료 처리 단계")
                .build();
        step4 = sopStepRepository.save(step4);

        // 4. Items 생성 (Rule 연결은 나중에)
        SopItem item1 = SopItem.builder()
                .step(step1)
                .title("화재 신호 위치 확인")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(1)
                .actionLabel("완료")
                .build();
        item1 = sopItemRepository.save(item1);

        SopItem item2 = SopItem.builder()
                .step(step1)
                .title("CCTV 화면 및 현장 상태 확인")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(2)
                .actionLabel("완료")
                .build();
        item2 = sopItemRepository.save(item2);

        SopItem item3 = SopItem.builder()
                .step(step1)
                .title("상태 선택")
                .itemType("MULTI_BUTTON")
                .isRequired(true)
                .groupKey("STATUS")
                .displayOrder(3)
                .build();
        item3 = sopItemRepository.save(item3);

        SopItem item4 = SopItem.builder()
                .step(step2)
                .title("비상대피 방송 송출 (텔레폰페이징)")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(1)
                .actionLabel("완료")
                .build();
        item4 = sopItemRepository.save(item4);

        SopItem item5 = SopItem.builder()
                .step(step3)
                .title("초동 조치 확인")
                .itemType("MULTI_BUTTON")
                .isRequired(true)
                .groupKey("FIRST_RESPONSE")
                .displayOrder(1)
                .build();
        item5 = sopItemRepository.save(item5);

        SopItem item6 = SopItem.builder()
                .step(step3)
                .title("유관 기관 신고")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(2)
                .actionLabel("완료")
                .build();
        item6 = sopItemRepository.save(item6);

        SopItem item7 = SopItem.builder()
                .step(step4)
                .title("상황종료 방송 송출")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(1)
                .actionLabel("완료")
                .build();
        item7 = sopItemRepository.save(item7);

        // 5. Item Options 생성 (activation_rule_id 연결)
        SopItemOption option1 = SopItemOption.builder()
                .item(item3)
                .label("화재발생")
                .uiStyle("danger")
                .displayOrder(1)
                .activationRule(rule100)
                .build();
        sopItemOptionRepository.save(option1);

        SopItemOption option2 = SopItemOption.builder()
                .item(item3)
                .label("비상상황")
                .uiStyle("primary")
                .displayOrder(2)
                .activationRule(rule200)
                .build();
        sopItemOptionRepository.save(option2);

        SopItemOption option3 = SopItemOption.builder()
                .item(item3)
                .label("오작동 확인")
                .uiStyle("secondary")
                .displayOrder(3)
                .activationRule(rule300)
                .build();
        sopItemOptionRepository.save(option3);

        SopItemOption option4 = SopItemOption.builder()
                .item(item5)
                .label("조치 완료")
                .uiStyle("primary")
                .displayOrder(1)
                .build();
        sopItemOptionRepository.save(option4);

        SopItemOption option5 = SopItemOption.builder()
                .item(item5)
                .label("조치 불가")
                .uiStyle("danger")
                .displayOrder(2)
                .build();
        sopItemOptionRepository.save(option5);

        // 6. Rule Conditions 생성
        SopRuleCondition condition1 = SopRuleCondition.builder()
                .rule(rule100)
                .itemId(item3.getId())
                .optionId(option1.getId())
                .build();
        sopRuleConditionRepository.save(condition1);

        SopRuleCondition condition2 = SopRuleCondition.builder()
                .rule(rule200)
                .itemId(item3.getId())
                .optionId(option2.getId())
                .build();
        sopRuleConditionRepository.save(condition2);

        SopRuleCondition condition3 = SopRuleCondition.builder()
                .rule(rule300)
                .itemId(item3.getId())
                .optionId(option3.getId())
                .build();
        sopRuleConditionRepository.save(condition3);

        // 7. Rule Actions 생성
        // Rule 100: step 2, 3, 4 활성화
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule100)
                .stepId(step2.getId())
                .build());
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule100)
                .stepId(step3.getId())
                .build());
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule100)
                .stepId(step4.getId())
                .build());

        // Rule 200: step 2, 4 활성화
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule200)
                .stepId(step2.getId())
                .build());
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule200)
                .stepId(step4.getId())
                .build());

        // Rule 300: step 4 활성화
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule300)
                .stepId(step4.getId())
                .build());

        log.info("화재감지 SOP 데이터 생성 완료 (Steps: 4, Items: 7, Options: 5, Rules: 3)");
    }

    /**
     * 지진감지 SOP 샘플 데이터 생성
     */
    private void createEarthquakeSop() {
        // 1. SOP Template 생성
        SopTemplate earthquakeTemplate = SopTemplate.builder()
                .eventType("EARTHQUAKE")
                .name("지진감지 SOP 샘플")
                .version(1)
                .isActive(true)
                .build();
        earthquakeTemplate = sopTemplateRepository.save(earthquakeTemplate);
        log.info("지진감지 SOP 템플릿 생성 완료 (ID: {})", earthquakeTemplate.getId());

        // 2. SOP Rules 생성
        SopRule rule400 = SopRule.builder()
                .template(earthquakeTemplate)
                .ruleType("ACTIVATE_STEP")
                .expression("")
                .description("실제 지진 선택 시 STEP 11~14 활성화")
                .build();
        rule400 = sopRuleRepository.save(rule400);

        SopRule rule500 = SopRule.builder()
                .template(earthquakeTemplate)
                .ruleType("ACTIVATE_STEP")
                .expression("")
                .description("여진 선택 시 STEP 13~14 활성화")
                .build();
        rule500 = sopRuleRepository.save(rule500);

        SopRule rule600 = SopRule.builder()
                .template(earthquakeTemplate)
                .ruleType("ACTIVATE_STEP")
                .expression("")
                .description("오작동 선택 시 STEP 14만 활성화")
                .build();
        rule600 = sopRuleRepository.save(rule600);

        // 3. Steps 생성
        SopStep step10 = SopStep.builder()
                .template(earthquakeTemplate)
                .stepOrder(1)
                .title("지진 감지 확인")
                .description("지진 센서 알람 확인 및 상황 판단")
                .build();
        step10 = sopStepRepository.save(step10);

        SopStep step11 = SopStep.builder()
                .template(earthquakeTemplate)
                .stepOrder(2)
                .title("긴급 대피 안내")
                .description("실제 지진 시 대피 방송 및 안내")
                .build();
        step11 = sopStepRepository.save(step11);

        SopStep step12 = SopStep.builder()
                .template(earthquakeTemplate)
                .stepOrder(3)
                .title("현장 안전 확인")
                .description("건물 및 시설 안전도 점검")
                .build();
        step12 = sopStepRepository.save(step12);

        SopStep step13 = SopStep.builder()
                .template(earthquakeTemplate)
                .stepOrder(4)
                .title("복구 및 보고")
                .description("피해 상황 파악 및 복구 조치")
                .build();
        step13 = sopStepRepository.save(step13);

        SopStep step14 = SopStep.builder()
                .template(earthquakeTemplate)
                .stepOrder(5)
                .title("상황 종료")
                .description("최종 점검 및 종료 처리")
                .build();
        step14 = sopStepRepository.save(step14);

        // 4. Items 생성 - Step 1 (지진 감지 확인)
        SopItem item10 = SopItem.builder()
                .step(step10)
                .title("지진 센서 알람 확인")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(1)
                .actionLabel("확인")
                .build();
        item10 = sopItemRepository.save(item10);

        SopItem item11 = SopItem.builder()
                .step(step10)
                .title("상황 판단")
                .itemType("MULTI_BUTTON")
                .isRequired(true)
                .groupKey("EARTHQUAKE_STATUS")
                .displayOrder(2)
                .build();
        item11 = sopItemRepository.save(item11);

        SopItem item12 = SopItem.builder()
                .step(step10)
                .title("지진 강도 기록 (진도)")
                .itemType("SINGLE_BUTTON")
                .isRequired(false)
                .displayOrder(3)
                .actionLabel("기록")
                .build();
        item12 = sopItemRepository.save(item12);

        // 5. Items 생성 - Step 2 (긴급 대피 안내)
        SopItem item13 = SopItem.builder()
                .step(step11)
                .title("긴급 대피 방송 송출")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(1)
                .actionLabel("완료")
                .build();
        item13 = sopItemRepository.save(item13);

        SopItem item14 = SopItem.builder()
                .step(step11)
                .title("대피 경로 안내 (방송)")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(2)
                .actionLabel("완료")
                .build();
        item14 = sopItemRepository.save(item14);

        SopItem item15 = SopItem.builder()
                .step(step11)
                .title("엘리베이터 긴급 정지")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(3)
                .actionLabel("완료")
                .build();
        item15 = sopItemRepository.save(item15);

        // 6. Items 생성 - Step 3 (현장 안전 확인)
        SopItem item16 = SopItem.builder()
                .step(step12)
                .title("건물 균열 및 파손 점검")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(1)
                .actionLabel("완료")
                .build();
        item16 = sopItemRepository.save(item16);

        SopItem item17 = SopItem.builder()
                .step(step12)
                .title("가스 누출 여부 확인")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(2)
                .actionLabel("완료")
                .build();
        item17 = sopItemRepository.save(item17);

        SopItem item18 = SopItem.builder()
                .step(step12)
                .title("재실 인원 파악")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(3)
                .actionLabel("완료")
                .build();
        item18 = sopItemRepository.save(item18);

        // 7. Items 생성 - Step 4 (복구 및 보고)
        SopItem item19 = SopItem.builder()
                .step(step13)
                .title("피해 상황 보고서 작성")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(1)
                .actionLabel("완료")
                .build();
        item19 = sopItemRepository.save(item19);

        SopItem item20 = SopItem.builder()
                .step(step13)
                .title("관련 기관 보고")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(2)
                .actionLabel("완료")
                .build();
        item20 = sopItemRepository.save(item20);

        SopItem item21 = SopItem.builder()
                .step(step13)
                .title("복구 조치 계획 수립")
                .itemType("SINGLE_BUTTON")
                .isRequired(false)
                .displayOrder(3)
                .actionLabel("완료")
                .build();
        item21 = sopItemRepository.save(item21);

        // 8. Items 생성 - Step 5 (상황 종료)
        SopItem item22 = SopItem.builder()
                .step(step14)
                .title("안전 확인 완료 방송")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(1)
                .actionLabel("완료")
                .build();
        item22 = sopItemRepository.save(item22);

        SopItem item23 = SopItem.builder()
                .step(step14)
                .title("상황 종료 보고")
                .itemType("SINGLE_BUTTON")
                .isRequired(true)
                .displayOrder(2)
                .actionLabel("완료")
                .build();
        item23 = sopItemRepository.save(item23);

        // 9. Item Options 생성 (상황 판단 - item11)
        SopItemOption option10 = SopItemOption.builder()
                .item(item11)
                .label("실제 지진")
                .uiStyle("danger")
                .displayOrder(1)
                .activationRule(rule400)
                .build();
        option10 = sopItemOptionRepository.save(option10);

        SopItemOption option11 = SopItemOption.builder()
                .item(item11)
                .label("여진")
                .uiStyle("warning")
                .displayOrder(2)
                .activationRule(rule500)
                .build();
        option11 = sopItemOptionRepository.save(option11);

        SopItemOption option12 = SopItemOption.builder()
                .item(item11)
                .label("오작동")
                .uiStyle("secondary")
                .displayOrder(3)
                .activationRule(rule600)
                .build();
        option12 = sopItemOptionRepository.save(option12);

        // 10. Rule Conditions 생성
        sopRuleConditionRepository.save(SopRuleCondition.builder()
                .rule(rule400)
                .itemId(item11.getId())
                .optionId(option10.getId())
                .build());

        sopRuleConditionRepository.save(SopRuleCondition.builder()
                .rule(rule500)
                .itemId(item11.getId())
                .optionId(option11.getId())
                .build());

        sopRuleConditionRepository.save(SopRuleCondition.builder()
                .rule(rule600)
                .itemId(item11.getId())
                .optionId(option12.getId())
                .build());

        // 11. Rule Actions 생성
        // Rule 400: step 11, 12, 13, 14 활성화
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule400)
                .stepId(step11.getId())
                .build());
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule400)
                .stepId(step12.getId())
                .build());
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule400)
                .stepId(step13.getId())
                .build());
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule400)
                .stepId(step14.getId())
                .build());

        // Rule 500: step 13, 14 활성화
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule500)
                .stepId(step13.getId())
                .build());
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule500)
                .stepId(step14.getId())
                .build());

        // Rule 600: step 14 활성화
        sopRuleActionRepository.save(SopRuleAction.builder()
                .rule(rule600)
                .stepId(step14.getId())
                .build());

        log.info("지진감지 SOP 데이터 생성 완료 (Steps: 5, Items: 14, Options: 3, Rules: 3)");
    }
}

