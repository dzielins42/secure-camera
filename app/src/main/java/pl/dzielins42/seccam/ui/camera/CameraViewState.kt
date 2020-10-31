package pl.dzielins42.seccam.ui.camera

import pl.dzielins42.bloxyz.lce.LceViewState

enum class CameraViewStateContent {
    Initialized, Completed, PermissionsNotGranted
}

typealias CameraViewState = LceViewState<CameraViewStateContent>