/*!
 * Copyright (c) Friendly Captcha GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.friendlycaptcha.android.sdk

/**
 * Constants for the different states of the FriendlyCaptcha widget.
 * One of "init" | "reset" | "unactivated" | "activating" | "activated" | "requesting" | "solving" | "verifying" | "completed" | "expired" | "error" | "destroyed";
 */
class FriendlyCaptchaWidgetState {
        companion object {
            /**
             * The widget is being initialized, it was probably just created.
             */
            const val INIT = "init"

            /**
             * The widget was just reset - it will be `ready` again soon.
             */
            const val RESET = "reset"

            /**
             * The widget is not yet activated. This means the widget is ready to be triggered so
             * it can start solving (in the background). It can be triggered by calling `start()`,
             * or by the user clicking the widget.
             */
            const val UNACTIVATED = "unactivated"

            /**
             * The widget is being activated. This means the widget is talking to the server to
             * request a challenge.
             */
            const val ACTIVATING = "activating"

            /**
             * The widget is activated and awaiting user interaction in the form of a click
             * to continue (if the widget is in `interactive` mode).
             */
            const val ACTIVATED = "activated"

            /**
             * The widget is requesting the final challenge from the server.
             */
            const val REQUESTING = "requesting"

            /**
             * The widget is solving the challenge.
             */
            const val SOLVING = "solving"

            /**
             * The widget is verifying the solution, which means it is talking to the server to
             * receive the final verification response - which is what you will need to send
             * to your own server to verify.
             */
            const val VERIFYING = "verifying"
            /**
             * The widget has completed the challenge solving process.
             */
            const val COMPLETED = "completed"

            /**
             * The widget has expired. This can happen if the user has waited a long time without
             * using the captcha response. The user can click the widget to start again.
             */
            const val EXPIRED = "expired"

            /**
             * The widget has encountered an error.
             */
            const val ERROR = "error"

            /**
             * The widget has been destroyed. This means it is no longer usable.
             */
            const val DESTROYED = "destroyed"
        }
}