import React, { useEffect, useState } from "react";
import { useHistory } from "react-router";
import { Box, Typography, Modal } from "@mui/material/";
import { Buttontwo } from "components/common/button/ButtonStyle";
const style = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 500,
  bgcolor: "background.paper",
  border: "3px solid #ff8f5c",
  boxShadow: 24,
  outline: "none",
  p: 4,
  borderRadius: 10,
};
/**타이머 후 가위바위보 결과 판단 함수 */
const WinLose = ({
  ghostHandFi,
  userHand,
  timer,
  resetTimer,
  endGameHandler,
  pageChangeHandler,
}) => {
  const [open, setOpen] = useState(false);
  const [winLose, setWinLose] = useState(null);
  const history = useHistory();
  const handleClose = () => {
    setOpen(false);
    if (winLose === "비겼습니다!") {
      //다시 타이머 초기화
      resetTimer();
    } else {
      endGameHandler();
      if (winLose === "ㅠㅠ 아쉽게 졌다") {
        pageChangeHandler(22);
      } else if (winLose === "와아~!! 가위바위보 게임에서 이겼다") {
        pageChangeHandler(23);
      } else {
        //손 인식에 오류가 있는 경우
        resetTimer();
      }
    }
  };
  useEffect(() => {
    if (timer === 0) {
      if (ghostHandFi === "rock" && userHand === "rock") {
        setWinLose("비겼습니다!");
      } else if (ghostHandFi === "scissors" && userHand === "scissors") {
        setWinLose("비겼습니다!");
      } else if (ghostHandFi === "paper" && userHand === "paper") {
        setWinLose("비겼습니다!");
      } else if (ghostHandFi === "rock" && userHand === "scissors") {
        setWinLose("ㅠㅠ 아쉽게 졌다");
      } else if (ghostHandFi === "scissors" && userHand === "paper") {
        setWinLose("ㅠㅠ 아쉽게 졌다");
      } else if (ghostHandFi === "paper" && userHand === "rock") {
        setWinLose("ㅠㅠ 아쉽게 졌다");
      } else if (ghostHandFi === "rock" && userHand === "paper") {
        setWinLose("와아~!! 가위바위보 게임에서 이겼다");
      } else if (ghostHandFi === "scissors" && userHand === "rock") {
        setWinLose("와아~!! 가위바위보 게임에서 이겼다");
      } else if (ghostHandFi === "paper" && userHand === "scissors") {
        setWinLose("와아~!! 가위바위보 게임에서 이겼다");
      } else {
        setWinLose("손을 정확히 카메라에 올려주세요. 다시 해볼게요!");
      }
      setOpen(true);
    }
  }, [timer]);

  return (
    <div>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <Typography
            sx={{
              m: 2,
              fontSize: 30,
              color: "#FF8F5C",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
            id="modal-modal-title"
            variant="h6"
            component="h2"
          >
            {winLose}
          </Typography>
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            {winLose === "비겼습니다!" && (
              <Buttontwo onClick={handleClose}>다시하기</Buttontwo>
            )}
            {winLose === "와아~!! 가위바위보 게임에서 이겼다" && (
              <Buttontwo onClick={handleClose}>확인</Buttontwo>
            )}
            {winLose === "ㅠㅠ 아쉽게 졌다" && (
              <Buttontwo onClick={handleClose}>확인</Buttontwo>
            )}
          </Box>
        </Box>
      </Modal>
    </div>
  );
};

export default WinLose;
