package pl.dzielins42.seccam.ui.camera

import pl.dzielins42.seccam.util.LceViewState

sealed class CameraViewStateContent {
    object Completed : CameraViewStateContent()
}

typealias CameraViewState = LceViewState<CameraViewStateContent>