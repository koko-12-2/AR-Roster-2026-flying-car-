package com.roster2026.flyingcar;

/** Shared input and flight state. Values are volatile because GL and UI run on separate threads. */
final class GameState {
    volatile float steer;
    volatile boolean lift;
    volatile boolean turnLeft;
    volatile boolean turnRight;
    volatile boolean reset;
    volatile float speed;
    volatile float altitude;
    volatile float distance;
}
