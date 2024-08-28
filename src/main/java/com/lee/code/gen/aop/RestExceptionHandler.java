package com.lee.code.gen.aop;

import com.lee.code.gen.common.CodeEnum;
import com.lee.code.gen.common.R;
import com.lee.code.gen.common.ValidationError;
import com.lee.code.gen.dto.common.ValidationErrorResponseDto;
import com.lee.code.gen.exception.BizException;
import com.lee.code.gen.exception.JsonSchemaValidationException;
import com.lee.code.gen.exception.RequestNotPermitted;
import com.lee.code.gen.exception.ServerException;
import com.lee.code.gen.util.MessageUtil;
import com.lee.code.gen.util.WebUtil;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class RestExceptionHandler {

    private final MessageUtil messageUtil;
    private static final String BIZ_LOG_MSG = "MsgCode: {0}, Msg: {1}";

    /**
     * 参数校验错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public R<ValidationErrorResponseDto> exception(MethodArgumentNotValidException e) {
        log.debug(e.getMessage(), e);
        ValidationErrorResponseDto responseDto = new ValidationErrorResponseDto();
        BindingResult bindingResult = e.getBindingResult();
        responseDto.setErrors(bindingResult.getFieldErrors().stream().map(o -> {
            ValidationError error = new ValidationError();
            error.setField(o.getField());
            error.setErrorMsg(o.getDefaultMessage());

            return error;
        }).toList());
        return R.optFail(CodeEnum.RCD20002, responseDto);
    }

    /**
     * 参数校验错误
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public R<ValidationErrorResponseDto> exception(HandlerMethodValidationException e) {
        log.debug(e.getMessage(), e);
        ValidationErrorResponseDto responseDto = new ValidationErrorResponseDto();
        responseDto.setErrors(e.getAllValidationResults().stream().map(o -> {
            String paramName = o.getMethodParameter().getParameterName();
            return o.getResolvableErrors().stream().map(x -> {
                ValidationError error = new ValidationError();
                error.setField(paramName);
                error.setErrorMsg(x.getDefaultMessage());

                return error;
            }).toList();
        }).flatMap(Collection::stream).toList());
        return R.optFail(CodeEnum.RCD20002, responseDto);
    }

    /**
     * 参数类型不匹配错误
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public R<ValidationErrorResponseDto> exception(MethodArgumentTypeMismatchException e) {
        log.debug(e.getMessage(), e);
        ValidationErrorResponseDto responseDto = new ValidationErrorResponseDto();
        ValidationError error = new ValidationError();
        error.setField(e.getName());
        error.setErrorMsg(e.getMessage());
        responseDto.setErrors(List.of(error));
        return R.optFail(CodeEnum.RCD20002, responseDto);
    }

    /**
     * Query 参数不匹配错误
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public R<ValidationErrorResponseDto> exception(MissingServletRequestParameterException e) {
        log.debug(e.getMessage(), e);
        ValidationErrorResponseDto responseDto = new ValidationErrorResponseDto();
        ValidationError error = new ValidationError();
        error.setField(e.getParameterName());
        error.setErrorMsg(e.getMessage());
        responseDto.setErrors(List.of(error));
        return R.optFail(CodeEnum.RCD20002, responseDto);
    }

    /**
     * Json Schema 校验错误
     */
    @ExceptionHandler(JsonSchemaValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public R<ValidationErrorResponseDto> exception(JsonSchemaValidationException e) {
        log.debug(e.getMessage(), e);
        ValidationErrorResponseDto responseDto = new ValidationErrorResponseDto();
        responseDto.setErrors(e.getErrors());
        return R.optFail(CodeEnum.RCD20004, responseDto);
    }

    /**
     * 404
     */
    @ExceptionHandler({NoResourceFoundException.class, HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Object> exception404(Exception e, HttpServletRequest request) {
        log.debug(e.getMessage(), e);
        return R.optFail(CodeEnum.RCD40000.getCode(), request.getRequestURI());
    }

    /**
     * 系统内部错误
     */
    @ExceptionHandler({Exception.class, ServerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> exception(Exception e) {
        log.error(e.getMessage(), e);
        return R.internalFail();
    }

    /**
     * 远程调用-4xx
     */
    @ExceptionHandler({FeignException.BadRequest.class, FeignException.NotFound.class, FeignException.UnprocessableEntity.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<String> exception(FeignException e) {
        log.warn(e.getMessage(), e);
        Optional<ByteBuffer> responseBody = e.responseBody();
        return responseBody
                .map(byteBuffer -> R.optFail(StandardCharsets.UTF_8.decode(byteBuffer).toString()))
                .orElseGet(() -> R.optFail(CodeEnum.RCD20001.getCode()));
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> exception(BizException e) {
        String msgCode = String.valueOf(e.getMsgCode());
        log.info(MessageFormat.format(BIZ_LOG_MSG, msgCode, messageUtil.getMsg(msgCode, e.getParams())), e);
        return R.optFail(e.getMsgCode(), e.getParams());
    }

    /**
     * 频率限制异常
     */
    @ExceptionHandler(RequestNotPermitted.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public R<Object> exception(RequestNotPermitted e, HttpServletRequest request) {
        String ip = WebUtil.getRemoteIP();
        log.warn("API: {}[{}], IP: {}, 实行频率限制（{}次/{}{}）", request.getMethod(), request.getRequestURI(), ip, e.getRate(), e.getInterval(), e.getUnit().name());
        return R.optFail(CodeEnum.RCD20003.getCode(), ip);
    }

    /**
     * 拒绝访问异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Object> exception(AccessDeniedException e) {
        log.debug(e.getMessage(), e);
        return R.optFail(CodeEnum.RCD20005.getCode());
    }
}
