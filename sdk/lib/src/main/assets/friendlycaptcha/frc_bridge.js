/*!
 * Copyright (c) Friendly Captcha GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

// Using this instead of the browser built-in URLSearchParams we can save shipping a 11kb polyfill.
// returns an object like `{a: "3", b: "bla"}`
function parseQuery(queryString) {
  var out = {}
  queryString
    .replace(/^\?/, "")
    .split("&")
    .forEach(function(kv) {
        if (kv.length === 0) return;
        const k = kv.split("=");
        out[decodeURIComponent(k[0])] = decodeURIComponent(k[1] || "");
    });
  return out;
}

var fp = parseQuery(window.location.search);
console.debug("Query parameters", JSON.stringify(fp));

var handle;

// Called by the Android app. The message is a JSON string that is URL encoded.
function receiveMessage(msg) {
    msg = decodeURIComponent(msg);
    var m;
    try {
      m = JSON.parse(msg);
    }
    catch (e) {
        console.error("Error parsing JSON:", msg);
    }

    if (m.type === "frc:widget.start") {
        handle.start();
    } else if (m.type === "frc:widget.reset") {
        handle.reset();
    }
}

function sendMessage(msg) {
    window.Android.receiveMessage(JSON.stringify(msg));
}

function main() {
    handle = frcaptcha.createWidget({
        element: document.getElementById("mount"),
        apiEndpoint: fp.endpoint,
        sitekey: fp.sitekey,
        theme: fp.theme,
        lang: fp.language,
    });

    handle.addEventListener("frc:widget.statechange", function(event) {
        sendMessage({type: "frc:widget.statechange", detail: event.detail});
    });
    handle.addEventListener("frc:widget.complete", function(event) {
            sendMessage({type: "frc:widget.complete", detail: event.detail});
        });
    handle.addEventListener("frc:widget.error", function(event) {
        sendMessage({type: "frc:widget.error", detail: event.detail});
    });
    handle.addEventListener("frc:widget.expire", function(event) {
        sendMessage({type: "frc:widget.expire", detail: event.detail});
    });

    sendMessage({type: "ready", "id": handle.id});
    console.debug("Bridge ready");
}

if (document.readyState !== "loading") {
    main();
} else {
    document.addEventListener("DOMContentLoaded", main);
}
