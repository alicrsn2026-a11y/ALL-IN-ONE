package com.allinone.app.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.allinone.app.data.AppContainer

/**
 * Builds a ViewModelProvider.Factory that constructs a ViewModel from the
 * app container. Usage:
 *   viewModel(factory = containerFactory(container) { SpaceViewModel(it) })
 */
inline fun <reified T : ViewModel> containerFactory(
    container: AppContainer,
    crossinline create: (AppContainer) -> T
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        T::class.java.let { create(container) }
    }
}
