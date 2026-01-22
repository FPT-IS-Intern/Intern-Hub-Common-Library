package com.intern.hub.library.common.context;

import com.intern.hub.library.common.dto.Scope;

import java.util.Map;

/**
 * Immutable record representing the authentication context for the current user.
 * <p>
 * This record holds the authenticated user's ID and their permissions map,
 * which is used by the {@link com.intern.hub.library.common.annotation.aspect.HasPermissionAspect}
 * for permission checking.
 * </p>
 *
 * <p><b>Permissions Map:</b></p>
 * <ul>
 *   <li>Key format: "{@code resource:action}" (e.g., "user:read", "order:delete")</li>
 *   <li>Value: {@link Scope} indicating the access level (OWN, TENANT, or ALL)</li>
 * </ul>
 *
 * @param userId      the unique identifier of the authenticated user
 * @param permissions a map of permission keys to their corresponding scopes
 * @see AuthContextHolder
 * @see Scope
 */
public record AuthContext(
    Long userId,
    Map<String, Scope> permissions
) {
}
