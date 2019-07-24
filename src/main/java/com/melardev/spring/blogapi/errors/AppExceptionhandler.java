package com.melardev.spring.blogapi.errors;

import com.melardev.spring.blogapi.dtos.response.base.AppResponse;
import com.melardev.spring.blogapi.dtos.response.base.ErrorResponse;
import com.melardev.spring.blogapi.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.blogapi.errors.exceptions.ResourceNotFoundException;
import com.melardev.spring.blogapi.errors.exceptions.UnexpectedStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class AppExceptionhandler extends ResponseEntityExceptionHandler {
    // TODO: redirect to custom page

    /*
    @ExceptionHandler({ResourceNotFoundException.class})
    public RedirectView handleNotFound(RuntimeException ex, HttpServletRequest request) {
        String redirect = "/errors/404";
        RedirectView rw = new RedirectView(redirect);
        rw.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // you might not need this
        FlashMap outputFlashMap = RequestContextUtils.getOutputFlashMap(request);
        if (outputFlashMap != null) {
            outputFlashMap.put("message", ex.getLocalizedMessage());
        }
        return rw;
    }*/

    @ExceptionHandler({ResourceNotFoundException.class})
    public String resourceNotFound(ResourceNotFoundException e, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("messages", e.getMessage());
        return "redirect:/errors/404";
    }

    @ExceptionHandler({UnexpectedStateException.class})
    public String unexpectedState(ResourceNotFoundException e, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("messages", e.getMessage());
        return "redirect:/errors/unexpected";
    }

    @ExceptionHandler({PermissionDeniedException.class})
    public ResponseEntity<AppResponse> permissionDenied(PermissionDeniedException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    /*
    @ExceptionHandler(Exception.class)
    public String exception(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("messages", ex.getLocalizedMessage());
        return "errors/5xx";
    }
*/
}