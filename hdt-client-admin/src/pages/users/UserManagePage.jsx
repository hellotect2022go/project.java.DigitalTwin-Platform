import { useMemo, useState } from "react";
import styled from "styled-components";
import AdminPageTemplate from "@/components/common/AdminPageTemplate";
import UserRegisterForm from "@/components/modal/user/UserRegisterForm";
import UserEditModalForm from "@/components/modal/user/UserEditModalForm";
import { useModal } from "@/contexts/ModalContext";

const ROLE_ID_TO_NAME = {
  1: "최종관리자",
  2: "운영자",
  3: "조회자",
};

/**
 * 사용자 관리 — 검색/등록 + 목록
 * 등록·수정·삭제는 ModalContext 기반 모달
 */
const UserManagePage = () => {
  const { openModal, closeModal } = useModal();

  const [typeFilter, setTypeFilter] = useState("all");
  const [searchField, setSearchField] = useState("loginId");
  const [keywordInput, setKeywordInput] = useState("");
  const [searchKeyword, setSearchKeyword] = useState("");

  const [list, setList] = useState([
    {
      userId: 2,
      loginId: "test2",
      username: "홍길동길동",
      email: "",
      active: true,
      accountNonLocked: true,
      failedLoginAttempts: 0,
      lastPasswordChangeDate: "2026-03-01T09:00:00+09:00",
      lastLoginDate: "2026-03-17T14:30:00+09:00",
      createdAt: "2026-02-01T10:00:00+09:00",
      updatedAt: "2026-03-10T11:00:00+09:00",
      roleIds: [1],
      roleName: "최종관리자",
    },
    {
      userId: 1,
      loginId: "admin",
      username: "관리자",
      email: "admin@example.com",
      active: true,
      accountNonLocked: true,
      failedLoginAttempts: 0,
      lastPasswordChangeDate: "2026-03-15T08:00:00+09:00",
      lastLoginDate: "2026-03-17T09:00:00+09:00",
      createdAt: "2026-01-05T10:00:00+09:00",
      updatedAt: "2026-03-16T12:00:00+09:00",
      roleIds: [1],
      roleName: "최종관리자",
    },
  ]);

  const handleSearchClick = () => {
    setSearchKeyword(keywordInput.trim());
  };

  const filtered = useMemo(() => {
    return list.filter((row) => {
      if (typeFilter === "active" && !row.active) return false;
      if (typeFilter === "inactive" && row.active) return false;
      if (!searchKeyword) return true;
      const k = searchKeyword.toLowerCase();
      if (searchField === "loginId") {
        return row.loginId.toLowerCase().includes(k);
      }
      return row.username.toLowerCase().includes(k);
    });
  }, [list, typeFilter, searchField, searchKeyword]);

  const openRegisterModal = () => {
    openModal({
      title: "사용자 등록",
      hideFooter: true,
      wide: true,
      content: (
        <UserRegisterForm
          onCancel={closeModal}
          onSuccess={(formData) => {
            closeModal();
            if (!formData) return;
            const roleName =
              formData.roleIds?.length > 0
                ? formData.roleIds.map((id) => ROLE_ID_TO_NAME[id] || "역할").join(", ")
                : "—";
            const nextId = Math.max(0, ...list.map((u) => u.userId)) + 1;
            const now = new Date().toISOString();
            setList((prev) => [
              {
                userId: nextId,
                loginId: formData.loginId,
                username: formData.username,
                email: formData.email || "",
                active: formData.active !== false,
                accountNonLocked: true,
                failedLoginAttempts: 0,
                lastPasswordChangeDate: now,
                lastLoginDate: null,
                createdAt: now,
                updatedAt: now,
                roleIds: formData.roleIds?.length ? [...formData.roleIds] : [],
                roleName,
              },
              ...prev,
            ]);
          }}
        />
      ),
    });
  };

  const openEditModal = (row) => {
    openModal({
      title: "사용자 수정",
      hideFooter: true,
      wide: true,
      content: (
        <UserEditModalForm
          user={row}
          onCancel={closeModal}
          onSave={(payload) => {
            const roleName =
              payload.roleIds?.length > 0
                ? payload.roleIds.map((id) => ROLE_ID_TO_NAME[id] || "역할").join(", ")
                : "—";
            const now = new Date().toISOString();
            setList((prev) =>
              prev.map((u) =>
                u.userId === payload.userId
                  ? {
                      ...u,
                      username: payload.username,
                      email: payload.email ?? "",
                      active: payload.active,
                      accountNonLocked: payload.accountNonLocked,
                      failedLoginAttempts: payload.failedLoginAttempts,
                      roleIds: payload.roleIds ? [...payload.roleIds] : u.roleIds,
                      roleName,
                      lastPasswordChangeDate: payload.password
                        ? now
                        : u.lastPasswordChangeDate,
                      updatedAt: now,
                    }
                  : u
              )
            );
            closeModal();
          }}
        />
      ),
    });
  };

  const openDeleteModal = (row) => {
    openModal({
      title: "삭제 확인",
      content: `${row.username} (${row.loginId}) 사용자를 삭제할까요?`,
      onConfirm: () => {
        setList((prev) => prev.filter((u) => u.userId !== row.userId));
      },
    });
  };

  return (
    <AdminPageTemplate title="사용자 관리" description="">
      <Toolbar>
        <FilterGroup>
          <Select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
            <option value="all">전체</option>
            <option value="active">활성</option>
            <option value="inactive">비활성</option>
          </Select>
          <Select value={searchField} onChange={(e) => setSearchField(e.target.value)}>
            <option value="loginId">아이디</option>
            <option value="username">이름</option>
          </Select>
          <SearchInput
            placeholder="검색명을 입력하세요"
            value={keywordInput}
            onChange={(e) => setKeywordInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearchClick()}
          />
          <SearchButton type="button" onClick={handleSearchClick}>
            검색
          </SearchButton>
        </FilterGroup>
        <RegisterButton type="button" onClick={openRegisterModal}>
          사용자 등록
        </RegisterButton>
      </Toolbar>

      <TableWrap>
        <Table>
          <thead>
            <tr>
              <Th>번호</Th>
              <Th>아이디</Th>
              <Th>이름</Th>
              <Th>그룹명</Th>
              <Th $center>관리</Th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <Td colSpan={5}>조건에 맞는 사용자가 없습니다.</Td>
              </tr>
            ) : (
              filtered.map((row) => (
                <tr key={row.userId}>
                  <Td>{row.userId}</Td>
                  <Td>{row.loginId}</Td>
                  <Td>{row.username}</Td>
                  <Td>{row.roleName}</Td>
                  <Td $center>
                    <EditBtn type="button" onClick={() => openEditModal(row)}>
                      수정
                    </EditBtn>
                    <DeleteBtn type="button" onClick={() => openDeleteModal(row)}>
                      삭제
                    </DeleteBtn>
                  </Td>
                </tr>
              ))
            )}
          </tbody>
        </Table>
      </TableWrap>
    </AdminPageTemplate>
  );
};

