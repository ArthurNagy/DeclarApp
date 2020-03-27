package com.arthurnagy.staysafe.feature.util

inline fun consume(block: () -> Unit) = true.also { block() }