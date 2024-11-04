/**
 * By using this instead of the browser built-in URLSearchParams we can save shipping a 11kb polyfill in the polyfilled version.
 * @returns an object like `{a: "3", b: "bla"}`
 */
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
console.log("fp", JSON.stringify(fp));

var handle;

// Called by the Android app.
function receiveMessage(msg) {
    console.log("Received message: ", msg);
    try {
      var m = JSON.parse(msg);
    }
    catch (e) {
        console.log("Error parsing JSON: " + msg);
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

    sendMessage({type: "ready"});
    console.log("Bridge ready");
}

if (document.readyState !== "loading") {
    main();
} else {
    document.addEventListener("DOMContentLoaded", main);
}





