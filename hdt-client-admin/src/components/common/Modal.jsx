import styled from "styled-components";
import { SvgIcons } from "./Icon";

const Modal = ({config , onClose }) => {
  const { isOpen, title, content, onConfirm } = config;
  if (!isOpen) return null;

  return (
    <Backdrop onClick={onClose}>
      <ModalContainer onClick={(e) => e.stopPropagation()}>
        <div style={{display:"flex", justifyContent:"flex-end"}} onClick={onClose}><SvgIcons.Cross/></div>
        <ModalHeader>{title}</ModalHeader>
        <ModalBody>{content}</ModalBody>
        <ModalFooter>
          <Button onClick={onClose}>취소</Button>
          <Button $primary onClick={() => {
            if (onConfirm) onConfirm();
            onClose();
          }}>확인</Button>
        </ModalFooter>
      </ModalContainer>
    </Backdrop>
  );
};

export default Modal;

// --- Styled Components (기존과 동일) ---
const Backdrop = styled.div`
  position: fixed; 
  inset: 0; 
  background: rgba(0, 0, 0, 0.5);
  display: flex; 
  justify-content: center; 
  align-items: center; 
  z-index: 9999;
`;
const ModalContainer = styled.div`
  background: white; 
  padding: 16px 20px 16px 20px; 
  border-radius: 8px; 
  min-width: 320px;
`;
const ModalHeader = styled.h2` 
    display: flex;
    justify-content: center;
    margin: 0 0 16px 0; 
    font-size: 1.25rem; 
`;
const ModalBody = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: 24px; 
  color: #4b5563; 
  line-height: 1.5;
`;
const ModalFooter = styled.div`
 display: flex; 
 justify-content: center; 
 gap: 8px; 
`;
const Button = styled.button`
  padding: 8px 16px; 
  border-radius: 4px; 
  border: none; 
  width: 137px;
  height: 36px;
  cursor: pointer;
  background: ${props => props.$primary ? '#009591' : '#FFFFFF'};
  color: ${props => props.$primary ? 'white' : 'black'};
  border: ${props => props.$primary ? '1px solid #009591' : '1px solid #CECECE'};
`;