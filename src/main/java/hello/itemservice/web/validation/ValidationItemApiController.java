package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * API의 경우 3가지 경우를 생각해야함
 * 1. 성공로직
 * 2. Json변환 실패: json변환실패해서 요청객체를 못만들었을 때
 * 3. 검증 실패: json변환은 성공했으나, 검증이 실패 했을 때
 */
@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    @PostMapping("/add")
    public Object add(@Validated @RequestBody ItemSaveForm form, BindingResult result) {
        log.info("api 호출");
        if (form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                result.addError(new ObjectError("form",
                        "가격 * 수량의 합은 10000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        if (result.hasErrors()) {
//            log.info("검증 오류 발생: errors={}", result);
//            return result.getAllErrors();

            Map<String, String> responseMap = new HashMap<>();
            for (ObjectError error : result.getAllErrors()) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    responseMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    continue;
                }
                responseMap.put(error.getObjectName(), error.getDefaultMessage());
            }
            return responseMap;
        }

        log.info("성공 로직 실행");
        return form;
    }
}
