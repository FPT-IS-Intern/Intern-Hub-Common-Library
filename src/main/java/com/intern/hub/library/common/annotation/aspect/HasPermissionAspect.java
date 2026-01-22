package com.intern.hub.library.common.annotation.aspect;

import com.intern.hub.library.common.annotation.HasPermission;
import com.intern.hub.library.common.context.AuthContext;
import com.intern.hub.library.common.context.AuthContextHolder;
import com.intern.hub.library.common.dto.Scope;
import com.intern.hub.library.common.exception.ExceptionConstant;
import com.intern.hub.library.common.exception.ForbiddenException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Aspect that enforces permission checking for methods annotated with {@link HasPermission}.
 * <p>
 * This aspect intercepts method calls annotated with {@code @HasPermission} and verifies
 * that the current user (obtained from {@link AuthContextHolder}) has sufficient permissions
 * to execute the method.
 * </p>
 *
 * <p><b>Permission checking logic:</b></p>
 * <ol>
 *   <li>Retrieves the current {@link AuthContext} from {@link AuthContextHolder}</li>
 *   <li>Constructs the permission key as "{@code resource:action}"</li>
 *   <li>Compares the user's scope with the required scope</li>
 *   <li>Throws {@link ForbiddenException} if the user lacks permission</li>
 * </ol>
 *
 * @see HasPermission
 * @see AuthContext
 * @see AuthContextHolder
 * @see ForbiddenException
 */
@Component
@Aspect
public class HasPermissionAspect {

  /**
   * Around advice that checks permissions before method execution.
   * <p>
   * If the user lacks the required permission, a {@link ForbiddenException} is thrown.
   * Otherwise, the method proceeds normally.
   * </p>
   *
   * @param pjp the proceeding join point representing the intercepted method
   * @return the result of the method execution
   * @throws ForbiddenException if the user is not authenticated or lacks the required permission
   */
  @Around("@annotation(com.intern.hub.library.common.annotation.HasPermission)")
  public Object hasPermissionAdvice(ProceedingJoinPoint pjp) {
    MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
    HasPermission hasPermission = methodSignature.getMethod().getAnnotation(HasPermission.class);

    AuthContext authContext = AuthContextHolder.get().orElse(null);
    if(authContext == null) {
      throw new ForbiddenException(ExceptionConstant.FORBIDDEN_DEFAULT_CODE);
    }

    Map<String, Scope> permissions = authContext.permissions();
    Scope userScope = permissions.get(hasPermission.resource() + ":" + hasPermission.action());
    if(userScope == null || hasPermission.scope().getValue() > userScope.getValue()) {
      throw new ForbiddenException(ExceptionConstant.FORBIDDEN_DEFAULT_CODE);
    }
    return next(pjp);
  }

  /**
   * Proceeds with the method execution.
   *
   * @param pjp the proceeding join point
   * @return the result of the method execution
   * @throws RuntimeException if the method throws an exception
   */
  private Object next(ProceedingJoinPoint pjp) {
    try {
      return pjp.proceed();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

}
