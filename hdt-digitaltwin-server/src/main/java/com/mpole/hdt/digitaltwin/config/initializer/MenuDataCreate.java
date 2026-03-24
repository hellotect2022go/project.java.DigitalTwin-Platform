package com.mpole.hdt.digitaltwin.config.initializer;

import com.mpole.hdt.digitaltwin.persistence.menu.Menu;
import com.mpole.hdt.digitaltwin.persistence.menu.MenuRepository;
import com.mpole.hdt.digitaltwin.persistence.menu.RoleMenu;
import com.mpole.hdt.digitaltwin.persistence.menu.RoleMenuRepository;
import com.mpole.hdt.digitaltwin.persistence.user.Role;
import com.mpole.hdt.digitaltwin.persistence.user.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuDataCreate {
    private final MenuRepository menuRepo;
    private final RoleMenuRepository roleMenuRepo;
    private final RoleRepository roleRepo;

    public void initializeRoleMenu() {
        if (roleMenuRepo.count() > 0) return;

        // 관리자 에 대해서 전체 메뉴를 전부다 삽입
        Role adminRole = roleRepo.findByRoleName("ADMIN")
                .orElseThrow(() -> new RuntimeException(" 권한을 찾을 수 없습니다."));

        List<RoleMenu> adminRoleMenus = new ArrayList<>();
        menuRepo.findAllMenus().forEach(menu -> {
            RoleMenu roleMenu = RoleMenu.builder()
                    .role(adminRole)
                    .menu(menu)
                    .build();
            adminRoleMenus.add(roleMenu);
        });
        roleMenuRepo.saveAll(adminRoleMenus);

        Role managerRole = roleRepo.findByRoleName("MANAGER")
                .orElseThrow(() -> new RuntimeException(" 권한을 찾을 수 없습니다."));

        List<RoleMenu> managerRoleMenus = new ArrayList<>();
        menuRepo.findAllMenus().stream().filter(menu -> !menu.getMenuCode().startsWith("ADMIN")).forEach(menu -> {
            RoleMenu roleMenu = RoleMenu.builder()
                    .role(managerRole)
                    .menu(menu)
                    .build();
            managerRoleMenus.add(roleMenu);
        });
        roleMenuRepo.saveAll(managerRoleMenus);
    }

    public void initializeMenus() {
        if (menuRepo.count() > 0) return ;
        // 1. 대메뉴(Depth 1) 생성 및 저장
        // 나중에 자식 메뉴에서 참조하기 위해 변수로 선언합니다.
        Menu p_1 = createMenu(null, "디지털트윈", "DIGITAL_TWIN", "/digital-twin", "/icons/3d-building.svg", 2, 1);
        Menu p_2 = createMenu(null, "대시보드", "DASHBOARD", "/dashboard", "/icons/dashboard.svg", 1, 1);
        Menu p_3 = createMenu(null, "스마트오피스", "SMART_OFFICE", "/smart-office", "/icons/office.svg", 3, 1);
        Menu p_4 = createMenu(null, "건물서비스", "BUILDING_SERVICE", "/building-service", "/icons/service.svg", 4, 1);
        Menu p_5 = createMenu(null, "환경제어", "ENVIRONMENT", "/environment", "/icons/environment.svg", 5, 1);
        Menu p_6 = createMenu(null, "혁신서비스", "INNOVATION", "/innovation", "/icons/innovation.svg", 6, 1);
        Menu p_7 = createMenu(null, "관리자", "ADMIN", "/admin", "/icons/admin.svg", 7, 1);
        Menu p_8 = createMenu(null, "통계/리포트", "STATISTICS", "/statistics", "/icons/chart.svg", 8, 1);

        List<Menu> parentMenus = List.of(p_1,p_2,p_3,p_4,p_5,p_6,p_7,p_8);
        menuRepo.saveAll(parentMenus);

        // 2. 자식 메뉴(Depth 2) 생성 및 저장
        List<Menu> childMenus = new ArrayList<>();

        // 디지털트윈(ID 2)의 하위 메뉴들
        childMenus.add(createMenu(p_1, "3D 공간정보", "DT_3D_SPACE", "/digital-twin/3d-space", "/icons/3d-space.svg", 1, 2));
        childMenus.add(createMenu(p_1, "가상순찰", "DT_PATROL", "/digital-twin/patrol", "/icons/patrol.svg", 2, 2));
        childMenus.add(createMenu(p_1, "씬 관리", "DT_SCENE", "/digital-twin/scene", "/icons/scene.svg", 3, 2));
        childMenus.add(createMenu(p_1, "오브젝트 관리", "DT_OBJECT", "/digital-twin/object", "/icons/object.svg", 4, 2));

        // 2단계: 스마트오피스 하위 메뉴
        childMenus.add(createMenu(p_2, "좌석 현황", "SMART_SEAT", "/smart-office/seats", "/icons/seat.svg", 1, 2));
        childMenus.add(createMenu(p_2, "회의실 현황", "SMART_MEETING", "/smart-office/meeting-rooms", "/icons/meeting.svg", 2, 2));
        childMenus.add(createMenu(p_2, "사물함 현황", "SMART_LOCKER", "/smart-office/lockers", "/icons/locker.svg", 3, 2));
        // 2단계: 건물서비스 하위 메뉴
        childMenus.add(createMenu(p_3, "복지시설 안내", "SERVICE_FACILITY", "/building-service/facilities", "/icons/facility.svg", 1, 2));
        childMenus.add(createMenu(p_3, "셔틀버스 안내", "SERVICE_SHUTTLE", "/building-service/shuttle", "/icons/shuttle.svg", 2, 2));
        childMenus.add(createMenu(p_3, "화장실 현황", "SERVICE_RESTROOM", "/building-service/restrooms", "/icons/restroom.svg", 3, 2));
        childMenus.add(createMenu(p_3, "방문객 관리", "SERVICE_VISITOR", "/building-service/visitors", "/icons/visitor.svg", 4, 2));
        childMenus.add(createMenu(p_3, "길안내", "SERVICE_NAVIGATION", "/building-service/navigation", "/icons/navigation.svg", 5, 2));
        childMenus.add(createMenu(p_3, "주차장 관리", "SERVICE_PARKING", "/building-service/parking", "/icons/parking.svg", 6, 2));
        childMenus.add(createMenu(p_3, "식당/카페", "SERVICE_RESTAURANT", "/building-service/restaurants", "/icons/restaurant.svg", 7, 2));
        childMenus.add(createMenu(p_3, "나만의 램프", "SERVICE_MYLAMP", "/building-service/my-lamp", "/icons/lamp.svg", 8, 2));
        // 2단계: 환경제어 하위 메뉴
        childMenus.add(createMenu(p_4, "실내환경제어", "ENV_INDOOR", "/environment/indoor-control", "/icons/indoor.svg", 1, 2));
        childMenus.add(createMenu(p_4, "ESG 보드", "ENV_ESG", "/environment/esg", "/icons/esg.svg", 2, 2));
        childMenus.add(createMenu(p_4, "혼잡도 시각화", "ENV_CONGESTION", "/environment/congestion", "/icons/congestion.svg", 3, 2));
        childMenus.add(createMenu(p_4, "시설물 공간정보", "ENV_FACILITY", "/environment/facility-spatial", "/icons/facility-info.svg", 4, 2));
        // 2단계: 혁신서비스 하위 메뉴
        childMenus.add(createMenu(p_5, "로봇 운영 현황", "INNO_ROBOT", "/innovation/robots", "/icons/robot.svg", 1, 2));
        childMenus.add(createMenu(p_5, "드론 운영 현황", "INNO_DRONE", "/innovation/drones", "/icons/drone.svg", 2, 2));
        childMenus.add(createMenu(p_5, "재난관리", "INNO_DISASTER", "/innovation/disaster", "/icons/disaster.svg", 3, 2));
        // 2단계: 관리자 하위 메뉴
        childMenus.add(createMenu(p_6, "사용자 관리", "ADMIN_USER", "/admin/users", "/icons/user.svg", 1, 2));
        childMenus.add(createMenu(p_6, "권한 관리", "ADMIN_ROLE", "/admin/roles", "/icons/role.svg", 2, 2));
        childMenus.add(createMenu(p_6, "메뉴 관리", "ADMIN_MENU", "/admin/menus", "/icons/menu.svg", 3, 2));
        childMenus.add(createMenu(p_6, "오브젝트 관리", "ADMIN_OBJECT", "/admin/objects", "/icons/object-admin.svg", 4, 2));
        childMenus.add(createMenu(p_6, "이벤트 관리", "ADMIN_EVENT", "/admin/events", "/icons/event.svg", 5, 2));
        childMenus.add(createMenu(p_6, "장비 관리", "ADMIN_DEVICE", "/admin/devices", "/icons/device.svg", 6, 2));
        childMenus.add(createMenu(p_6, "SOP 관리", "ADMIN_SOP", "/admin/sop", "/icons/sop.svg", 7, 2));
        childMenus.add(createMenu(p_6, "시스템 설정", "ADMIN_SETTING", "/admin/settings", "/icons/settings.svg", 8, 2));
        childMenus.add(createMenu(p_6, "서버 모니터링", "ADMIN_SERVER", "/admin/server-monitoring", "/icons/server.svg", 9, 2));
        // 2단계: 통계/리포트 하위 메뉴
        childMenus.add(createMenu(p_7, "통합 대시보드", "STAT_DASHBOARD", "/statistics/dashboard", "/icons/stat-dashboard.svg", 1, 2));
        childMenus.add(createMenu(p_7, "좌석 이용 분석", "STAT_SEAT", "/statistics/seat-analysis", "/icons/stat-seat.svg", 2, 2));
        childMenus.add(createMenu(p_7, "에너지 분석", "STAT_ENERGY", "/statistics/energy-analysis", "/icons/stat-energy.svg", 3, 2));
        childMenus.add(createMenu(p_7, "이벤트 이력", "STAT_EVENT", "/statistics/event-history", "/icons/stat-event.svg", 4, 2));
        childMenus.add(createMenu(p_7, "접근 로그", "STAT_ACCESS", "/statistics/access-log", "/icons/stat-access.svg", 5, 2));
        // 3단계: 관리자 > 사용자 관리 하위 메뉴
        childMenus.add(createMenu(p_8, "사용자 목록", "ADMIN_USER_LIST", "/admin/users/list", null, 1, 3));
        childMenus.add(createMenu(p_8, "사용자 등록", "ADMIN_USER_CREATE", "/admin/users/create", null, 2, 3));
        childMenus.add(createMenu(p_8, "권한 할당", "ADMIN_USER_ASSIGN", "/admin/users/assign-role", null, 3, 3));
        menuRepo.saveAll(childMenus);

    }


    // 편의를 위한 헬퍼 메서드
    private Menu createMenu(Menu parent, String name, String code, String url, String icon, int sort, int depth) {
        return Menu.builder()
                .menuName(name)
                .menuCode(code)
                .sortOrder(sort)
                .active(true)
                .build();
    }
}
