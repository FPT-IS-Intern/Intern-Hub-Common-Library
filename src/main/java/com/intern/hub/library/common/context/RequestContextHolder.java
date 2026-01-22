package com.intern.hub.library.common.context;

/**
 * Thread-safe holder for the current {@link RequestContext} using Java's {@link ScopedValue}.
 * <p>
 * This class provides access to the request context that has been bound to the current scope.
 * It uses {@link ScopedValue} (introduced in Java 21) which provides a more efficient
 * alternative to {@link ThreadLocal} for virtual threads.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 * // Binding the context (typically done in a filter or interceptor)
 * RequestContext requestContext = new RequestContext(traceId, requestId, startTime, source);
 * ScopedValue.runWhere(RequestContextHolder.REQUEST_CONTEXT, requestContext, () -> {
 *     // The context is available within this scope
 *     RequestContext ctx = RequestContextHolder.get();
 * });
 *
 * // Retrieving the context
 * RequestContext context = RequestContextHolder.get();
 * }</pre>
 *
 * @see RequestContext
 * @see ScopedValue
 */
public final class RequestContextHolder {

  /**
   * The scoped value holding the current {@link RequestContext}.
   * <p>
   * This should be bound using {@code ScopedValue.runWhere()} or similar methods
   * at the beginning of a request scope.
   * </p>
   */
  public final static ScopedValue<RequestContext> REQUEST_CONTEXT = ScopedValue.newInstance();

  private RequestContextHolder() {
  }

  /**
   * Get the current RequestContext from the scoped value.
   *
   * @return the current RequestContext
   * @throws IllegalStateException if no RequestContext has been bound to the current scope
   */
  public static RequestContext get() {
    if (!REQUEST_CONTEXT.isBound()) {
      throw new IllegalStateException("No RequestContext is bound to the current scope");
    }
    return REQUEST_CONTEXT.get();
  }

}
