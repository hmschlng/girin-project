import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import {
  Container,
  ServiceName,
  GameScreen,
  GameContainer,
  TimerScreen,
  TimerTime,
  RspImg,
} from "components/rsp/RspStyle";

import rock from "assets/image/rock.png";
import scissors from "assets/image/scissors.png";
import paper from "assets/image/paper.png";
import rsp from "assets/image/rsp.gif";

import RspSpinner from "components/rsp/RspSpinner";
import FingerPose from "utils/fingerPose/FingerPose";

const Rsp = () => {
  const isLoad = useSelector((state) => state.game.isLoad);
  const [ghostHand, setGhostHand] = useState(null);
  const [isChange, setIsChange] = useState(true);
  const [timer, setTimer] = useState(3);
  const [showSpinner, setShowSpinner] = useState(false);

  useEffect(() => {
    const randomNum = Math.floor(Math.random() * 3) + 1;

    if (randomNum === 1) {
      setGhostHand(rock);
    } else if (randomNum === 2) {
      setGhostHand(scissors);
    } else if (randomNum === 3) {
      setGhostHand(paper);
    }

    const interval = setInterval(() => {
      setTimer((prevTimer) => prevTimer - 1);
    }, 1000);

    if (timer === 0) {
      clearInterval(interval);
      setIsChange(false);
    }

    return () => clearInterval(interval);
  }, [timer]);

  useEffect(() => {
    if (isLoad) {
      setShowSpinner(false);
    } else {
      const spinnerTimeout = setTimeout(() => {
        setShowSpinner(true);
      }, 500);
      return () => clearTimeout(spinnerTimeout);
    }
  }, [isLoad]);

  return (
    <div>
      <Container>
        <ServiceName>재미있는 가위바위보 게임</ServiceName>
      </Container>

      <RspSpinner showSpinner={showSpinner} />

      <GameContainer showSpinner={showSpinner}>
        <GameScreen>
          {timer === 0 ? (
            <RspImg src={ghostHand} alt="유령의손" />
          ) : (
            <RspImg src={rsp} alt="유령가위바위보" />
          )}
        </GameScreen>

        <TimerScreen>
          <TimerTime>{timer}</TimerTime>
        </TimerScreen>

        <GameScreen>
          <FingerPose />
        </GameScreen>
      </GameContainer>
    </div>
  );
};

export default Rsp;
