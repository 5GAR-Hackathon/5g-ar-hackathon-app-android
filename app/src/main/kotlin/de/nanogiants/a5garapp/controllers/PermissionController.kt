package de.nanogiants.a5garapp.controllers

interface PermissionController {

  fun hasPermissions(vararg permissions: String): Boolean

  fun requestPermissions(vararg permissions: String, onGranted: (() -> Unit) = {}, onDenied: () -> Unit = {})
}