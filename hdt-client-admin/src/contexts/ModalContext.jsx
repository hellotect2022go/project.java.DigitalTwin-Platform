import Modal from "@/components/common/Modal";
import { createContext, useCallback, useContext, useState } from "react";

const ModalContext = createContext(null);

export const ModalProvider = ({children}) => {
    const [modalConfig, setModalConfig] = useState({
        isOpen:false,
        title:'',
        content:'',
        onConfirm:null
    })

    // 모달 열기 함수 
    // provider 는 component 상태 값이 바뀔때마다 함수를 초기화 하고 다시 메모리에 할당 
    // useCallback 을 사용하여 함수 메모리를 메모제이션 해서 사용함 
    const openModal = useCallback(({title, content, onConfirm}) =>{
        setModalConfig({
            isOpen:true,
            title,
            content,
            onConfirm,
        })
    },[])

    const closeModal = useCallback(() => {
        setModalConfig((prev) => ({ ...prev, isOpen: false }));
    },[]);

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
