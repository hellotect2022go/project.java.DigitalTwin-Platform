import Modal from "@/components/common/Modal";
import { createContext, useContext, useState } from "react";

const ModalContext = createContext(null);

export const ModalProvider = ({children}) => {
    const [modalConfig, setModalConfig] = useState({
        isOpen:false,
        title:'',
        content:'',
        onConfirm:null
    })

    // 모달 열기 함수 
    const openModal = ({title, content, onConfirm}) =>{
        setModalConfig({
            isOpen:true,
            title,
            content,
            onConfirm,
        })
    }

    const closeModal = () => {
        setModalConfig((prev) => ({ ...prev, isOpen: false }));
    };

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
