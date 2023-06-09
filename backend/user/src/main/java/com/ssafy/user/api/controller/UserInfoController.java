package com.ssafy.user.api.controller;

import com.ssafy.user.api.dto.request.UserInfoPostRequest;
import com.ssafy.user.api.dto.response.BaseResponseBody;
import com.ssafy.user.api.service.UserInfoService;
import com.ssafy.user.api.service.UserScenarioListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;import java.util.Map;

@CrossOrigin
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/members")
public class UserInfoController {

  private final UserInfoService userInfoService;
  private final UserScenarioListService userScenarioListService;

  @PostMapping(value = "/user", produces = "application/json")
  public ResponseEntity<? extends BaseResponseBody> createProfile(
      @RequestBody UserInfoPostRequest request) {

    // userInfo 주입
    var userInfo = userInfoService.saveUserInfo(request);

    var userScenario = userScenarioListService.saveUserScenario(request.getUserId(), 1);
    var successMessage = userInfo + " 회원 정보 추가 성공: (ID=" + userScenario + ")";

    return ResponseEntity
        .ok()
        .body(new BaseResponseBody<>(200, "OK", successMessage));
  }

  @PostMapping(value = "/image", produces = "application/json")
  public ResponseEntity<? extends BaseResponseBody> updateImage(
      @RequestBody UserInfoPostRequest request) { // 프로필 사진 업데이트

    var userInfo = userInfoService.updateUserImage(request);

    return ResponseEntity
        .ok()
        .body(new BaseResponseBody<>(200, "OK", userInfo));
  }

  @GetMapping
  public ResponseEntity<? extends BaseResponseBody> getUserInfo(HttpServletRequest request) {
    String userId = (String)request.getAttribute("userId");
    var userInfo = userInfoService.findUserInfoByUserId(userId);

    return ResponseEntity
        .ok()
        .body(new BaseResponseBody<>(200, "OK", userInfo));
  }

  @GetMapping("/scenarios")
  public ResponseEntity<? extends BaseResponseBody> getAllUserScenarios(HttpServletRequest request) {

    String userId = (String)request.getAttribute("userId");
    var userScenarioList = userScenarioListService.findAllScenariosByUserId(userId);

    return ResponseEntity
        .ok()
        .body(new BaseResponseBody<>(200, "OK", userScenarioList));
  }

  @PatchMapping
  public ResponseEntity<? extends BaseResponseBody> updateUserKey(
      HttpServletRequest request,
      @RequestBody Map<String, Object> param) {

    String userId = (String)request.getAttribute("userId");
    Integer keyAmount = (Integer)param.get("keyAmount");

    var userinfo = userInfoService.updateKeyAmount(userId, keyAmount);

    var successMessage = "유저 키 수량 변경 성공: (ID=" + userinfo.getUserId()
        + " : " + userinfo.getCoinAMount() + ")";

    return ResponseEntity
        .ok()
        .body(new BaseResponseBody<>(200, "OK", successMessage));

  }

  @PostMapping
  public ResponseEntity<? extends BaseResponseBody> insertScenario(
      HttpServletRequest request,
      @RequestBody Map<String, Object> param) {

    String userId = (String)request.getAttribute("userId");
    Integer scenarioId = (Integer)param.get("scenarioId");
    var userScenario = userScenarioListService.saveUserScenario(userId, scenarioId);

    var successMessage = "시나리오 추가 성공: (ID=" + userScenario + ")";

    return ResponseEntity
        .ok()
        .body(new BaseResponseBody<>(201, "Created", successMessage));
  }

}