const Toolbar = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  min-height: 50px;
  padding: 16px 20px;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e1e2e5;
  border-radius: 5px;
`;

const FilterGroup = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
`;

const Select = styled.select`
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #fff;
  min-width: 100px;
`;

const SearchInput = styled.input`
  padding: 8px 12px;
  width: min(100%, 260px);
  font-size: 14px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  outline: none;
  &::placeholder {
    color: #9ca3af;
  }
`;

const SearchButton = styled.button`
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: #4a6380;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  &:hover {
    background: #3d5370;
  }
`;

const RegisterButton = styled.button`
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: #4a6380;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  white-space: nowrap;
  &:hover {
    background: #3d5370;
  }
`;

const TableWrap = styled.div`
  overflow-x: auto;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
`;

const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
`;

const Th = styled.th`
  padding: 12px 16px;
  text-align: left;
  font-weight: 700;
  color: #111827;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  ${(p) => p.$center && "text-align: center;"}
`;

const Td = styled.td`
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
  color: #111827;
  ${(p) => p.$center && "text-align: center;"}
`;

const EditBtn = styled.button`
  padding: 4px 14px;
  margin-right: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  background: #f59e0b;
  border: 1px solid #d97706;
  border-radius: 4px;
  cursor: pointer;
  &:hover {
    background: #d97706;
  }
`;

const DeleteBtn = styled.button`
  padding: 4px 14px;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
  background: #dc2626;
  border: 1px solid #b91c1c;
  border-radius: 4px;
  cursor: pointer;
  &:hover {
    background: #b91c1c;
  }
`;

export default UserManagePage;
