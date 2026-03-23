import Modal from "@/components/modal/Modal";
import { createContext, useCallback, useContext, useState } from "react";

const ModalContext = createContext(null);

export const ModalProvider = ({children}) => {
    const [modalConfig, setModalConfig] = useState({
        isOpen: false,
        title: "",
        content: "",
        onConfirm: null,
        hideFooter: false,
        wide: false,
    })

    // 모달 열기 함수
    // hideFooter: true → 폼 등에서 본문만 표시(취소/확인 푸터 숨김)
    // wide: true → 넓은 모달(등록/수정 폼용)
    const openModal = useCallback(
        ({ title, content, onConfirm, hideFooter = false, wide = false }) => {
            setModalConfig({
                isOpen: true,
                title,
                content,
                onConfirm: hideFooter ? null : onConfirm,
                hideFooter,
                wide,
            })
        },
        []
    )

    const closeModal = useCallback(() => {
        setModalConfig((prev) => ({
            ...prev,
            isOpen: false,
            hideFooter: false,
            wide: false,
        }))
    }, [])

    return (
        <ModalContext.Provider value={{ openModal, closeModal }}>
            {children}
            {modalConfig.isOpen && (
                <Modal
                config={modalConfig}
                onClose={closeModal}
                />
            )}
        </ModalContext.Provider>
    );
}

export const useModal = () => useContext(ModalContext);
