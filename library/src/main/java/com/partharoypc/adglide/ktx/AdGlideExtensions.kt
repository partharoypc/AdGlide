package com.partharoypc.adglide.ktx

import com.partharoypc.adglide.AdGlideConfig

/**
 * Kotlin DSL for building AdGlideConfig.
 * Usage:
 * AdGlideConfig.Builder().apply {
 *    enableAds(true)
 *    primaryNetwork(AdGlideNetwork.ADMOB)
 * }.build()
 * 
 * Or with this DSL:
 * adGlideConfig {
 *    enableAds = true
 *    primaryNetwork = AdGlideNetwork.ADMOB
 * }
 */

fun adGlideConfig(block: AdGlideConfig.Builder.() -> Unit): AdGlideConfig {
    return AdGlideConfig.Builder().apply(block).build()
}

// Extension properties for Builder to make it even more DSL-like
var AdGlideConfig.Builder.enableAds: Boolean
    get() = false // Not used
    set(value) { enableAds(value) }

var AdGlideConfig.Builder.primaryNetwork: String
    get() = ""
    set(value) { primaryNetwork(value) }

var AdGlideConfig.Builder.testMode: Boolean
    get() = false
    set(value) { testMode(value) }

var AdGlideConfig.Builder.debug: Boolean
    get() = true
    set(value) { debug(value) }

var AdGlideConfig.Builder.enableGDPR: Boolean
    get() = false
    set(value) { enableGDPR(value) }

var AdGlideConfig.Builder.enableDebugHUD: Boolean
    get() = false
    set(value) { enableDebugHUD(value) }
