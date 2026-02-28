package com.intern.hub.library.common.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestContext {

  private final String requestId;
  private final Map<String, Object> requestAttributes;

  public RequestContext(String requestId) {
    this.requestId = requestId;
    this.requestAttributes = new ConcurrentHashMap<>(4);
  }

  public String requestId() {
    return requestId;
  }

  public void putAttribute(String key, Object value) {
    requestAttributes.put(key, value);
  }

  public Object getAttribute(String key) {
    return requestAttributes.get(key);
  }

}
