package com.gnest.remember.feature.permissionprovider.navigation

import android.os.Bundle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.gnest.remember.core.common.extensions.getParcelableSafe
import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.core.permission.Permission
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class PermissionScreen(private val permission: Permission) : Screen {

    override val route get() = "$permissionRoute/${Json.encodeToString(permission)}"

    private class PermissionNavType : NavType<Permission>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): Permission? {
            return bundle.getParcelableSafe(key)
        }

        override fun parseValue(value: String): Permission = Json.decodeFromString(value)

        override fun put(bundle: Bundle, key: String, value: Permission) = bundle.putParcelable(key, value)
    }

    companion object {

        private const val permissionRoute = "permissionRoute"
        const val permissionArg = "Permission"
        const val routePattern = "$permissionRoute/{$permissionArg}"
        val args = listOf(navArgument(permissionArg) { type = PermissionNavType() })

    }
}