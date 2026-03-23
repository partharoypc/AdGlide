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

var AdGlideConfig.Builder.debugGDPR: Boolean
    get() = false
    set(value) { debugGDPR(value) }

var AdGlideConfig.Builder.enableDebugHUD: Boolean
    get() = false
    set(value) { enableDebugHUD(value) }

var AdGlideConfig.Builder.adResponseTimeout: Int
    get() = 3500
    set(value) { adResponseTimeout(value) }

var AdGlideConfig.Builder.appOpenCooldownMinutes: Int
    get() = 30
    set(value) { appOpenCooldownMinutes(value) }

var AdGlideConfig.Builder.adMobAppId: String
    get() = ""
    set(value) { adMobAppId(value) }

var AdGlideConfig.Builder.adMobInterstitialId: String
    get() = ""
    set(value) { adMobInterstitialId(value) }

var AdGlideConfig.Builder.adMobRewardedId: String
    get() = ""
    set(value) { adMobRewardedId(value) }

var AdGlideConfig.Builder.adMobBannerId: String
    get() = ""
    set(value) { adMobBannerId(value) }

var AdGlideConfig.Builder.adMobNativeId: String
    get() = ""
    set(value) { adMobNativeId(value) }

var AdGlideConfig.Builder.adMobAppOpenId: String
    get() = ""
    set(value) { adMobAppOpenId(value) }

// Add more as needed for other networks...
