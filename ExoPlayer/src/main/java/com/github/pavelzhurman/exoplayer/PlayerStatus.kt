package com.github.pavelzhurman.exoplayer


sealed class PlayerStatus() {
    class Other() : PlayerStatus()
    class Playing() : PlayerStatus()
    class Paused() : PlayerStatus()
    class Cancelled() : PlayerStatus()
    class Error() : PlayerStatus()
    class Buffering() : PlayerStatus()
    class Idle() : PlayerStatus()
}